package es.redmic.test.vesselsview.integration.controller;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.testutils.documentation.DocumentationViewBaseTest;
import es.redmic.testutils.utils.JsonToBeanTestUtil;
import es.redmic.vesselsview.VesselsViewApplication;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import es.redmic.vesselsview.repository.vesseltype.VesselTypeESRepository;

@SpringBootTest(classes = { VesselsViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "schema.registry.port=18082" })
@DirtiesContext
public class VesselTypeControllerTest extends DocumentationViewBaseTest {

	@Value("${documentation.VESSEL_HOST}")
	private String HOST;

	@Value("${controller.mapping.vesseltype}")
	private String VESSELTYPE_PATH;

	@Autowired
	VesselTypeESRepository repository;

	VesselType vesselType = new VesselType();

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1);

	@PostConstruct
	public void CreateVesselFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@Override
	@Before
	public void setUp() {

		String code = RandomStringUtils.random(4, true, false);

		vesselType.setId("vesseltype-code-" + code);
		vesselType.setCode(code);
		vesselType.setName("Other Type, no additional information");
		vesselType.setName_en("Other Type, no additional information");

		repository.save(vesselType);

		// @formatter:off
		
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain)
				.apply(documentationConfiguration(this.restDocumentation)
						.uris().withScheme(SCHEME).withHost(HOST).withPort(PORT))
				.alwaysDo(this.document).build();

		// @formatter:on
	}

	@After
	public void clean() {
		repository.delete(vesselType.getId());
	}

	@Test
	public void getVesselType_Return200_WhenItemExist() throws Exception {

		// @formatter:off
		
		this.mockMvc.perform(get(VESSELTYPE_PATH + "/" + vesselType.getId()).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body.id", is(vesselType.getId())));
		
		// @formatter:on
	}

	@Test
	public void searchVesselTypesPost_Return200_WhenSearchIsCorrect() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);

		// @formatter:off
		
		this.mockMvc
				.perform(post(VESSELTYPE_PATH + "/_search").content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getSimpleQueryFieldsDescriptor());
		
		// @formatter:on
	}

	@Test
	public void searchVesselTypesQueryString_Return200_WhenSearchIsCorrect() throws Exception {

		// @formatter:off
		
		this.mockMvc
			.perform(get(VESSELTYPE_PATH)
					.param("fields", "{name}")
					.param("text", vesselType.getName())
					.param("from", "0")
					.param("size", "1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getSearchSimpleParametersDescription());
		
		// @formatter:off
	}

	@Test
	public void mgetVesselTypes_Return200_WhenVesselTypesExists() throws Exception {

		MgetDTO mgetQuery = new MgetDTO();
		mgetQuery.setIds(Arrays.asList(vesselType.getId()));
		mgetQuery.setFields(Arrays.asList("id"));

		// @formatter:off
		
		this.mockMvc
			.perform(post(VESSELTYPE_PATH + "/_mget").content(mapper.writeValueAsString(mgetQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data[0].id", is(vesselType.getId())))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getMgetRequestDescription());
		
		// @formatter:on
	}

	@Test
	public void suggestVesselTypesQueryString_Return200_WhenSuggestIsCorrect() throws Exception {

		// @formatter:off
		
		this.mockMvc
			.perform(get(VESSELTYPE_PATH + "/_suggest")
					.param("fields", new String[] { "name" })
					.param("text", vesselType.getName())
					.param("size", "1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.length()", is(1)))
				.andExpect(jsonPath("$.body[0]", startsWith("<b>")))
				.andExpect(jsonPath("$.body[0]", endsWith("</b>")))
					.andDo(getSuggestParametersDescription());
		
		// @formatter:on
	}

	@Test
	public void suggestVesselTypesPost_Return200_WhenSuggestIsCorrect() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);
		dataQuery.createSimpleQueryDTOFromSuggestQueryParams(new String[] { "name" }, vesselType.getName(), 1);

		// @formatter:off
		
		this.mockMvc
			.perform(post(VESSELTYPE_PATH + "/_suggest").content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.length()", is(1)))
				.andExpect(jsonPath("$.body[0]", startsWith("<b>")))
				.andExpect(jsonPath("$.body[0]", endsWith("</b>")))
					.andDo(getSimpleQueryFieldsDescriptor());;
				
		
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getFilterSchema_Return200_WhenSchemaIsAvailable() throws Exception {

		Map<String, Object> schemaExpected = (Map<String, Object>) JsonToBeanTestUtil
				.getBean("/data/schemas/vesseltypequeryschema.json", Map.class);

		// @formatter:off
		
		this.mockMvc.perform(get(VESSELTYPE_PATH + filterSchemaPath)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body", is(schemaExpected)));
		// @formatter:on
	}
}
