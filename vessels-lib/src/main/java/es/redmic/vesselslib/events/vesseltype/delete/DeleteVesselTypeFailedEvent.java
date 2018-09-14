package es.redmic.vesselslib.events.vesseltype.delete;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;

public class DeleteVesselTypeFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"DeleteVesselTypeFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.delete\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.DELETE_FAILED;

	public DeleteVesselTypeFailedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
