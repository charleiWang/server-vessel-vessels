package es.redmic.vesselslib.events.vesseltype.delete;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeCancelledEvent;

public class DeleteVesselTypeCancelledEvent extends VesselTypeCancelledEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"DeleteVesselTypeCancelledEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.delete\",\"fields\":["
			+ getVesselTypeEventSchema() + ","
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.DELETE_CANCELLED;

	public DeleteVesselTypeCancelledEvent() {
		super(type);
	}

	public DeleteVesselTypeCancelledEvent(VesselTypeDTO vesselType) {
		super(type);
		this.setVesselType(vesselType);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
