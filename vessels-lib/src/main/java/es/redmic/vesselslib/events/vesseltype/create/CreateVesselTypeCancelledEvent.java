package es.redmic.vesselslib.events.vesseltype.create;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;

public class CreateVesselTypeCancelledEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselTypeCancelledEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.create\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.CREATE_CANCELLED;

	public CreateVesselTypeCancelledEvent() {
		super(type);
	}

	public CreateVesselTypeCancelledEvent(VesselTypeDTO vesselType) {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
