package es.redmic.vesselslib.events.vessel;

import es.redmic.brokerlib.avro.common.EventTypes;

public abstract class VesselEventTypes extends EventTypes {

	public static String
	// @formatter:off
		//UPDATE
		UPDATE_VESSELTYPE = "UPDATE_VESSELTYPE",
		UPDATE_VESSELTYPE_CONFIRMED = "UPDATE_VESSELTYPE_CONFIRMED",
		VESSELTYPE_UPDATED = "VESSELTYPE_UPDATED",
		UPDATE_VESSELTYPE_FAILED = "UPDATE_VESSELTYPE_FAILED",
		UPDATE_VESSELTYPE_CANCELLED = "UPDATE_VESSELTYPE_CANCELLED";
	//@formatter:on

	public static boolean isLocked(String eventType) {

		return (!(eventType.equals(VESSELTYPE_UPDATED.toString())
				|| eventType.equals(UPDATE_VESSELTYPE_CANCELLED.toString())) && EventTypes.isLocked(eventType));
	}
}
