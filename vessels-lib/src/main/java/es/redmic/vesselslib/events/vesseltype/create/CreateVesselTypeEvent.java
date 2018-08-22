package es.redmic.vesselslib.events.vesseltype.create;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class CreateVesselTypeEvent extends VesselTypeEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"CreateVesselTypeEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltype.create\",\"fields\":["
			+ getVesselTypeEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselTypeEventTypes.CREATE;

	public CreateVesselTypeEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public CreateVesselTypeEvent(VesselTypeDTO vesselType) {
		super(type);
		this.setVesselType(vesselType);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
