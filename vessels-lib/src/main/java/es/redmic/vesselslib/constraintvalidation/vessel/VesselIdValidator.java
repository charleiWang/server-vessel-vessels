package es.redmic.vesselslib.constraintvalidation.vessel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.redmic.brokerlib.constraintvalidation.base.BaseValidator;

public class VesselIdValidator extends BaseValidator<String> implements ConstraintValidator<ValidateVesselId, Object> {

	private String mmsiMethodName;
	private String imoMethodName;

	@Override
	public void initialize(ValidateVesselId validateDateRange) {

		mmsiMethodName = getAccessorMethodName(validateDateRange.mmsi());
		imoMethodName = getAccessorMethodName(validateDateRange.imo());
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext cvc) {

		Integer mmsi = invokeDateGetter(obj, mmsiMethodName);
		Integer imo = invokeDateGetter(obj, imoMethodName);
		if (mmsi == null && imo == null) {
			return false;
		}
		return true;
	}
}