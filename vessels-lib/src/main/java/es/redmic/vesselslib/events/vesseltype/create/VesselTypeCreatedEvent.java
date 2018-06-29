package es.redmic.vesselslib.events.vesseltype.create;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class VesselTypeCreatedEvent extends VesselTypeEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"VesselTypeCreatedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.create\",\"fields\":["
			+ getVesselTypeEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static VesselTypeEventType type = VesselTypeEventType.VESSELTYPE_CREATED;

	public VesselTypeCreatedEvent() {
		super(type.name());
	}

	public VesselTypeCreatedEvent(VesselTypeDTO vesselType) {
		super(type.name());
		this.setVesselType(vesselType);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
