package es.redmic.vesselslib.events.vesseltracking.create;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;

public class VesselTrackingCreatedEvent extends VesselTrackingEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"VesselTrackingCreatedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.create\",\"fields\":["
			+ getVesselTrackingEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.CREATED;

	public VesselTrackingCreatedEvent() {
		super(type);
	}

	public VesselTrackingCreatedEvent(VesselTrackingDTO vesselTracking) {
		super(type);
		this.setVesselTracking(vesselTracking);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
