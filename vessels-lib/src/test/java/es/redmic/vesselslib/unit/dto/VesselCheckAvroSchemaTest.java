package es.redmic.vesselslib.unit.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.unit.utils.VesselAvroBaseTest;
import es.redmic.vesselslib.unit.utils.VesselDataUtil;

public class VesselCheckAvroSchemaTest extends VesselAvroBaseTest {

	@Test
	public void serializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselDTO dto = VesselDataUtil.getVessel();

		Object result = serializerAndDeserializer(dto);

		assertTrue("El objeto obtenido debe ser una instancia de VesselDTO", VesselDTO.class.isInstance(result));

		assertEquals(result, dto);

	}

	@Test
	public void serializeAndDeserialize_IsSuccessful_IfImoIsNull() {

		VesselDTO dto = VesselDataUtil.getVessel();
		dto.setImo(0); // Si imo es 0 se considera como Null

		assertNull(dto.getImo());

		Object result = serializerAndDeserializer(dto);

		assertTrue("El objeto obtenido debe ser una instancia de VesselDTO", VesselDTO.class.isInstance(result));

		assertEquals(result, dto);

	}
}
