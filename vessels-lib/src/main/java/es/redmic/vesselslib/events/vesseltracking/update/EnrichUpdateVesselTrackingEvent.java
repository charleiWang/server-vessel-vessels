package es.redmic.vesselslib.events.vesseltracking.update;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;

public class EnrichUpdateVesselTrackingEvent extends VesselTrackingEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"EnrichUpdateVesselTrackingEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.update\",\"fields\":["
			+ getVesselTrackingEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.ENRICH_UPDATE;

	public EnrichUpdateVesselTrackingEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public EnrichUpdateVesselTrackingEvent(VesselTrackingDTO vesselTracking) {
		super(type);
		this.setVesselTracking(vesselTracking);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
