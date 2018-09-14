package es.redmic.vesselslib.events.vesseltype.delete;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;

public class DeleteVesselTypeEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"DeleteVesselTypeEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.delete\",\"fields\":["
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.DELETE;

	public DeleteVesselTypeEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
