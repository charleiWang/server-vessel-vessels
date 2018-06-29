package es.redmic.vesselscommands.aggregate;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.SimpleEvent;
import es.redmic.commandslib.aggregate.Aggregate;
import es.redmic.commandslib.exceptions.HistoryNotFoundException;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.exception.database.DBNotFoundException;
import es.redmic.vesselscommands.commands.CreateVesselTypeCommand;
import es.redmic.vesselscommands.commands.DeleteVesselTypeCommand;
import es.redmic.vesselscommands.commands.UpdateVesselTypeCommand;
import es.redmic.vesselscommands.statestore.VesselTypeStateStore;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

public class VesselTypeAggregate extends Aggregate {

	private VesselTypeDTO vesselType;

	private VesselTypeStateStore vesselTypeStateStore;

	public VesselTypeAggregate(VesselTypeStateStore vesselTypeStateStore) {

		this.vesselTypeStateStore = vesselTypeStateStore;
	}

	public CreateVesselTypeEvent process(CreateVesselTypeCommand cmd) {

		assert vesselTypeStateStore != null;

		String vesselTypeId = cmd.getVesselType().getId();

		Event state = vesselTypeStateStore.getVesselType(vesselTypeId);

		if (state != null) {

			loadFromHistory(state);

			if (!isDeleted()) {
				logger.info("Descartando vessel type " + vesselTypeId + ". Ya está registrado.");
				return null; // Se lanza excepción en el origen no aquí
			}

			reset();
		}

		this.setAggregateId(vesselTypeId);

		CreateVesselTypeEvent evt = new CreateVesselTypeEvent(cmd.getVesselType());
		evt.setAggregateId(vesselTypeId);
		evt.setVersion(1);
		return evt;
	}

	public UpdateVesselTypeEvent process(UpdateVesselTypeCommand cmd) {

		assert vesselTypeStateStore != null;

		String vesselTypeId = cmd.getVesselType().getId();

		Event state = vesselTypeStateStore.getVesselType(vesselTypeId);

		if (state == null) {
			logger.error("Intentando modificar un elemento del cual no se tiene historial", vesselTypeId);
			throw new HistoryNotFoundException(VesselTypeEventType.UPDATE_VESSELTYPE.toString(), vesselTypeId);
		}

		loadFromHistory(state);

		if (this.deleted) {
			logger.error("Intentando editar un elemento que ya está eliminado, ", vesselTypeId);
			throw new ItemNotFoundException("id", vesselTypeId);
		}

		if (itemIsLocked(state.getType())) {
			logger.error("Intentando modificar un elemento bloqueado por una edición en curso, ", vesselTypeId);
			throw new ItemLockedException("id", vesselTypeId);
		}

		UpdateVesselTypeEvent evt = new UpdateVesselTypeEvent(cmd.getVesselType());
		evt.setAggregateId(vesselTypeId);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public DeleteVesselTypeEvent process(DeleteVesselTypeCommand cmd) {

		assert vesselTypeStateStore != null;

		String vesselTypeId = cmd.getVesselTypeId();

		Event state = vesselTypeStateStore.getVesselType(vesselTypeId);

		if (state == null) {
			logger.error("Intentando eliminar un elemento del cual no se tiene historial, " + vesselTypeId);
			throw new HistoryNotFoundException(VesselTypeEventType.UPDATE_VESSELTYPE.toString(), vesselTypeId);
		}

		loadFromHistory(state);

		if (this.deleted) {
			logger.error("Intentando eliminar un elemento que ya está eliminado, ", vesselTypeId);
			throw new ItemNotFoundException("id", vesselTypeId);
		}

		if (itemIsLocked(state.getType())) {
			logger.error("Intentando eliminar un elemento bloqueado por una edición en curso, ", vesselTypeId);
			throw new ItemLockedException("id", vesselTypeId);
		}

		DeleteVesselTypeEvent evt = new DeleteVesselTypeEvent();
		evt.setAggregateId(vesselTypeId);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public VesselTypeDTO getVesselType() {
		return vesselType;
	}

	public VesselTypeDTO getVesselTypeFromStateStore(VesselTypeDTO type) {

		CreateVesselTypeCommand cmd = new CreateVesselTypeCommand(type);

		Event state = vesselTypeStateStore.getVesselType(cmd.getVesselType().getId());

		if (state == null) {
			throw new DBNotFoundException("id", cmd.getVesselType().getId());
		}

		loadFromHistory(state);

		return getVesselType();
	}

	@Override
	public void loadFromHistory(Event history) {

		logger.debug("Cargando último estado del vessel type ", history.getAggregateId());

		String eventType = history.getType();

		switch (VesselTypeEventType.valueOf(eventType)) {
		case CREATE_VESSELTYPE:
			apply((CreateVesselTypeEvent) history);
			break;
		case CREATE_VESSELTYPE_CONFIRMED:
			apply((CreateVesselTypeConfirmedEvent) history);
			break;
		case VESSELTYPE_CREATED:
			apply((VesselTypeCreatedEvent) history);
			break;
		case UPDATE_VESSELTYPE:
			apply((UpdateVesselTypeEvent) history);
			break;
		case UPDATE_VESSELTYPE_CONFIRMED:
			apply((UpdateVesselTypeConfirmedEvent) history);
			break;
		case VESSELTYPE_UPDATED:
			apply((VesselTypeUpdatedEvent) history);
			break;
		case DELETE_VESSELTYPE:
			apply((DeleteVesselTypeEvent) history);
			break;
		case DELETE_VESSELTYPE_CONFIRMED:
			apply((DeleteVesselTypeConfirmedEvent) history);
			break;
		case VESSELTYPE_DELETED:
			apply((VesselTypeDeletedEvent) history);
			break;
		// FAILED
		case CREATE_VESSELTYPE_FAILED:
		case UPDATE_VESSELTYPE_FAILED:
		case DELETE_VESSELTYPE_FAILED:
			logger.debug("Evento fallido");
			_apply((SimpleEvent) history);
			break;
		// CANCELLED
		case CREATE_VESSELTYPE_CANCELLED:
			apply((CreateVesselTypeCancelledEvent) history);
			break;
		case UPDATE_VESSELTYPE_CANCELLED:
		case DELETE_VESSELTYPE_CANCELLED:
			logger.debug("Compensación por edición/borrado fallido");
			_apply((VesselTypeEvent) history);
			break;
		default:
			break;
		}
	}

	public void apply(CreateVesselTypeEvent event) {
		logger.debug("En fase de creación");
		_apply(event);
	}

	public void apply(CreateVesselTypeConfirmedEvent event) {
		logger.debug("Creación confirmada");
		_apply(event);
	}

	public void apply(VesselTypeCreatedEvent event) {
		logger.debug("Item creado");
		_apply(event);
	}

	public void apply(CreateVesselTypeCancelledEvent event) {
		logger.debug("Compensación por creación fallida");
		this.deleted = true;
		setVersion(event.getVersion());
		setAggregateId(event.getAggregateId());
	}

	public void apply(UpdateVesselTypeEvent event) {
		logger.debug("En fase de modificación");
		_apply(event);
	}

	public void apply(UpdateVesselTypeConfirmedEvent event) {
		logger.debug("Modificación confirmada");
		_apply(event);
	}

	public void apply(VesselTypeUpdatedEvent event) {
		logger.debug("Item modificado");
		_apply(event);
	}

	public void apply(DeleteVesselTypeEvent event) {
		logger.debug("En fase de borrado");
		this.deleted = true;
		_apply(event);
	}

	public void apply(DeleteVesselTypeConfirmedEvent event) {
		logger.debug("Borrado confirmado");
		this.deleted = true;
		_apply(event);
	}

	public void apply(VesselTypeDeletedEvent event) {
		logger.debug("Item borrado");
		this.deleted = true;
		_apply(event);
	}

	private void _apply(VesselTypeEvent event) {
		this.vesselType = event.getVesselType();
		setVersion(event.getVersion());
		setAggregateId(event.getAggregateId());
	}

	@Override
	protected void reset() {
		this.vesselType = null;
		super.reset();
	}

	private boolean itemIsLocked(String eventType) {

		return !(eventType.equals(VesselTypeEventType.VESSELTYPE_CREATED.toString())
				|| eventType.equals(VesselTypeEventType.VESSELTYPE_UPDATED.toString())
				|| eventType.equals(VesselTypeEventType.CREATE_VESSELTYPE_CANCELLED.toString())
				|| eventType.equals(VesselTypeEventType.UPDATE_VESSELTYPE_CANCELLED.toString())
				|| eventType.equals(VesselTypeEventType.DELETE_VESSELTYPE_CANCELLED.toString()));
	}
}
