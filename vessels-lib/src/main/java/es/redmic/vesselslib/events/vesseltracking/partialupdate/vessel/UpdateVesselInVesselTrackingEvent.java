package es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;

public class UpdateVesselInVesselTrackingEvent extends VesselEvent {

	// @formatter:off

		public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
			+ "\"type\":\"record\",\"name\":\"UpdateVesselInVesselTrackingEvent\","
					+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel\",\"fields\":["
				+ getVesselEventSchema() + ","
				+ getEventBaseSchema() + "]}");
		// @formatter:on

	static String type = VesselTrackingEventTypes.UPDATE_VESSEL;

	public UpdateVesselInVesselTrackingEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public UpdateVesselInVesselTrackingEvent(VesselDTO vessel) {
		super(type);
		this.setVessel(vessel);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
