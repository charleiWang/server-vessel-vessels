package es.redmic.vesselslib.unit.dto.ais;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import es.redmic.vesselslib.dto.ais.AISTrackingDTO;
import es.redmic.vesselslib.unit.utils.VesselAvroBaseTest;

public class AISTrackingCheckAvroSchemaTest extends VesselAvroBaseTest {

	@Test
	public void serializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		AISTrackingDTO dto = new AISTrackingDTO();
		dto.setMmsi(1234);
		dto.setTstamp(new DateTime());
		dto.setLatitude(2.1);
		dto.setLongitude(3.2);

		Object result = serializerAndDeserializer(dto);

		assertTrue("El objeto obtenido debe ser una instancia de AISTrackingDTO",
				AISTrackingDTO.class.isInstance(result));

		assertEquals(result, dto);

	}
}
