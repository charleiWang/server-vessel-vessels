package es.redmic.vesselslib.events.vesseltracking.update;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingCancelledEvent;

public class UpdateVesselTrackingCancelledEvent extends VesselTrackingCancelledEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselTrackingCancelledEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.update\",\"fields\":["
			+ getVesselTrackingEventSchema() + ","
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.UPDATE_CANCELLED;

	public UpdateVesselTrackingCancelledEvent() {
		super(type);
	}

	public UpdateVesselTrackingCancelledEvent(VesselTrackingDTO vesselTracking) {
		super(type);
		this.setVesselTracking(vesselTracking);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
