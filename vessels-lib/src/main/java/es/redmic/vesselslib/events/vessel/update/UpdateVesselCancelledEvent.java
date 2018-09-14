package es.redmic.vesselslib.events.vessel.update;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselCancelledEvent;

public class UpdateVesselCancelledEvent extends VesselCancelledEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselCancelledEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.update\",\"fields\":["
			+ getVesselEventSchema() + ","
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.UPDATE_CANCELLED;

	public UpdateVesselCancelledEvent() {
		super(type);
	}

	public UpdateVesselCancelledEvent(VesselDTO vessel) {
		super(type);
		this.setVessel(vessel);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
