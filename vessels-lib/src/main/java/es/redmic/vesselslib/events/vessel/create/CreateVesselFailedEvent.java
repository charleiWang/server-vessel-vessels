package es.redmic.vesselslib.events.vessel.create;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;

public class CreateVesselFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.create\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.CREATE_FAILED;

	public CreateVesselFailedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
