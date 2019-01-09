package es.redmic.vesselslib.unit.dto.vesseltype;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.unit.utils.VesselTypeDataUtil;

public class VesselTypeEqualTest {

	@Test
	public void equal_returnTrue_IfVesselTypeIsEqual() {

		VesselTypeDTO dto = VesselTypeDataUtil.getVesselType();

		assertTrue(dto.equals(dto));
	}

	@Test
	public void equal_returnFalse_IfIdIsDifferent() {

		VesselTypeDTO dto1 = VesselTypeDataUtil.getVesselType();

		VesselTypeDTO dto2 = VesselTypeDataUtil.getVesselType();

		dto1.setId("111111");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfIdIsNull() {

		VesselTypeDTO dto1 = VesselTypeDataUtil.getVesselType();

		VesselTypeDTO dto2 = VesselTypeDataUtil.getVesselType();

		dto1.setId(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfVesselCodeIsDifferent() {

		VesselTypeDTO dto1 = VesselTypeDataUtil.getVesselType();

		VesselTypeDTO dto2 = VesselTypeDataUtil.getVesselType();

		dto1.setCode("112222344");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnFalse_IfVesselTypeCodeIsNull() {

		VesselTypeDTO dto1 = VesselTypeDataUtil.getVesselType();

		VesselTypeDTO dto2 = VesselTypeDataUtil.getVesselType();

		dto1.setCode(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselTypeNameIsDifferent() {

		VesselTypeDTO dto1 = VesselTypeDataUtil.getVesselType();

		VesselTypeDTO dto2 = VesselTypeDataUtil.getVesselType();

		dto1.setName("cddd");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselTypeNameIsNull() {

		VesselTypeDTO dto1 = VesselTypeDataUtil.getVesselType();

		VesselTypeDTO dto2 = VesselTypeDataUtil.getVesselType();

		dto1.setName(null);
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselTypeName_enIsDifferent() {

		VesselTypeDTO dto1 = VesselTypeDataUtil.getVesselType();

		VesselTypeDTO dto2 = VesselTypeDataUtil.getVesselType();

		dto1.setName_en("cvvcvc");
		assertFalse(dto1.equals(dto2));
	}

	@Test
	public void equal_returnTrue_IfVesselTypeName_enIsNull() {

		VesselTypeDTO dto1 = VesselTypeDataUtil.getVesselType();

		VesselTypeDTO dto2 = VesselTypeDataUtil.getVesselType();

		dto1.setName_en(null);
		assertFalse(dto1.equals(dto2));
	}
}
