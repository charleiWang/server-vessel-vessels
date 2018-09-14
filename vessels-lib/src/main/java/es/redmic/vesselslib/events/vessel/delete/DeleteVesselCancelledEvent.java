package es.redmic.vesselslib.events.vessel.delete;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselCancelledEvent;

public class DeleteVesselCancelledEvent extends VesselCancelledEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"DeleteVesselCancelledEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.delete\",\"fields\":["
			+ getVesselEventSchema() + ","
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.DELETE_CANCELLED;

	public DeleteVesselCancelledEvent() {
		super(type);
	}

	public DeleteVesselCancelledEvent(VesselDTO vessel) {
		super(type);
		this.setVessel(vessel);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
