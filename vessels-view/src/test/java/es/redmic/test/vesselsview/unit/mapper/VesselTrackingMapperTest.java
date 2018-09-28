package es.redmic.test.vesselsview.unit.mapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.wrapper.GeoSearchWrapper;
import es.redmic.testutils.utils.JsonToBeanTestUtil;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselsview.config.MapperScanBean;
import es.redmic.vesselsview.mapper.vessel.VesselESMapper;
import es.redmic.vesselsview.mapper.vesseltracking.VesselTrackingESMapper;
import es.redmic.vesselsview.mapper.vesseltracking.VesselTrackingPropertiesESMapper;
import es.redmic.vesselsview.mapper.vesseltype.VesselTypeESMapper;
import es.redmic.vesselsview.model.vesseltracking.VesselTracking;
import es.redmic.viewlib.common.mapper.es2dto.FeatureCollectionMapper;
import es.redmic.viewlib.common.mapper.es2dto.FeatureMapper;
import ma.glasnost.orika.MappingContext;

@RunWith(MockitoJUnitRunner.class)
public class VesselTrackingMapperTest {

	@InjectMocks
	VesselTrackingESMapper mapper;

	@InjectMocks
	VesselTrackingPropertiesESMapper mapperProperties;

	@InjectMocks
	VesselESMapper mapperVessel;

	@InjectMocks
	VesselTypeESMapper mapperVesselType;

	@InjectMocks
	FeatureCollectionMapper geoDataCollectionMapper;

	@InjectMocks
	FeatureMapper geoDataItemMapper;

	protected MapperScanBean factory = new MapperScanBean().build();

	// @formatter:off

	String modelPath = "/data/model/vesseltracking/vesselTracking.json",
			dtoToSavePath = "/data/dto/vesseltracking/vesselTracking.json",
			searchWrapperPath = "/data/model/vesseltracking/searchWrapperVesselTrackingESModel.json",
			searchDTOPath = "/data/dto/vesseltracking/searchWrapperVesselTrackingDTO.json";

	// @formatter:on

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(mapper);
		factory.addMapper(mapperProperties);
		factory.addMapper(mapperVessel);
		factory.addMapper(mapperVesselType);
		factory.addMapper(geoDataCollectionMapper);
		factory.addMapper(geoDataItemMapper);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		VesselTrackingDTO dtoIn = (VesselTrackingDTO) JsonToBeanTestUtil.getBean(dtoToSavePath,
				VesselTrackingDTO.class);

		VesselTracking modelOut = factory.getMapperFacade().map(dtoIn, VesselTracking.class);

		String modelStringExpected = JsonToBeanTestUtil.getJsonString(modelPath);
		String modelString = JsonToBeanTestUtil.writeValueAsString(modelOut);

		JSONAssert.assertEquals(modelStringExpected, modelString, false);
	}

	@Test
	public void mapperSearchWrapperToDto() throws JsonParseException, JsonMappingException, IOException, JSONException {

		JavaType type = JsonToBeanTestUtil.getParametizedType(GeoSearchWrapper.class, VesselTracking.class);

		GeoSearchWrapper<?> searchWrapperModel = (GeoSearchWrapper<?>) JsonToBeanTestUtil.getBean(searchWrapperPath,
				type);
		String expected = JsonToBeanTestUtil.getJsonString(searchDTOPath);

		Map<Object, Object> globalProperties = new HashMap<Object, Object>();
		globalProperties.put("targetTypeDto", VesselTrackingDTO.class);
		MappingContext context = new MappingContext(globalProperties);

		GeoJSONFeatureCollectionDTO searchDTO = factory.getMapperFacade().map(searchWrapperModel.getHits(),
				GeoJSONFeatureCollectionDTO.class, context);

		String searchDTOString = JsonToBeanTestUtil.writeValueAsString(searchDTO);

		JSONAssert.assertEquals(expected, searchDTOString, false);
	}
}