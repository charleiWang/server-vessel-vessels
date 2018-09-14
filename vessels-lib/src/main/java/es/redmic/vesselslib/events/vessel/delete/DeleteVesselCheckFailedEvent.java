package es.redmic.vesselslib.events.vessel.delete;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;

public class DeleteVesselCheckFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"DeleteVesselCheckFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.delete\",\"fields\":["
		+ getFailEventSchema() + ","
		+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.DELETE_CHECK_FAILED;

	public DeleteVesselCheckFailedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
