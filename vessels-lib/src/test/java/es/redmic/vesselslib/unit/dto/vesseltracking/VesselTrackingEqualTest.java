package es.redmic.vesselslib.unit.dto.vesseltracking;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;

import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.unit.utils.VesselTrackingDataUtil;

public class VesselTrackingEqualTest {

	VesselTrackingDTO dto1;

	VesselTrackingDTO dto2;

	@Before
	public void beforeEachTest() {

		dto1 = VesselTrackingDataUtil.getVesselTracking();

		dto2 = VesselTrackingDataUtil.getVesselTracking();

		dto1.setUuid(dto2.getUuid());
		dto1.setId(dto2.getId());
		dto1.getProperties().setDate(dto2.getProperties().getDate());
	}

	@Test
	public void equal_returnTrue_IfVesselTrackingIsEqual() {

		VesselTrackingDTO dto = VesselTrackingDataUtil.getVesselTracking();

		assertTrue(dto.equals(dto));
	}

	@Test
	public void equal_returnFalse_IfCommonPropertyIsDifferent() {

		dto1.setId("111111");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfCommonPropertyIsNull() {

		dto1.setId(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfVesselTrackingGeometryIsDifferent() {

		dto1.setGeometry(new GeometryFactory().createPoint());
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfVesselTrackingGeometryIsNull() {

		dto1.setGeometry(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfVesselTrackingPropertyIsDifferent() {

		dto1.getProperties().setActivity("112222344");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfVesselTrackingPropertyIsNull() {

		dto1.getProperties().setActivity(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselTrackingInsertedIsDifferent() {

		dto2.getProperties().setInserted(DateTime.now().plusDays(1));
		assertTrue(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselTrackingInsertedIsNull() {

		dto2.getProperties().setInserted(null);
		assertTrue(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselTrackingUpdatedIsDifferent() {

		dto2.getProperties().setUpdated(DateTime.now().plusDays(1));
		assertTrue(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselTrackingUpdatedIsNull() {

		dto2.getProperties().setUpdated(null);
		assertTrue(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfVesselTrackingVesselIsDifferent() {

		dto1.getProperties().getVessel().setId("1221333");
		assertFalse(dto1.equals(dto2));
	}
}
