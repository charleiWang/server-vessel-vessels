package es.redmic.vesselslib.events.vesseltype.update;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeCancelledEvent;

public class UpdateVesselTypeCancelledEvent extends VesselTypeCancelledEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselTypeCancelledEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.update\",\"fields\":["
			+ getVesselTypeEventSchema() + ","
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.UPDATE_CANCELLED;

	public UpdateVesselTypeCancelledEvent() {
		super(type);
	}

	public UpdateVesselTypeCancelledEvent(VesselTypeDTO vesselType) {
		super(type);
		this.setVesselType(vesselType);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
