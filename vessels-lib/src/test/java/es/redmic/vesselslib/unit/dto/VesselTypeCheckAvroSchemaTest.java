package es.redmic.vesselslib.unit.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.unit.utils.VesselAvroBaseTest;
import es.redmic.vesselslib.unit.utils.VesselTypeDataUtil;

public class VesselTypeCheckAvroSchemaTest extends VesselAvroBaseTest {

	@Test
	public void serializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselTypeDTO dto = VesselTypeDataUtil.getVesselType();

		Object result = serializerAndDeserializer(dto);

		assertTrue("El objeto obtenido debe ser una instancia de VesselTypeDTO",
				VesselTypeDTO.class.isInstance(result));

		assertEquals(result, dto);
	}
}
