package es.redmic.vesselslib.events.vessel.partialupdate.vesseltype;

import org.apache.avro.Schema;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;

public class UpdateVesselTypeInVesselFailedEvent extends EventError {

	// @formatter:off

	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{"
		+ "\"type\":\"record\",\"name\":\"UpdateVesselTypeInVesselFailedEvent\","
				+ "\"namespace\":\"es.redmic.vesselslib.events.vessel.partialupdate.vesseltype\",\"fields\":["
			+ getFailEventSchema() + ","
			+ getEventBaseSchema() + "]}");
	// @formatter:on

	static String type = VesselEventTypes.UPDATE_VESSELTYPE_FAILED;

	public UpdateVesselTypeInVesselFailedEvent() {
		super(type);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}
}