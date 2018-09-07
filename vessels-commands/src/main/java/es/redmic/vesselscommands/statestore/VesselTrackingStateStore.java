package es.redmic.vesselscommands.statestore;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.statestore.StateStore;

public class VesselTrackingStateStore extends StateStore {

	public VesselTrackingStateStore(StreamConfig config, AlertService alertService) {
		super(config, alertService);
		logger.info("Arrancado servicio VesselTrackingStateStore con Id: " + this.serviceId);
		init();
	}

	public Event getVesselTracking(String id) {

		return store.get(id);
	}
}
