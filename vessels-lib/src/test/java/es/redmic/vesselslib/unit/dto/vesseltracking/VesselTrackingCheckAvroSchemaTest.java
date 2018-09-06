package es.redmic.vesselslib.unit.dto.vesseltracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.unit.utils.VesselAvroBaseTest;
import es.redmic.vesselslib.unit.utils.VesselTrackingDataUtil;

public class VesselTrackingCheckAvroSchemaTest extends VesselAvroBaseTest {

	@Test
	public void serializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselTrackingDTO dto = VesselTrackingDataUtil.getVesselTracking();

		Object result = serializerAndDeserializer(dto);

		assertTrue("El objeto obtenido debe ser una instancia de VesselTrackingDTO",
				VesselTrackingDTO.class.isInstance(result));

		assertEquals(result, dto);

	}
}
