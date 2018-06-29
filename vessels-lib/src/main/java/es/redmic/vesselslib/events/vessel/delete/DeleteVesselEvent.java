package es.redmic.vesselslib.events.vessel.delete;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.vesselslib.events.vessel.VesselEventType;

public class DeleteVesselEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"DeleteVesselEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.delete\",\"fields\":["
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static VesselEventType type = VesselEventType.DELETE_VESSEL;

	public DeleteVesselEvent() {
		super(type.name());
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
