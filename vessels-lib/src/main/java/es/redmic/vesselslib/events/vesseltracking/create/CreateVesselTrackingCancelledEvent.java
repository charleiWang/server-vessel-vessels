package es.redmic.vesselslib.events.vesseltracking.create;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;

public class CreateVesselTrackingCancelledEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselTrackingCancelledEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.create\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.CREATE_CANCELLED;

	public CreateVesselTrackingCancelledEvent() {
		super(type);
	}

	public CreateVesselTrackingCancelledEvent(VesselTrackingDTO vesselTracking) {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
