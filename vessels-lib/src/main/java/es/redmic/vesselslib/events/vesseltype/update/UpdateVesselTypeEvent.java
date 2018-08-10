package es.redmic.vesselslib.events.vesseltype.update;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class UpdateVesselTypeEvent extends VesselTypeEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselTypeEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.update\",\"fields\":["
			+ getVesselTypeEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.UPDATE;

	public UpdateVesselTypeEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public UpdateVesselTypeEvent(VesselTypeDTO vesselType) {
		super(type);
		this.setVesselType(vesselType);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
