package es.redmic.vesselscommands.statestore;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.statestore.StateStore;

public class VesselStateStore extends StateStore {

	public VesselStateStore(StreamConfig config, AlertService alertService) {
		super(config, alertService);
		logger.info("Arrancado servicio VesselStateStore con Id: " + this.serviceId);
		init();
	}

	public Event getVessel(String id) {

		return store.get(id);
	}
}
