package es.redmic.vesselslib.events.vessel.create;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;

public class VesselCreatedEvent extends VesselEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"VesselCreatedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.create\",\"fields\":["
			+ getVesselEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.CREATED;

	public VesselCreatedEvent() {
		super(type);
	}

	public VesselCreatedEvent(VesselDTO vessel) {
		super(type);
		this.setVessel(vessel);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
