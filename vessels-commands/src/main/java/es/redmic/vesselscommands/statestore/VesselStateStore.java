package es.redmic.vesselscommands.statestore;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.statestore.StateStore;
import es.redmic.commandslib.statestore.StreamConfig;

public class VesselStateStore extends StateStore {

	public VesselStateStore(StreamConfig config) {
		super(config);
		logger.info("Arrancado servicio VesselStateStore con Id: " + this.serviceId);
		init();
	}

	public Event getVessel(String id) {

		return store.get(id);
	}
}
