package es.redmic.vesselslib.unit.dto.vesseltracking;

import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.jts4jackson.module.JTSModule;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.unit.utils.VesselAvroBaseTest;
import es.redmic.vesselslib.unit.utils.VesselTrackingDataUtil;

public class VesselTrackingCheckAvroSchemaTest extends VesselAvroBaseTest {

	ObjectMapper mapper = new ObjectMapper().registerModule(new JTSModule());

	@Test
	public void serializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect()
			throws JsonProcessingException, JSONException {

		VesselTrackingDTO dto = VesselTrackingDataUtil.getVesselTracking();

		Object result = serializerAndDeserializer(dto);

		assertTrue("El objeto obtenido debe ser una instancia de VesselTrackingDTO",
				VesselTrackingDTO.class.isInstance(result));

		JSONAssert.assertEquals(mapper.writeValueAsString(result), mapper.writeValueAsString(dto), false);

	}
}
