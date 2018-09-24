package es.redmic.test.vesselsview.integration.controller;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.testutils.documentation.DocumentationViewBaseTest;
import es.redmic.vesselsview.VesselsViewApplication;
import es.redmic.vesselsview.model.vessel.Vessel;
import es.redmic.vesselsview.model.vesseltracking.VesselTracking;
import es.redmic.vesselsview.model.vesseltracking.VesselTrackingProperties;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import es.redmic.vesselsview.repository.vesseltracking.VesselTrackingESRepository;

@SpringBootTest(classes = { VesselsViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "schema.registry.port=18181" })
@DirtiesContext
public class VesselTrackingControllerTest extends DocumentationViewBaseTest {

	public final static String PREFIX = "vesseltracking-mmsi-tstamp-", MMSI = "1234", USER = "1";

	@Value("${documentation.VESSELTRACKING_HOST}")
	private String VESSELTRACKING_HOST;

	@Value("${controller.mapping.vesseltracking}")
	private String VESSELTRACKING_PATH;

	@Value("${vesseltracking.activity.id}")
	private String ACTIVITY_ID;

	String url;

	@Autowired
	VesselTrackingESRepository repository;

	VesselTracking vesselTracking = new VesselTracking();

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1);

	@PostConstruct
	public void CreateVesselTrackingFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@Override
	@Before
	public void setUp() {

		vesselTracking = new VesselTracking();

		Vessel vessel = new Vessel();
		vessel.setId("vessel-mmsi-" + MMSI);
		vessel.setMmsi(Integer.valueOf(MMSI));
		vessel.setName("Avatar");
		vessel.setImo(1234);
		vessel.setBeam(30.2);
		vessel.setLength(230.5);
		vessel.setCallSign("23e2");
		vessel.setInserted(DateTime.now());
		vessel.setUpdated(DateTime.now());

		VesselType vesselType = new VesselType();
		vesselType.setCode("70");
		vesselType.setId("vesseltype-code-" + "70");
		vesselType.setName("Cargo, all ships of this type");
		vesselType.setName_en("Cargo, all ships of this type");
		vessel.setType(vesselType);

		vesselTracking.setId(PREFIX + MMSI + "-" + new DateTime().getMillis());
		vesselTracking.setUuid(UUID.randomUUID().toString());

		Point geometry = JTSFactoryFinder.getGeometryFactory().createPoint(new Coordinate(44.56433, 37.94388));
		vesselTracking.setGeometry(geometry);

		VesselTrackingProperties properties = new VesselTrackingProperties();
		vesselTracking.setProperties(properties);

		properties.setActivity("r.1.8.22");
		properties.setVessel(vessel);
		properties.setDate(DateTime.now());

		properties.setCog(23.3);
		properties.setSog(23.3);
		properties.setHeading(12);
		properties.setNavStat(33);
		properties.setDest("Santa Cruz de Tenerife");
		properties.setEta("00:00 00:00");

		properties.setInserted(DateTime.now());
		properties.setUpdated(DateTime.now());

		repository.save(vesselTracking);

		url = VESSELTRACKING_PATH.replace("{activityId}", ACTIVITY_ID);

		// @formatter:off

		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain)
				.apply(documentationConfiguration(this.restDocumentation)
						.uris().withScheme(SCHEME).withHost(VESSELTRACKING_HOST.replace("{activityId}", ACTIVITY_ID))
							.withPort(PORT))
				.alwaysDo(this.document).build();

		// @formatter:on
	}

	@After
	public void clean() {
		repository.delete(vesselTracking.getId());
	}

	@Test
	public void getVessel_Return200_WhenItemExist() throws Exception {

		// @formatter:off
		
		this.mockMvc.perform(get(url + "/" + vesselTracking.getId()).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body.id", is(vesselTracking.getId())));
		
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void searchVesselTrackingPost_Return200_WhenSearchIsCorrect() throws Exception {

		DataQueryDTO dataQuery = new DataQueryDTO();
		dataQuery.setSize(1);

		// Se elimina accessibilityIds ya que no está permitido para usuarios
		// no registrados
		HashMap<String, Object> query = mapper.convertValue(dataQuery, HashMap.class);
		query.remove("accessibilityIds");

		// @formatter:off
		
		this.mockMvc
				.perform(post(url + "/_search").content(mapper.writeValueAsString(query))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.features", notNullValue()))
				.andExpect(jsonPath("$.body.features[0]", notNullValue()))
				.andExpect(jsonPath("$.body.features.length()", is(1)))
					.andDo(getDataQueryFieldsDescriptor());
		
		// @formatter:on
	}

	@Test
	public void mgetVesselTracking_Return200_WhenVesselTrackingExists() throws Exception {

		MgetDTO mgetQuery = new MgetDTO();
		mgetQuery.setIds(Arrays.asList(vesselTracking.getId()));
		mgetQuery.setFields(Arrays.asList("id"));

		// @formatter:off
		
		this.mockMvc
			.perform(post(url + "/_mget").content(mapper.writeValueAsString(mgetQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.features", notNullValue()))
				.andExpect(jsonPath("$.body.features[0]", notNullValue()))
				.andExpect(jsonPath("$.body.features[0].id", is(vesselTracking.getId())))
				.andExpect(jsonPath("$.body.features.length()", is(1)))
					.andDo(getMgetRequestDescription());
		
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void suggestVesselTrackingPost_Return200_WhenSuggestIsCorrect() throws Exception {

		DataQueryDTO dataQuery = new DataQueryDTO();
		dataQuery.setSize(1);
		dataQuery.createSimpleQueryDTOFromSuggestQueryParams(new String[] { "properties.dest" },
				vesselTracking.getProperties().getDest(), 1);

		// Se elimina accessibilityIds ya que no está permitido para usuarios
		// no registrados
		HashMap<String, Object> query = mapper.convertValue(dataQuery, HashMap.class);
		query.remove("accessibilityIds");

		// @formatter:off
		
		this.mockMvc
			.perform(post(url + "/_suggest").content(mapper.writeValueAsString(query))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.length()", is(1))) 
				.andExpect(jsonPath("$.body[0]", startsWith("<b>")))
				.andExpect(jsonPath("$.body[0]", endsWith("</b>")))
					.andDo(getDataQueryFieldsDescriptor());
				
		
		// @formatter:on
	}

	@Test
	public void getFilterSchema_Return200_WhenSchemaIsAvailable() throws Exception {

		// @formatter:off
		
		this.mockMvc.perform(get(url + filterSchemaPath)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body.schema", notNullValue()))
			.andExpect(jsonPath("$.body.schema.properties", notNullValue()))
			.andExpect(jsonPath("$.body.schema.properties.postFilter", notNullValue()))
			.andExpect(jsonPath("$.body.schema.properties.aggs", notNullValue()));
			// TODO: aumentar el nivel de checkeo
		// @formatter:on
	}
}
