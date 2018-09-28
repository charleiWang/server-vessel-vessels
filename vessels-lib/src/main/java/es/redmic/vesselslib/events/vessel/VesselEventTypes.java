package es.redmic.vesselslib.events.vessel;

import es.redmic.brokerlib.avro.common.EventTypes;

public abstract class VesselEventTypes extends EventTypes {

	public static String
	// @formatter:off
		UPDATE_VESSELTYPE = "UPDATE_VESSELTYPE";
	//@formatter:on

	public static boolean isLocked(String eventType) {

		return EventTypes.isLocked(eventType);
	}

	public static boolean isSnapshot(String eventType) {

		return EventTypes.isSnapshot(eventType);
	}
}
