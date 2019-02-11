package es.redmic.vesselslib.unit.dto.vessel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.unit.utils.VesselDataUtil;

public class VesselEqualTest {

	@Test
	public void equal_returnTrue_IfVesselIsEqual() {

		VesselDTO dto = VesselDataUtil.getVessel();

		assertTrue(dto.equals(dto));
	}

	@Test
	public void equal_returnFalse_IfCommonPropertyIsDifferent() {

		VesselDTO dto1 = VesselDataUtil.getVessel();

		VesselDTO dto2 = VesselDataUtil.getVessel();

		dto1.setId("111111");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfCommonPropertyIsNull() {

		VesselDTO dto1 = VesselDataUtil.getVessel();

		VesselDTO dto2 = VesselDataUtil.getVessel();

		dto1.setId(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfVesselPropertyIsDifferent() {

		VesselDTO dto1 = VesselDataUtil.getVessel();

		VesselDTO dto2 = VesselDataUtil.getVessel();

		dto1.setMmsi(112222344);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfVesselPropertyIsNull() {

		VesselDTO dto1 = VesselDataUtil.getVessel();

		VesselDTO dto2 = VesselDataUtil.getVessel();

		dto1.setMmsi(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselInsertedIsDifferent() {

		VesselDTO dto1 = VesselDataUtil.getVessel();

		VesselDTO dto2 = VesselDataUtil.getVessel();

		dto1.setInserted(DateTime.now().plusDays(1));
		assertTrue(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselInsertedIsNull() {

		VesselDTO dto1 = VesselDataUtil.getVessel();

		VesselDTO dto2 = VesselDataUtil.getVessel();

		dto1.setInserted(null);
		assertTrue(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselUpdatedIsDifferent() {

		VesselDTO dto1 = VesselDataUtil.getVessel();

		VesselDTO dto2 = VesselDataUtil.getVessel();

		dto1.setUpdated(DateTime.now().plusDays(1));
		assertTrue(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselUpdatedIsNull() {

		VesselDTO dto1 = VesselDataUtil.getVessel();

		VesselDTO dto2 = VesselDataUtil.getVessel();

		dto1.setUpdated(null);
		assertTrue(dto1.equals(dto2));
	}
}
