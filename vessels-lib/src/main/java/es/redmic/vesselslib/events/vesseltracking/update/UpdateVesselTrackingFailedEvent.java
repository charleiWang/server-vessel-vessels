package es.redmic.vesselslib.events.vesseltracking.update;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;

public class UpdateVesselTrackingFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselTrackingFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.update\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.UPDATE_FAILED;

	public UpdateVesselTrackingFailedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
