package es.redmic.vesselslib.events.vesseltracking.create;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;

public class CreateVesselTrackingConfirmedEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselTrackingConfirmedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.create\",\"fields\":["
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTrackingEventTypes.CREATE_CONFIRMED;

	public CreateVesselTrackingConfirmedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
