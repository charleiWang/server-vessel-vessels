package es.redmic.vesselslib.events.vesseltracking.create;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;

public class CreateVesselTrackingEvent extends VesselTrackingEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselTrackingEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.create\",\"fields\":["
			+ getVesselTrackingEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.CREATE;

	public CreateVesselTrackingEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public CreateVesselTrackingEvent(VesselTrackingDTO vesselTracking) {
		super(type);
		this.setVesselTracking(vesselTracking);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
