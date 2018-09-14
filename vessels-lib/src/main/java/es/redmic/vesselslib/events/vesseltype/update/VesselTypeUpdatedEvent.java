package es.redmic.vesselslib.events.vesseltype.update;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class VesselTypeUpdatedEvent extends VesselTypeEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"VesselTypeUpdatedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.update\",\"fields\":["
			+ getVesselTypeEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.UPDATED;

	public VesselTypeUpdatedEvent() {
		super(type);
	}

	public VesselTypeUpdatedEvent(VesselTypeDTO vesselType) {
		super(type);
		this.setVesselType(vesselType);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
