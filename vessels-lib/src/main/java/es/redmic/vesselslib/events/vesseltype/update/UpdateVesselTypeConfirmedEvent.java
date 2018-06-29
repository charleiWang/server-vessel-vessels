package es.redmic.vesselslib.events.vesseltype.update;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;

public class UpdateVesselTypeConfirmedEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselTypeConfirmedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.update\",\"fields\":["
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static VesselTypeEventType type = VesselTypeEventType.UPDATE_VESSELTYPE_CONFIRMED;

	public UpdateVesselTypeConfirmedEvent() {
		super(type.name());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
