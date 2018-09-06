package es.redmic.vesselslib.unit.dto.vesseltracking;

import org.junit.Before;
import org.junit.Test;

import es.redmic.testutils.dto.DTOBaseTest;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.unit.utils.VesselTrackingDataUtil;

public class VesselTrackingCheckDTOValidationTest extends DTOBaseTest<VesselTrackingDTO> {

	private static VesselTrackingDTO dto;

	@Before
	public void reset() {

		dto = VesselTrackingDataUtil.getVesselTracking();
	}

	@Test
	public void validationDTO_NoReturnError_IfDTOIsCorrect() {

		checkDTOHasNoError(dto);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfUUIDIsNull() {

		dto.setUuid(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfGeometryIsNull() {

		dto.setGeometry(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfPropertiesIsNull() {

		dto.setProperties(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfVesselIsNull() {

		dto.getProperties().setVessel(null);
		;

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfDateIsNull() {

		dto.getProperties().setDate(null);
		;

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}
}