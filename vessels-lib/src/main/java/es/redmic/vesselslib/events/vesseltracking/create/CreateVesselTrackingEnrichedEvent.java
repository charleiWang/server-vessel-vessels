package es.redmic.vesselslib.events.vesseltracking.create;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;

public class CreateVesselTrackingEnrichedEvent extends VesselTrackingEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselTrackingEnrichedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.create\",\"fields\":["
			+ getVesselTrackingEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.CREATE_ENRICHED;

	public CreateVesselTrackingEnrichedEvent() {
		super(type);
	}

	public CreateVesselTrackingEnrichedEvent(VesselTrackingDTO vesselTracking) {
		super(type);
		this.setVesselTracking(vesselTracking);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
