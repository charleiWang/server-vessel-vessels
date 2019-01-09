package es.redmic.vesselslib.unit.dto.vessel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import es.redmic.testutils.dto.DTOBaseTest;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;

public class VesselCheckDTOValidationTest extends DTOBaseTest<VesselDTO> {

	protected static final String VESSEL_ID_NOT_FOUND_MESSAGE_TEMPLATE = "{redmic.validation.constraints.ValidateVesselId.message}";

	private static VesselDTO dto;

	@Before
	public void reset() {

		dto = new VesselDTO();
		dto.setName("name");
		dto.setMmsi(1234);
		dto.setImo(1234);
		VesselTypeDTO type = new VesselTypeDTO();
		type.setId("1");
		type.setCode("1234");
		type.setName("type");
		dto.setType(type);
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

		dto.setName(generateString(600));

		checkDTOHasError(dto, SIZE_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnIdNotFoundError_IfMmsiAndImoAreNull() {

		dto.setImo(null);
		dto.setMmsi(null);

		checkDTOHasError(dto, VESSEL_ID_NOT_FOUND_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_NoReturnError_IfImoIsNull() {

		dto.setImo(null);

		checkDTOHasNoError(dto);
	}

	@Test
	public void validationDTO_ReturnError_IfImoExceedsSize() {

		dto.setImo(10000000);

		checkDTOHasError(dto, MAX_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_NoReturnError_IfImoNoExceedsSize() {

		dto.setImo(9999999);

		checkDTOHasNoError(dto);
	}

	@Test
	public void validationDTO_NoReturnError_IfMmsiIsNull() {

		dto.setMmsi(null);

		checkDTOHasNoError(dto);
	}

	@Test
	public void validationDTO_ReturnError_IfMmsiExceedsSize() {

		dto.setMmsi(1000000000);

		checkDTOHasError(dto, MAX_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_NoReturnError_IfMmsiNoExceedsSize() {

		dto.setMmsi(999999999);

		checkDTOHasNoError(dto);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfTypeIsNull() {

		dto.setType(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void equal_ReturnTrue_IfDTOsAreEqual() {

		assertTrue(dto.equals(dto));
	}

	@Test
	public void equal_ReturnFalse_IfDTOsAreDiferent() {

		VesselDTO vesselDTO = new VesselDTO();
		vesselDTO.copyFromAIS(dto);

		assertFalse(dto.equals(vesselDTO));
	}

	@Test
	public void equal_ReturnTrue_IfDTOsAreCopy() {

		VesselDTO vesselDTO = new VesselDTO();
		vesselDTO.copyFromAIS(dto);
		vesselDTO.setMmsi(dto.getMmsi());
		vesselDTO.setId(dto.getId());

		assertTrue(dto.equals(vesselDTO));
	}
}