package es.redmic.vesselscommands.aggregate;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.aggregate.Aggregate;
import es.redmic.exception.database.DBNotFoundException;
import es.redmic.vesselscommands.commands.CreateVesselCommand;
import es.redmic.vesselscommands.commands.DeleteVesselCommand;
import es.redmic.vesselscommands.commands.UpdateVesselCommand;
import es.redmic.vesselscommands.statestore.VesselStateStore;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;

public class VesselAggregate extends Aggregate {

	private VesselDTO vessel;

	private VesselStateStore vesselStateStore;

	public VesselAggregate(VesselStateStore vesselStateStore) {
		this.vesselStateStore = vesselStateStore;
	}

	public CreateVesselEvent process(CreateVesselCommand cmd) {

		assert vesselStateStore != null;

		String id = cmd.getVessel().getId();

		if (exist(id)) {
			logger.info("Descartando inserción de " + id + ". El item ya está registrado.");
			return null; // Se lanza excepción en el origen no aquí
		}
		this.setAggregateId(id);

		CreateVesselEvent evt = new CreateVesselEvent(cmd.getVessel());
		evt.setAggregateId(id);
		evt.setVersion(1);
		return evt;
	}

	public UpdateVesselEvent process(UpdateVesselCommand cmd) {

		assert vesselStateStore != null;

		String id = cmd.getVessel().getId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		UpdateVesselEvent evt = new UpdateVesselEvent(cmd.getVessel());
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public DeleteVesselEvent process(DeleteVesselCommand cmd) {

		assert vesselStateStore != null;

		String id = cmd.getVesselId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		DeleteVesselEvent evt = new DeleteVesselEvent();
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public VesselDTO getVessel() {
		return vessel;
	}

	public VesselDTO getVesselFromStateStore(VesselDTO type) {

		CreateVesselCommand cmd = new CreateVesselCommand(type);

		Event state = getItemFromStateStore(cmd.getVessel().getId());

		if (state == null) {
			throw new DBNotFoundException("id", cmd.getVessel().getId());
		}

		loadFromHistory(state);

		return getVessel();
	}

	@Override
	protected boolean isLocked(String eventType) {

		return VesselEventTypes.isLocked(eventType);
	}

	@Override
	protected Event getItemFromStateStore(String id) {

		return vesselStateStore.getVessel(id);
	}

	@Override
	public void loadFromHistory(Event history) {

		logger.debug("Cargando último estado del vessel ", history.getAggregateId());

		String eventType = history.getType();

		switch (eventType) {
		case "CREATE":
			logger.debug("En fase de creación");
			apply((VesselEvent) history);
			break;
		case "CREATED":
			logger.debug("Item creado");
			apply((VesselEvent) history);
			break;
		case "UPDATE":
			logger.debug("En fase de modificación");
			apply((VesselEvent) history);
			break;
		case "UPDATED":
			logger.debug("Item modificado");
			apply((VesselEvent) history);
			break;
		case "DELETED":
			logger.debug("Item borrado");
			apply((VesselDeletedEvent) history);
			break;
		// CANCELLED
		case "CREATE_CANCELLED":
			logger.debug("Compensación por creación fallida");
			apply((CreateVesselCancelledEvent) history);
			break;
		case "UPDATE_CANCELLED":
		case "DELETE_CANCELLED":
			logger.debug("Compensación por edición/borrado fallido");
			apply((VesselEvent) history);
			break;
		default:
			super._loadFromHistory(history);
			break;
		}
	}

	public void apply(CreateVesselCancelledEvent event) {
		this.deleted = true;
		apply(event);
	}

	public void apply(VesselDeletedEvent event) {
		this.deleted = true;
		super.apply(event);
	}

	public void apply(VesselEvent event) {
		this.vessel = event.getVessel();
		super.apply(event);
	}

	@Override
	protected void reset() {
		this.vessel = null;
		super.reset();
	}
}
