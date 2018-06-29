package es.redmic.vesselslib.events.vesseltype.create;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;

public class CreateVesselTypeFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselTypeFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.create\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static VesselTypeEventType type = VesselTypeEventType.CREATE_VESSELTYPE_FAILED;

	public CreateVesselTypeFailedEvent() {
		super(type.name());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
