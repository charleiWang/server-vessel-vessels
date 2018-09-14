package es.redmic.vesselslib.events.vesseltype;

import es.redmic.brokerlib.avro.common.EventTypes;

public abstract class VesselTypeEventTypes extends EventTypes {

	public static boolean isLocked(String eventType) {

		return EventTypes.isLocked(eventType);
	}
}
