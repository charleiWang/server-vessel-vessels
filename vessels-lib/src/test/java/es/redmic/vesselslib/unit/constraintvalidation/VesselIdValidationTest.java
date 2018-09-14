package es.redmic.vesselslib.unit.constraintvalidation;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;

public class VesselIdValidationTest {

	VesselDTO vessel;

	ObjectMapper mapper = new ObjectMapper();

	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Before
	public void setUp() {

		vessel = new VesselDTO();
		VesselTypeDTO type = new VesselTypeDTO();
		type.setCode("234");
		type.setName("type");
		vessel.setType(type);
		vessel.setName("prueba");
	}

	@Test
	public void validateVesselId_ReturnTrue_IfMmsiExist() {

		vessel.setMmsi(1234);

		Set<ConstraintViolation<VesselDTO>> constraintViolations = validator.validate(vessel);
		assertEquals(0, constraintViolations.size());
	}

	@Test
	public void validateVesselId_ReturnTrue_IfImoExist() {

		vessel.setImo(1234);

		Set<ConstraintViolation<VesselDTO>> constraintViolations = validator.validate(vessel);
		assertEquals(0, constraintViolations.size());
	}

	@Test
	public void validateVesselId_ReturnFalse_IfMmsiAndImoAreNull() {

		Set<ConstraintViolation<VesselDTO>> constraintViolations = validator.validate(vessel);
		assertEquals(1, constraintViolations.size());
	}
}
