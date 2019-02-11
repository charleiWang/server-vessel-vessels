package es.redmic.vesselslib.unit.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import es.redmic.vesselslib.dto.ais.AISTrackingDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.utils.VesselTrackingUtil;

@RunWith(MockitoJUnitRunner.class)
public class VesselTrackingUtilsTest {

	// @formatter:off
 
	String QFLAG = "0",
			VFLAG = "N",
			ACTIVITY_ID = "9999";
	
	// @formatter:on

	@Test
	public void convertAisToVesselTracking_ReturnVesselTrackingDTO_IfAISTrackingDTOIsCorrect() {

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

		VesselTrackingDTO result = VesselTrackingUtil.convertTrackToVesselTracking(dto, QFLAG, VFLAG, ACTIVITY_ID);

		assertTrue(result.getProperties().getDate().isEqual(dto.getTstamp()));
		assertEquals(result.getProperties().getCog(), dto.getCog());
		assertEquals(result.getProperties().getSog(), dto.getSog());
		assertEquals(result.getProperties().getHeading(), dto.getHeading());
		assertEquals(result.getProperties().getNavStat(), dto.getNavStat());
		assertEquals(result.getProperties().getEta(), dto.getEta());
		assertEquals(result.getProperties().getDest(), dto.getDest());

		VesselDTO vesselResult = result.getProperties().getVessel();

		assertEquals(vesselResult.getMmsi(), dto.getMmsi());
		assertEquals(vesselResult.getName(), dto.getName());
		Double length = dto.getA() + dto.getB();
		assertEquals(vesselResult.getLength(), length);
		Double beam = dto.getC() + dto.getD();
		assertEquals(vesselResult.getBeam(), beam);
	}

	@Test
	public void convertAisToVesselTrackingWithNullValues_ReturnVesselTrackingDTO_IfAISTrackingDTOIsCorrect() {

		VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getCreatedEvent();

		VesselDTO vessel = vesselCreatedEvent.getVessel();

		AISTrackingDTO dto = new AISTrackingDTO();
		dto.setMmsi(vessel.getMmsi());
		dto.setName(vessel.getName());
		dto.setTstamp(new DateTime());
		dto.setLatitude(2.1);
		dto.setLongitude(3.2);

		VesselTrackingDTO result = VesselTrackingUtil.convertTrackToVesselTracking(dto, QFLAG, VFLAG, ACTIVITY_ID);

		assertTrue(result.getProperties().getDate().isEqual(dto.getTstamp()));
		assertNull(result.getProperties().getCog());
		assertNull(result.getProperties().getSog());
		assertNull(result.getProperties().getHeading());
		assertNull(result.getProperties().getNavStat());
		assertNull(result.getProperties().getEta());
		assertNull(result.getProperties().getDest());

		VesselDTO vesselResult = result.getProperties().getVessel();

		assertEquals(vesselResult.getMmsi(), dto.getMmsi());
		assertEquals(vesselResult.getName(), dto.getName());
		assertNotNull(vesselResult.getType());
		assertEquals(vesselResult.getType().getCode(), "0");
		assertNull(vesselResult.getImo());
		assertNull(vesselResult.getLength());
		assertNull(vesselResult.getBeam());
		assertNull(vesselResult.getCallSign());
	}
}
