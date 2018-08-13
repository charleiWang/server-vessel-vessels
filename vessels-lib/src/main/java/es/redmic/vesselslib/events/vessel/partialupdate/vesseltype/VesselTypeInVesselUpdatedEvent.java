package es.redmic.vesselslib.events.vessel.partialupdate.vesseltype;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class VesselTypeInVesselUpdatedEvent extends VesselTypeEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"VesselTypeInVesselUpdatedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.partialupdate.vesseltype\",\"fields\":["
			+ getVesselTypeEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.VESSELTYPE_UPDATED;

	public VesselTypeInVesselUpdatedEvent() {
		super(type);
	}

	public VesselTypeInVesselUpdatedEvent(VesselTypeDTO vesselType) {
		super(type);
		this.setVesselType(vesselType);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}