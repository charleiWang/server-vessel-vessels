package es.redmic.vesselslib.events.vessel.update;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vessel.VesselEventType;

public class UpdateVesselFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.update\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static VesselEventType type = VesselEventType.UPDATE_VESSEL_FAILED;

	public UpdateVesselFailedEvent() {
		super(type.name());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
