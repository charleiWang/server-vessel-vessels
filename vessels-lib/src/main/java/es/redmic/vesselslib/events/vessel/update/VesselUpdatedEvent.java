package es.redmic.vesselslib.events.vessel.update;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;

public class VesselUpdatedEvent extends VesselEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"VesselUpdatedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.update\",\"fields\":["
			+ getVesselEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.UPDATED;

	public VesselUpdatedEvent() {
		super(type);
	}

	public VesselUpdatedEvent(VesselDTO vessel) {
		super(type);
		this.setVessel(vessel);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
