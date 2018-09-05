package es.redmic.vesselslib.unit.dto.vesseltype;

import org.junit.Before;
import org.junit.Test;

import es.redmic.testutils.dto.DTOBaseTest;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;

public class VesselTypeCheckDTOValidationTest extends DTOBaseTest<VesselTypeDTO> {

	protected static final String VESSEL_ID_NOT_FOUND_MESSAGE_TEMPLATE = "{redmic.validation.constraints.ValidateVesselId.message}";

	private static VesselTypeDTO dto;

	@Before
	public void reset() {

		dto = new VesselTypeDTO();
		dto.setId("1");
		dto.setCode("1234");
		dto.setName("type");
		dto.setName_en("type");
	}

	@Test
	public void validationDTO_NoReturnError_IfDTOIsCorrect() {

		checkDTOHasNoError(dto);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfNameIsNull() {

		dto.setName(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnSizeError_IfNameExceedsSize() {

		dto.setName(generateString(501));

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfName_enIsNull() {

		dto.setName_en(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnSizeError_IfName_enExceedsSize() {

		dto.setName_en(generateString(501));

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfCodeIsNull() {

		dto.setCode(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnSizeError_IfCodeExceedsSize() {

		dto.setCode(generateString(11));

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}
}