package es.redmic.vesselslib.events.vessel.partialupdate.vesseltype;

import java.util.UUID;

import org.apache.avro.Schema;

import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class UpdateVesselTypeInVesselEvent extends VesselTypeEvent {

	// @formatter:off

		public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
			+ "\"type\":\"record\",\"name\":\"UpdateVesselTypeInVesselEvent\","
					+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.partialupdate.vesseltype\",\"fields\":["
				+ getVesselTypeEventSchema() + ","
				+ getEventBaseSchema() + "]}");
		// @formatter:on

	static String type = VesselEventTypes.UPDATE_VESSELTYPE;

	public UpdateVesselTypeInVesselEvent() {
		super(type);
		setSessionId(UUID.randomUUID().toString());
	}

	public UpdateVesselTypeInVesselEvent(VesselTypeDTO vesselType) {
		super(type);
		this.setVesselType(vesselType);
		setSessionId(UUID.randomUUID().toString());
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}
