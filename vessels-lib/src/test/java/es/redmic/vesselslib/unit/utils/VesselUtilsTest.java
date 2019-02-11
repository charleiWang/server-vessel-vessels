package es.redmic.vesselslib.unit.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import es.redmic.vesselslib.dto.ais.AISTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.utils.VesselUtil;

@RunWith(MockitoJUnitRunner.class)
public class VesselUtilsTest {

	@Test
	public void convertAisToVessel_ReturnVesselDTO_IfAISTrackingDTOIsCorrect() {

		VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getCreatedEvent();

		VesselDTO vessel = vesselCreatedEvent.getVessel();

		AISTrackingDTO dto = new AISTrackingDTO();
		dto.setMmsi(vessel.getMmsi());
		dto.setImo(vessel.getImo());
		dto.setName(vessel.getName());
		dto.setType(Integer.parseInt(vessel.getType().getCode()));
		dto.setCallSign(vessel.getCallSign());
		dto.setTstamp(new DateTime());
		dto.setLatitude(2.1);
		dto.setLongitude(3.2);
		dto.setA(vessel.getLength() / 2);
		dto.setB(vessel.getLength() / 2);
		dto.setC(vessel.getBeam() / 2);
		dto.setD(vessel.getBeam() / 2);
		dto.setCog(2.3);
		dto.setSog(3.4);
		dto.setHeading(221);
		dto.setNavStat(33);
		dto.setEta("00:00 00:00");
		dto.setDest("Santa Cruz de Tenerife");

		VesselDTO result = VesselUtil.convertTrackToVessel(dto);

		assertEquals(result.getMmsi(), dto.getMmsi());
		assertEquals(result.getName(), dto.getName());
		Double length = dto.getA() + dto.getB();
		assertEquals(result.getLength(), length);
		Double beam = dto.getC() + dto.getD();
		assertEquals(result.getBeam(), beam);

		assertNotNull(result.getType());
		assertEquals(result.getType().getCode(), dto.getType().toString());
		assertEquals(result.getImo(), dto.getImo());
		assertEquals(result.getCallSign(), dto.getCallSign());
	}

	@Test
	public void convertAisToVesselWithNullValues_ReturnVesselDTO_IfAISTrackingDTOIsCorrect() {

		Integer mmsi = 2;

		String name = "prueba";

		AISTrackingDTO dto = new AISTrackingDTO();
		dto.setMmsi(mmsi);
		dto.setName(name);
		dto.setTstamp(new DateTime());
		dto.setLatitude(2.1);
		dto.setLongitude(3.2);

		VesselDTO result = VesselUtil.convertTrackToVessel(dto);

		assertEquals(result.getMmsi(), dto.getMmsi());
		assertEquals(result.getName(), dto.getName());
		assertNotNull(result.getType());
		assertEquals(result.getType().getCode(), "0");
		assertNull(result.getImo());
		assertNull(result.getLength());
		assertNull(result.getBeam());
		assertNull(result.getCallSign());
	}
}
