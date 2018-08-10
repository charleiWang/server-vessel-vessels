package es.redmic.vesselslib.events.vessel.update;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;

public class UpdateVesselEvent extends VesselEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.update\",\"fields\":["
			+ getVesselEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.UPDATE;

	public UpdateVesselEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public UpdateVesselEvent(VesselDTO vessel) {
		super(type);
		this.setVessel(vessel);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
