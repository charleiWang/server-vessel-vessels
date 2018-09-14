package es.redmic.vesselslib.events.vesseltracking.create;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;

public class CreateVesselTrackingFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselTrackingFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.create\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.CREATE_FAILED;

	public CreateVesselTrackingFailedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
