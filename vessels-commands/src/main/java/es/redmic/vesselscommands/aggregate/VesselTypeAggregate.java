package es.redmic.vesselscommands.aggregate;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.aggregate.Aggregate;
import es.redmic.exception.database.DBNotFoundException;
import es.redmic.vesselscommands.commands.vesseltype.CreateVesselTypeCommand;
import es.redmic.vesselscommands.commands.vesseltype.DeleteVesselTypeCommand;
import es.redmic.vesselscommands.commands.vesseltype.UpdateVesselTypeCommand;
import es.redmic.vesselscommands.statestore.VesselTypeStateStore;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;

public class VesselTypeAggregate extends Aggregate {

	private VesselTypeDTO vesselType;

	private VesselTypeStateStore vesselTypeStateStore;

	public VesselTypeAggregate(VesselTypeStateStore vesselTypeStateStore) {

		this.vesselTypeStateStore = vesselTypeStateStore;
	}

	public CreateVesselTypeEvent process(CreateVesselTypeCommand cmd) {

		assert vesselTypeStateStore != null;

		String id = cmd.getVesselType().getId();

		if (exist(id)) {
			logger.info("Descartando vessel type " + id + ". Ya está registrado.");
			return null; // Se lanza excepción en el origen no aquí
		}

		this.setAggregateId(id);

		CreateVesselTypeEvent evt = new CreateVesselTypeEvent(cmd.getVesselType());
		evt.setAggregateId(id);
		evt.setVersion(1);
		return evt;
	}

	public UpdateVesselTypeEvent process(UpdateVesselTypeCommand cmd) {

		assert vesselTypeStateStore != null;

		String id = cmd.getVesselType().getId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		UpdateVesselTypeEvent evt = new UpdateVesselTypeEvent(cmd.getVesselType());
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public DeleteVesselTypeEvent process(DeleteVesselTypeCommand cmd) {

		assert vesselTypeStateStore != null;

		String id = cmd.getVesselTypeId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		DeleteVesselTypeEvent evt = new DeleteVesselTypeEvent();
		evt.setAggregateId(id);
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
	protected boolean isLocked(String eventType) {

		return VesselTypeEventTypes.isLocked(eventType);
	}

	@Override
	protected Event getItemFromStateStore(String id) {

		return vesselTypeStateStore.getVesselType(id);
	}

	@Override
	public void loadFromHistory(Event history) {

		logger.debug("Cargando último estado del vessel type ", history.getAggregateId());

		String eventType = history.getType();

		switch (eventType) {
		case "CREATE":
			logger.debug("En fase de creación");
			apply((VesselTypeEvent) history);
			break;
		case "CREATED":
			logger.debug("Item creado");
			apply((VesselTypeEvent) history);
			break;
		case "UPDATE":
			logger.debug("En fase de modificación");
			apply((VesselTypeEvent) history);
			break;
		case "UPDATED":
			logger.debug("Item modificado");
			apply((VesselTypeEvent) history);
			break;
		case "DELETED":
			logger.debug("Item borrado");
			apply((VesselTypeDeletedEvent) history);
			break;
		// CANCELLED
		case "CREATE_CANCELLED":
			logger.debug("Compensación por creación fallida");
			apply((CreateVesselTypeCancelledEvent) history);
			break;
		case "UPDATE_CANCELLED":
		case "DELETE_CANCELLED":
			logger.debug("Compensación por edición/borrado fallido");
			apply((VesselTypeEvent) history);
			break;
		default:
			super._loadFromHistory(history);
		}
	}

	public void apply(CreateVesselTypeCancelledEvent event) {
		this.deleted = true;
		apply(event);
	}

	public void apply(VesselTypeDeletedEvent event) {
		this.deleted = true;
		super.apply(event);
	}

	public void apply(VesselTypeEvent event) {
		this.vesselType = event.getVesselType();
		super.apply(event);
	}

	@Override
	protected void reset() {
		this.vesselType = null;
		super.reset();
	}
}
