package es.redmic.vesselscommands.aggregate;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.commandslib.aggregate.Aggregate;
import es.redmic.commandslib.exceptions.HistoryNotFoundException;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.exception.database.DBNotFoundException;
import es.redmic.vesselscommands.commands.CreateVesselCommand;
import es.redmic.vesselscommands.commands.DeleteVesselCommand;
import es.redmic.vesselscommands.commands.UpdateVesselCommand;
import es.redmic.vesselscommands.statestore.VesselStateStore;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventType;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;

public class VesselAggregate extends Aggregate {

	private VesselDTO vessel;

	private VesselStateStore vesselStateStore;

	public VesselAggregate(VesselStateStore vesselStateStore) {
		this.vesselStateStore = vesselStateStore;
	}

	public CreateVesselEvent process(CreateVesselCommand cmd) {

		assert vesselStateStore != null;

		String vesselId = cmd.getVessel().getId();

		if (vesselId != null) {
			// comprueba que el MMSI no esté introducido
			Event state = vesselStateStore.getVessel(vesselId);

			if (state != null) {

				loadFromHistory(state);

				if (!isDeleted()) {
					logger.info("Descartando barco " + vesselId + ". El barco ya está registrado.");
					return null; // Se lanza excepción en el origen no aquí
				}
				reset();
			}
		}

		this.setAggregateId(vesselId);

		CreateVesselEvent evt = new CreateVesselEvent(cmd.getVessel());
		evt.setAggregateId(vesselId);
		evt.setVersion(1);
		return evt;
	}

	public UpdateVesselEvent process(UpdateVesselCommand cmd) {

		assert vesselStateStore != null;

		String vesselId = cmd.getVessel().getId();

		Event state = vesselStateStore.getVessel(vesselId);

		if (state == null) {
			logger.error("Intentando modificar un elemento del cual no se tiene historial, ", vesselId);
			throw new HistoryNotFoundException(VesselEventType.UPDATE_VESSEL.toString(), vesselId);
		}

		loadFromHistory(state);

		if (this.deleted) {
			logger.error("Intentando modificar un elemento eliminado, ", vesselId);
			throw new ItemNotFoundException("id", vesselId);
		}

		if (itemIsLocked(state.getType())) {
			logger.error("Intentando modificar un elemento bloqueado por una edición en curso, ", vesselId);
			throw new ItemLockedException("id", vesselId);
		}

		UpdateVesselEvent evt = new UpdateVesselEvent(cmd.getVessel());
		evt.setAggregateId(vesselId);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public DeleteVesselEvent process(DeleteVesselCommand cmd) {

		assert vesselStateStore != null;

		String vesselId = cmd.getVesselId();

		Event state = vesselStateStore.getVessel(vesselId);

		if (state == null) {
			logger.error("Intentando eliminar un elemento del cual no se tiene historial, " + vesselId);
			throw new HistoryNotFoundException(VesselEventType.DELETE_VESSEL.toString(), vesselId);
		}

		loadFromHistory(state);

		if (this.deleted) {
			logger.error("Intentando eliminar un elemento que ya está eliminado, ", vesselId);
			throw new ItemNotFoundException("id", vesselId);
		}

		if (itemIsLocked(state.getType())) {
			logger.error("Intentando eliminar un elemento bloqueado por una edición en curso, ", vesselId);
			throw new ItemLockedException("id", vesselId);
		}

		DeleteVesselEvent evt = new DeleteVesselEvent();
		evt.setAggregateId(vesselId);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public VesselDTO getVessel() {
		return vessel;
	}

	public VesselDTO getVesselFromStateStore(VesselDTO type) {

		CreateVesselCommand cmd = new CreateVesselCommand(type);

		Event state = vesselStateStore.getVessel(cmd.getVessel().getId());

		if (state == null) {
			throw new DBNotFoundException("id", cmd.getVessel().getId());
		}

		loadFromHistory(state);

		return getVessel();
	}

	@Override
	public void loadFromHistory(Event history) {

		logger.debug("Cargando último estado del vessel ", history.getAggregateId());

		String eventType = history.getType();

		switch (VesselEventType.valueOf(eventType)) {
		case CREATE_VESSEL:
			apply((CreateVesselEvent) history);
			break;
		case CREATE_VESSEL_CONFIRMED:
			apply((CreateVesselConfirmedEvent) history);
			break;
		case VESSEL_CREATED:
			apply((VesselCreatedEvent) history);
			break;
		case UPDATE_VESSEL:
			apply((UpdateVesselEvent) history);
			break;
		case UPDATE_VESSEL_CONFIRMED:
			apply((UpdateVesselConfirmedEvent) history);
			break;
		case VESSEL_UPDATED:
			apply((VesselUpdatedEvent) history);
			break;
		case DELETE_VESSEL:
			apply((DeleteVesselEvent) history);
			break;
		case DELETE_VESSEL_CONFIRMED:
			apply((DeleteVesselConfirmedEvent) history);
			break;
		case VESSEL_DELETED:
			apply((VesselDeletedEvent) history);
			break;
		// FAILED
		case CREATE_VESSEL_FAILED:
		case UPDATE_VESSEL_FAILED:
		case DELETE_VESSEL_FAILED:
			logger.debug("Evento fallido");
			_apply((SimpleEvent) history);
			break;
		// CANCELLED
		case CREATE_VESSEL_CANCELLED:
			apply((CreateVesselCancelledEvent) history);
			break;
		case UPDATE_VESSEL_CANCELLED:
		case DELETE_VESSEL_CANCELLED:
			logger.debug("Compensación por edición/borrado fallido");
			_apply((VesselEvent) history);
			break;
		default:
			logger.debug("Evento no manejado ", history.getType());
			break;
		}
	}

	public void apply(CreateVesselEvent event) {
		logger.debug("En fase de creación");
		_apply(event);
	}

	public void apply(CreateVesselConfirmedEvent event) {
		logger.debug("Creación confirmada");
		_apply(event);
	}

	public void apply(VesselCreatedEvent event) {
		logger.debug("Item creado");
		_apply(event);
	}

	public void apply(CreateVesselCancelledEvent event) {
		logger.debug("Compensación por creación fallida");
		this.deleted = true;
		setVersion(event.getVersion());
		setAggregateId(event.getAggregateId());
	}

	public void apply(UpdateVesselEvent event) {
		logger.debug("En fase de modificación");
		_apply(event);
	}

	public void apply(UpdateVesselConfirmedEvent event) {
		logger.debug("Modificación confirmada");
		_apply(event);
	}

	public void apply(VesselUpdatedEvent event) {
		logger.debug("Item modificado");
		_apply(event);
	}

	public void apply(DeleteVesselEvent event) {
		logger.debug("En fase de borrado");
		this.deleted = true;
		_apply(event);
	}

	public void apply(DeleteVesselConfirmedEvent event) {
		logger.debug("Borrado confirmado");
		this.deleted = true;
		_apply(event);
	}

	public void apply(VesselDeletedEvent event) {
		logger.debug("Item borrado");
		this.deleted = true;
		_apply(event);
	}

	private void _apply(VesselEvent event) {
		this.vessel = event.getVessel();
		setVersion(event.getVersion());
		setAggregateId(event.getAggregateId());
	}

	@Override
	protected void reset() {
		this.vessel = null;
		super.reset();
	}

	private boolean itemIsLocked(String eventType) {

		return !(eventType.equals(VesselEventType.VESSEL_CREATED.toString())
				|| eventType.equals(VesselEventType.VESSEL_UPDATED.toString())
				|| eventType.equals(VesselEventType.CREATE_VESSEL_CANCELLED.toString())
				|| eventType.equals(VesselEventType.UPDATE_VESSEL_CANCELLED.toString())
				|| eventType.equals(VesselEventType.DELETE_VESSEL_CANCELLED.toString()));
	}
}
