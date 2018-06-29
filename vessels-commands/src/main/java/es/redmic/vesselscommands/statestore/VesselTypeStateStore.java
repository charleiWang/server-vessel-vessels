package es.redmic.vesselscommands.statestore;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.statestore.StateStore;
import es.redmic.commandslib.statestore.StreamConfig;

public class VesselTypeStateStore extends StateStore {

	public VesselTypeStateStore(StreamConfig config) {
		super(config);
		logger.info("Arrancado servicio VesselTypeStateStore con Id: " + this.serviceId);
		init();
	}

	public Event getVesselType(String id) {

		return this.store.get(id);
	}
}