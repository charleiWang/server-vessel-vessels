package es.redmic.vesselslib.events.vesseltracking.delete;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;

public class CheckDeleteVesselTrackingEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CheckDeleteTrackingVesselEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.delete\",\"fields\":["
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.CHECK_DELETE;

	public CheckDeleteVesselTrackingEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
