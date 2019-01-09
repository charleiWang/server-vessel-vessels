package es.redmic.vesselslib.events.vesseltype;

import es.redmic.brokerlib.avro.common.EventTypes;

public abstract class VesselTypeEventTypes extends EventTypes {

	public static boolean isLocked(String eventType) {

		return EventTypes.isLocked(eventType);
	}

	public static boolean isSnapshot(String eventType) {

		return EventTypes.isSnapshot(eventType);
	}

	public static boolean isUpdatable(String eventType) {

		return EventTypes.isUpdatable(eventType);
	}
}
