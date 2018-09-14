package es.redmic.vesselslib.events.vesseltracking.update;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;

public class UpdateVesselTrackingConfirmedEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselTrackingConfirmedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.update\",\"fields\":["
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.UPDATE_CONFIRMED;

	public UpdateVesselTrackingConfirmedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
