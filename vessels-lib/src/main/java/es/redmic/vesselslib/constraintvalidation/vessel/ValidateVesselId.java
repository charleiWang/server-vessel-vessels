package es.redmic.vesselslib.constraintvalidation.vessel;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { VesselIdValidator.class })
@Documented
public @interface ValidateVesselId {
	String message() default "{redmic.validation.constraints.ValidateVesselId.message}";

	String mmsi();

	String imo();

	@SuppressWarnings("rawtypes")
	Class[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}