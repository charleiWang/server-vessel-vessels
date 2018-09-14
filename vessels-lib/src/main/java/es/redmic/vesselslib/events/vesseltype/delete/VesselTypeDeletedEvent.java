package es.redmic.vesselslib.events.vesseltype.delete;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;

public class VesselTypeDeletedEvent extends SimpleEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"VesselTypeDeletedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.delete\",\"fields\":["
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.DELETED;

	public VesselTypeDeletedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
