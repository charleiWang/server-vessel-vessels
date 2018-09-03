package es.redmic.vesselslib.events.vessel.create;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;

public class EnrichCreateVesselEvent extends VesselEvent {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"EnrichCreateVesselEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.create\",\"fields\":["
			+ getVesselEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.ENRICH_CREATE;

	public EnrichCreateVesselEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public EnrichCreateVesselEvent(VesselDTO vessel) {
		super(type);
		this.setVessel(vessel);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
