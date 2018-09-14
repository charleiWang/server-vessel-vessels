package es.redmic.vesselslib.events.vesseltype.create;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;

public class CreateVesselTypeConfirmedEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselTypeConfirmedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.create\",\"fields\":["
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.CREATE_CONFIRMED;

	public CreateVesselTypeConfirmedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
