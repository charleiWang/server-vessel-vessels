package es.redmic.vesselscommands.aggregate;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.aggregate.Aggregate;
import es.redmic.vesselscommands.commands.vesseltype.CreateVesselTypeCommand;
import es.redmic.vesselscommands.commands.vesseltype.DeleteVesselTypeCommand;
import es.redmic.vesselscommands.commands.vesseltype.UpdateVesselTypeCommand;
import es.redmic.vesselscommands.statestore.VesselTypeStateStore;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.CheckDeleteVesselTypeEvent;
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

	public CheckDeleteVesselTypeEvent process(DeleteVesselTypeCommand cmd) {

		assert vesselTypeStateStore != null;

		String id = cmd.getVesselTypeId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		CheckDeleteVesselTypeEvent evt = new CheckDeleteVesselTypeEvent();
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);

		return evt;
	}

	public VesselTypeDTO getVesselType() {
		return vesselType;
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
	public void loadFromHistory(Event event) {

		logger.debug("Cargando último estado del vessel type ", event.getAggregateId());

		check(event);

		String eventType = event.getType();

		switch (eventType) {
		case "CREATED":
			logger.debug("Item creado");
			apply((VesselTypeEvent) event);
			break;
		case "UPDATED":
			logger.debug("Item modificado");
			apply((VesselTypeEvent) event);
			break;
		case "DELETED":
			logger.debug("Item borrado");
			apply((VesselTypeDeletedEvent) event);
			break;
		// CANCELLED
		case "CREATE_CANCELLED":
			logger.debug("Compensación por creación fallida");
			apply((CreateVesselTypeCancelledEvent) event);
			break;
		case "UPDATE_CANCELLED":
		case "DELETE_CANCELLED":
			logger.debug("Compensación por edición/borrado fallido");
			apply((VesselTypeEvent) event);
			break;
		default:
			logger.debug("Evento no manejado ", event.getType());
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
