package es.redmic.vesselslib.events.vesseltracking;

import es.redmic.brokerlib.avro.common.EventTypes;

public abstract class VesselTrackingEventTypes extends EventTypes {

	public static String
	// @formatter:off
		UPDATE_VESSEL = "UPDATE_VESSEL";
	//@formatter:on

	public static boolean isLocked(String eventType) {

		return EventTypes.isLocked(eventType);
	}
}
