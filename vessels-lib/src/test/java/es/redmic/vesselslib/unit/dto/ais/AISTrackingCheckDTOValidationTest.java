package es.redmic.vesselslib.unit.dto.ais;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import es.redmic.testutils.dto.DTOBaseTest;
import es.redmic.vesselslib.dto.ais.AISTrackingDTO;

public class AISTrackingCheckDTOValidationTest extends DTOBaseTest<AISTrackingDTO> {

	private static AISTrackingDTO dto;

	@Before
	public void reset() {

		dto = new AISTrackingDTO();
		dto.setMmsi(1234);
		dto.setTstamp(new DateTime());
		dto.setLatitude(2.1);
		dto.setLongitude(3.2);
	}

	@Test
	public void validationDTO_NoReturnError_IfDTOIsCorrect() {

		checkDTOHasNoError(dto);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfMmsiIsNull() {

		dto.setMmsi(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfTstampIsNull() {

		dto.setTstamp(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfLatitudeIsNull() {

		dto.setLatitude(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}

	@Test
	public void validationDTO_ReturnNotNullError_IfLongitudeIsNull() {

		dto.setLongitude(null);

		checkDTOHasError(dto, NOT_NULL_MESSAGE_TEMPLATE);
	}
}