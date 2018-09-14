package es.redmic.vesselslib.events.vesseltype.create;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;

public class CreateVesselTypeFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselTypeFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.create\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.CREATE_FAILED;

	public CreateVesselTypeFailedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
