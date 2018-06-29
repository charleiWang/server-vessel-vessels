package es.redmic.test.vesselsview.integration.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.test.vesselsview.integration.common.CommonIntegrationTest;
import es.redmic.vesselsview.VesselsViewApplication;
import es.redmic.vesselsview.model.VesselType;
import es.redmic.vesselsview.repository.VesselTypeESRepository;

@SpringBootTest(classes = { VesselsViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class VesselTypeControllerTest extends CommonIntegrationTest {

	@Value("${documentation.VESSELTYPE_HOST}")
	private String VESSELTYPE_HOST;

	@Value("${controller.mapping.vesseltype}")
	private String VESSELTYPE_PATH;

	@Autowired
	VesselTypeESRepository repository;

	VesselType vesselType = new VesselType();

	@Before
	public void setUp() {

		vesselType.setId(UUID.randomUUID().toString());
		vesselType.setCode("199");
		vesselType.setName("Other Type, no additional information");
		vesselType.setName_en("Other Type, no additional information");

		repository.save(vesselType);

		// @formatter:off
		
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain)
				.apply(documentationConfiguration(this.restDocumentation)
						.uris().withScheme(SCHEME).withHost(VESSELTYPE_HOST).withPort(PORT))
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
					.param("fields", "{name}")
					.param("text", vesselType.getName())
					.param("size", "1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				//.andExpect(jsonPath("$.body.length()", is(1))) TODO: cuando funcionen las sugerencias, arreglar
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
				//.andExpect(jsonPath("$.body.length()", is(1))); TODO: cuando funcionen las sugerencias, arreglar 
					.andDo(getSimpleQueryFieldsDescriptor());;
				
		
		// @formatter:on
	}

	@Test
	public void getFilterSchema_Return200_WhenSchemaIsAvailable() throws Exception {

		// @formatter:off
		
		this.mockMvc.perform(get(VESSELTYPE_PATH + filterSchemaPath)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body.schema", notNullValue()))
			.andExpect(jsonPath("$.body.schema.properties", notNullValue()))
			.andExpect(jsonPath("$.body.schema.properties.postFilter").doesNotExist())
			.andExpect(jsonPath("$.body.schema.properties.aggs").doesNotExist());
			// TODO: aumentar el nivel de checkeo
		// @formatter:on
	}
}