package es.redmic.vesselslib.events.vessel.update;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.vesselslib.events.vessel.VesselEventType;

public class UpdateVesselConfirmedEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselConfirmedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.update\",\"fields\":["
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static VesselEventType type = VesselEventType.UPDATE_VESSEL_CONFIRMED;

	public UpdateVesselConfirmedEvent() {
		super(type.name());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
