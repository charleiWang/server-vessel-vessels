package es.redmic.test.vesselsview.unit.mapper;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.testutils.utils.JsonToBeanTestUtil;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselsview.config.MapperScanBean;
import es.redmic.vesselsview.mapper.vesseltype.VesselTypeESMapper;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import es.redmic.viewlib.common.mapper.es2dto.DataCollectionESMapper;
import es.redmic.viewlib.common.mapper.es2dto.DataItemESMapper;

@RunWith(MockitoJUnitRunner.class)
public class VesselTypeMapperTest {

	@InjectMocks
	VesselTypeESMapper mapper;

	@InjectMocks
	DataCollectionESMapper dataCollectionMapper;

	@InjectMocks
	DataItemESMapper dataItemMapper;

	protected MapperScanBean factory = new MapperScanBean().build();

	// @formatter:off

	String modelPath = "/data/model/vesseltype/vesseltype.json",
			dtoToSavePath = "/data/dto/vesseltype/vesseltype.json",
			searchWrapperPath = "/data/model/vesseltype/searchWrapperVesselTypeESModel.json",
			searchDTOPath = "/data/dto/vesseltype/searchWrapperVesselTypeDTO.json";

	// @formatter:on

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(mapper);
		factory.addMapper(dataCollectionMapper);
		factory.addMapper(dataItemMapper);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		VesselTypeDTO dtoIn = (VesselTypeDTO) JsonToBeanTestUtil.getBean(dtoToSavePath, VesselTypeDTO.class);

		VesselType modelOut = factory.getMapperFacade().map(dtoIn, VesselType.class);

		String modelStringExpected = JsonToBeanTestUtil.getJsonString(modelPath);
		String modelString = JsonToBeanTestUtil.writeValueAsString(modelOut);

		JSONAssert.assertEquals(modelString, modelStringExpected, false);
	}

	@Test
	public void mapperSearchWrapperToDto() throws JsonParseException, JsonMappingException, IOException, JSONException {

		JavaType type = JsonToBeanTestUtil.getParametizedType(DataSearchWrapper.class, VesselType.class);

		DataSearchWrapper<?> searchWrapperModel = (DataSearchWrapper<?>) JsonToBeanTestUtil.getBean(searchWrapperPath,
				type);
		String expected = JsonToBeanTestUtil.getJsonString(searchDTOPath);

		JSONCollectionDTO searchDTO = factory.getMapperFacade().map(searchWrapperModel.getHits(),
				JSONCollectionDTO.class);

		String searchDTOString = JsonToBeanTestUtil.writeValueAsString(searchDTO);

		JSONAssert.assertEquals(expected, searchDTOString, false);
	}
}