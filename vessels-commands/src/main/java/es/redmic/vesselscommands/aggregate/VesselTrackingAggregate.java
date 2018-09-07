package es.redmic.vesselscommands.aggregate;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.aggregate.Aggregate;
import es.redmic.vesselscommands.commands.vesseltracking.CreateVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.DeleteVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.UpdateVesselTrackingCommand;
import es.redmic.vesselscommands.statestore.VesselTrackingStateStore;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingPropertiesDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.create.EnrichCreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel.UpdateVesselInVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.EnrichUpdateVesselTrackingEvent;

public class VesselTrackingAggregate extends Aggregate {

	private VesselTrackingDTO vesselTracking;

	private VesselTrackingStateStore vesselTrackingStateStore;

	public VesselTrackingAggregate(VesselTrackingStateStore vesselTrackingStateStore) {
		this.vesselTrackingStateStore = vesselTrackingStateStore;
	}

	public VesselTrackingEvent process(CreateVesselTrackingCommand cmd) {

		assert vesselTrackingStateStore != null;

		String id = cmd.getVesselTracking().getId();

		if (exist(id)) {
			logger.info("Descartando inserción de " + id + ". El item ya está registrado.");
			return null; // Se lanza excepción en el origen no aquí
		}
		this.setAggregateId(id);

		VesselDTO vessel = cmd.getVesselTracking().getProperties().getVessel();

		// Si vessel es null no se guarda el track
		if (vessel == null)
			return null;

		logger.info("Creando evento para enriquecer VesselTracking");

		VesselTrackingEvent evt = new EnrichCreateVesselTrackingEvent(cmd.getVesselTracking());
		evt.setAggregateId(id);
		evt.setVersion(1);
		return evt;
	}

	public VesselTrackingEvent process(UpdateVesselTrackingCommand cmd) {

		assert vesselTrackingStateStore != null;

		String id = cmd.getVesselTracking().getId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		VesselDTO vessel = cmd.getVesselTracking().getProperties().getVessel();

		// Si vessel es null no se guarda el track
		if (vessel == null)
			return null;

		logger.info("Creando evento para enriquecer VesselTracking");
		EnrichUpdateVesselTrackingEvent evt = new EnrichUpdateVesselTrackingEvent(cmd.getVesselTracking());

		evt.setAggregateId(id);
		evt.setVersion(2);
		return evt;
	}

	public DeleteVesselTrackingEvent process(DeleteVesselTrackingCommand cmd) {

		assert vesselTrackingStateStore != null;

		String id = cmd.getVesselTrackingId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		DeleteVesselTrackingEvent evt = new DeleteVesselTrackingEvent();
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public VesselTrackingDTO getVesselTracking() {
		return vesselTracking;
	}

	@Override
	protected boolean isLocked(String eventType) {

		return VesselTrackingEventTypes.isLocked(eventType);
	}

	@Override
	protected Event getItemFromStateStore(String id) {

		return vesselTrackingStateStore.getVesselTracking(id);
	}

	@Override
	public void loadFromHistory(Event history) {

		logger.debug("Cargando último estado del vesselTracking ", history.getAggregateId());

		String eventType = history.getType();

		// TODO: Si se trata de un evento no final, controlar el error.

		switch (eventType) {
		case "CREATE":
			logger.debug("En fase de creación");
			apply((VesselTrackingEvent) history);
			break;
		case "CREATED":
			logger.debug("Item creado");
			apply((VesselTrackingEvent) history);
			break;
		case "UPDATE":
			logger.debug("En fase de modificación");
			apply((VesselTrackingEvent) history);
			break;
		case "UPDATED":
			logger.debug("Item modificado");
			apply((VesselTrackingEvent) history);
			break;
		case "DELETED":
			logger.debug("Item borrado");
			apply((VesselTrackingDeletedEvent) history);
			break;
		// CANCELLED
		case "CREATE_CANCELLED":
			logger.debug("Compensación por creación fallida");
			apply(history);
			break;
		case "UPDATE_CANCELLED":
		case "DELETE_CANCELLED":
			logger.debug("Compensación por edición/borrado fallido");
			apply((VesselTrackingEvent) history);
			break;
		case "UPDATE_VESSELTYPE":
			logger.debug("En fase de edición parcial de veseltype en vessel");
			apply(history);
			break;
		default:
			super._loadFromHistory(history);
			break;
		}
	}

	public void apply(UpdateVesselInVesselTrackingEvent event) {
		if (this.vesselTracking == null)
			this.vesselTracking = new VesselTrackingDTO();

		VesselTrackingPropertiesDTO properties = new VesselTrackingPropertiesDTO();

		properties.setVessel(event.getVessel());

		this.vesselTracking.setProperties(properties);

		super.apply(event);
	}

	public void apply(CreateVesselTrackingCancelledEvent event) {
		this.deleted = true;
		apply(event);
	}

	public void apply(VesselTrackingDeletedEvent event) {
		this.deleted = true;
		super.apply(event);
	}

	public void apply(VesselTrackingEvent event) {
		this.vesselTracking = event.getVesselTracking();
		super.apply(event);
	}

	@Override
	protected void reset() {
		this.vesselTracking = null;
		super.reset();
	}
}
