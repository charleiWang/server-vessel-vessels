package es.redmic.vesselslib.events.vesseltracking.delete;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;

public class DeleteVesselTrackingFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"DeleteVesselTrackingFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.delete\",\"fields\":["
		+ getFailEventSchema() + ","
		+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.DELETE_FAILED;

	public DeleteVesselTrackingFailedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
