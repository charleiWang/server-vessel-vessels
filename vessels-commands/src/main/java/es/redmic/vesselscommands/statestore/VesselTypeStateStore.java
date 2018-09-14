package es.redmic.vesselscommands.statestore;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.statestore.StateStore;

public class VesselTypeStateStore extends StateStore {

	public VesselTypeStateStore(StreamConfig config, AlertService alertService) {
		super(config, alertService);
		logger.info("Arrancado servicio VesselTypeStateStore con Id: " + this.serviceId);
		init();
	}

	public Event getVesselType(String id) {

		return this.store.get(id);
	}
}
