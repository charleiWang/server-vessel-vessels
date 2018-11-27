package es.redmic.vesselscommands.aggregate;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.aggregate.Aggregate;
import es.redmic.vesselscommands.commands.vesseltracking.CreateVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.DeleteVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.UpdateVesselTrackingCommand;
import es.redmic.vesselscommands.statestore.VesselTrackingStateStore;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.EnrichCreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
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

		VesselTrackingEvent evt;

		// Si el único campo relleno es el id (insertando vía rest)
		if (vessel.getMmsi() == null) {
			logger.debug("Generando evento para enriquecer VesselTracking");
			evt = new EnrichCreateVesselTrackingEvent(cmd.getVesselTracking());
		} else { // Si tiene más campos rellenos, es una inserción automatizada, por lo que no se
					// enriquece
			logger.debug("Generando evento para crear VesselTracking");
			evt = new CreateVesselTrackingEvent(cmd.getVesselTracking());
		}

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
	public void loadFromHistory(Event event) {

		logger.debug("Cargando último estado del vesselTracking ", event.getAggregateId());

		check(event);

		String eventType = event.getType();

		switch (eventType) {
		case "CREATED":
			logger.debug("Item creado");
			apply((VesselTrackingEvent) event);
			break;
		case "UPDATED":
			logger.debug("Item modificado");
			apply((VesselTrackingEvent) event);
			break;
		case "DELETED":
			logger.debug("Item borrado");
			apply((VesselTrackingDeletedEvent) event);
			break;
		// CANCELLED
		case "CREATE_CANCELLED":
			logger.debug("Compensación por creación fallida");
			apply(event);
			break;
		case "UPDATE_CANCELLED":
		case "DELETE_CANCELLED":
			logger.debug("Compensación por edición/borrado fallido");
			apply((VesselTrackingEvent) event);
			break;
		default:
			logger.debug("Evento no manejado ", event.getType());
			break;
		}
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
