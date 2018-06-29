package es.redmic.vesselslib.events.vesseltype.update;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;
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

	static VesselTypeEventType type = VesselTypeEventType.UPDATE_VESSELTYPE_CANCELLED;

	public UpdateVesselTypeCancelledEvent() {
		super(type.name());
	}

	public UpdateVesselTypeCancelledEvent(VesselTypeDTO vesselType) {
		super(type.name());
		this.setVesselType(vesselType);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
