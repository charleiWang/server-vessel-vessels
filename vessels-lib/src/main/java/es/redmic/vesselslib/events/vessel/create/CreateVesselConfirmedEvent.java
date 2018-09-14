package es.redmic.vesselslib.events.vessel.create;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;

public class CreateVesselConfirmedEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselConfirmedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.create\",\"fields\":["
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.CREATE_CONFIRMED;

	public CreateVesselConfirmedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
