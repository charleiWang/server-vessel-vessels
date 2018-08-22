package es.redmic.vesselslib.events.vessel.partialupdate.vesseltype;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class AggregationVesselTypeInVesselPostUpdateEvent extends VesselTypeEvent {

	// @formatter:off

		public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
			+ "\"type\":\"record\",\"name\":\"AggregationVesselTypeInVesselPostUpdateEvent\","
					+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.partialupdate.vesseltype\",\"fields\":["
				+ getVesselTypeEventSchema() + ","
				+ getEventBaseSchema() + "]}");
		// @formatter:on

	static String type = "AGGREGATION";

	public AggregationVesselTypeInVesselPostUpdateEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public AggregationVesselTypeInVesselPostUpdateEvent(String type) {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public AggregationVesselTypeInVesselPostUpdateEvent(String type, VesselTypeDTO vesselType) {
		super(type);
		this.setVesselType(vesselType);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
