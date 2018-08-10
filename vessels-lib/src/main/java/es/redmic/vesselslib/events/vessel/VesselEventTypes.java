package es.redmic.vesselslib.events.vessel;

import es.redmic.brokerlib.avro.common.EventTypes;

public abstract class VesselEventTypes extends EventTypes {

	// public static String

	public static boolean isLocked(String eventType) {

		return EventTypes.isLocked(eventType);
	}
}
