package es.redmic.vesselslib.events.vessel.create;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;

public class CreateVesselCancelledEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselCancelledEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.create\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.CREATE_CANCELLED;

	public CreateVesselCancelledEvent() {
		super(type);
	}

	public CreateVesselCancelledEvent(VesselDTO vessel) {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
