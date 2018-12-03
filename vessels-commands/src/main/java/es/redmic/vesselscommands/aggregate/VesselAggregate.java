package es.redmic.vesselscommands.aggregate;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.aggregate.Aggregate;
import es.redmic.vesselscommands.commands.vessel.CreateVesselCommand;
import es.redmic.vesselscommands.commands.vessel.DeleteVesselCommand;
import es.redmic.vesselscommands.commands.vessel.UpdateVesselCommand;
import es.redmic.vesselscommands.statestore.VesselStateStore;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.EnrichCreateVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.CheckDeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselEvent;
import es.redmic.vesselslib.events.vessel.update.EnrichUpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;

public class VesselAggregate extends Aggregate {

	private VesselDTO vessel;

	private VesselStateStore vesselStateStore;

	public VesselAggregate(VesselStateStore vesselStateStore) {
		this.vesselStateStore = vesselStateStore;
	}

	public VesselEvent process(CreateVesselCommand cmd) {

		assert vesselStateStore != null;

		String id = cmd.getVessel().getId();

		if (exist(id)) {
			logger.info("Descartando inserción de " + id + ". El item ya está registrado.");
			return null; // Se lanza excepción en el origen no aquí
		}
		this.setAggregateId(id);

		VesselEvent evt = null;

		if (cmd.getVessel().getType() != null) {

			logger.debug("Creando evento para enriquecer Vessel");
			evt = new EnrichCreateVesselEvent(cmd.getVessel());
		} else {
			logger.debug("Creando evento para crear Vessel. Enriquecimiento descartado");
			evt = new CreateVesselEvent(cmd.getVessel());
		}

		evt.setAggregateId(id);
		evt.setVersion(1);
		return evt;
	}

	public VesselEvent process(UpdateVesselCommand cmd) {

		assert vesselStateStore != null;

		String id = cmd.getVessel().getId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		VesselEvent evt = null;

		if (cmd.getVessel().getType() != null) {

			logger.debug("Creando evento para enriquecer Vessel");
			evt = new EnrichUpdateVesselEvent(cmd.getVessel());
		} else {
			logger.debug("Creando evento para modificar Vessel. Enriquecimiento descartado");
			evt = new UpdateVesselEvent(cmd.getVessel());
		}

		evt.setAggregateId(id);
		evt.setVersion(2);
		return evt;
	}

	public CheckDeleteVesselEvent process(DeleteVesselCommand cmd) {

		assert vesselStateStore != null;

		String id = cmd.getVesselId();

		Event state = getStateFromHistory(id);

		loadFromHistory(state);

		checkState(id, state.getType());

		CheckDeleteVesselEvent evt = new CheckDeleteVesselEvent();
		evt.setAggregateId(id);
		evt.setVersion(getVersion() + 1);
		return evt;
	}

	public VesselDTO getVessel() {
		return vessel;
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
	public void loadFromHistory(Event event) {

		logger.debug("Cargando último estado del vessel ", event.getAggregateId());

		check(event);

		String eventType = event.getType();

		switch (eventType) {
		case "CREATED":
			logger.debug("Item creado");
			apply((VesselEvent) event);
			break;
		case "UPDATED":
			logger.debug("Item modificado");
			apply((VesselEvent) event);
			break;
		case "DELETED":
			logger.debug("Item borrado");
			apply((VesselDeletedEvent) event);
			break;
		// CANCELLED
		case "CREATE_CANCELLED":
			logger.debug("Compensación por creación fallida");
			apply((CreateVesselCancelledEvent) event);
			break;
		case "UPDATE_CANCELLED":
		case "DELETE_CANCELLED":
			logger.debug("Compensación por edición/borrado fallido");
			apply((VesselEvent) event);
			break;
		default:
			logger.debug("Evento no manejado ", event.getType());
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

	public void apply(UpdateVesselTypeInVesselEvent event) {
		if (this.vessel == null)
			this.vessel = new VesselDTO();
		this.vessel.setType(event.getVesselType());
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
