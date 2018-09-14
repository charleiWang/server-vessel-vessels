package es.redmic.vesselslib.events.vesseltracking.delete;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;

public class DeleteVesselTrackingCheckFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"DeleteVesselTrackingCheckFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.delete\",\"fields\":["
		+ getFailEventSchema() + ","
		+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.DELETE_CHECK_FAILED;

	public DeleteVesselTrackingCheckFailedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
