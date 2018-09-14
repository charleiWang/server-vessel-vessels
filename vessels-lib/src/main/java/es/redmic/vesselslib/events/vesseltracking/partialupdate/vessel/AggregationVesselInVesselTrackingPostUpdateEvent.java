package es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;

public class AggregationVesselInVesselTrackingPostUpdateEvent extends VesselEvent {

	// @formatter:off

		public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
			+ "\"type\":\"record\",\"name\":\"AggregationVesselInVesselTrackingPostUpdateEvent\","
					+ "\"namespace\":\"es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel\",\"fields\":["
				+ getVesselEventSchema() + ","
				+ getEventBaseSchema() + "]}");
		// @formatter:on

	static String type = "AGGREGATION";

	public AggregationVesselInVesselTrackingPostUpdateEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public AggregationVesselInVesselTrackingPostUpdateEvent(String type) {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public AggregationVesselInVesselTrackingPostUpdateEvent(String type, VesselDTO vessel) {
		super(type);
		this.setVessel(vessel);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
