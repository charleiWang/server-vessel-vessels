package es.redmic.test.vesselscommands.unit.aggregate.vesseltracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.vesselscommands.commands.vesseltracking.CreateVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.DeleteVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.UpdateVesselTrackingCommand;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEventTest extends AggregateBaseTest {

	@Test // Simula un add desde ais con los datos del vessel completos
	public void processCreateVesselTrackingCommand_ReturnVesselTrackingCreateEvent_IfVesselIsComplete() {

		when(vesselTrackingsStateStore.getVesselTracking(any())).thenReturn(null);

		VesselTrackingDTO vesselTracking = getVesselTracking();

		CreateVesselTrackingCommand command = new CreateVesselTrackingCommand(vesselTracking);

		VesselTrackingEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getVesselTracking());
		assertEquals(evt.getVesselTracking(), vesselTracking);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), vesselTracking.getId());
		assertEquals(VesselTrackingEventTypes.CREATE, evt.getType());
		assertTrue(evt.getVersion().equals(1));
	}

	@Test // Simula un add desde http solo con el id del vessel
	public void processCreateVesselTrackingCommand_ReturnCreateVesselTrackingEnrichEvent_IfVesselIsNotComplete() {

		when(vesselTrackingsStateStore.getVesselTracking(any())).thenReturn(null);

		VesselTrackingDTO vesselTracking = getVesselTracking();

		VesselDTO emptyVessel = new VesselDTO();
		emptyVessel.setId(vesselTracking.getProperties().getVessel().getId());
		vesselTracking.getProperties().setVessel(emptyVessel);

		CreateVesselTrackingCommand command = new CreateVesselTrackingCommand(vesselTracking);

		VesselTrackingEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getVesselTracking());
		assertEquals(evt.getVesselTracking(), vesselTracking);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), vesselTracking.getId());
		assertEquals(VesselTrackingEventTypes.ENRICH_CREATE, evt.getType());
		assertTrue(evt.getVersion().equals(1));
	}

	@Test
	public void processUpdateVesselTrackingCommand_ReturnVesselTrackingUpdatedEvent_IfProcessIsOk() {

		VesselTrackingCreatedEvent vesselTrackingCreatedEvent = getVesselTrackingCreatedEvent();

		when(vesselTrackingsStateStore.getVesselTracking(any())).thenReturn(vesselTrackingCreatedEvent);

		VesselTrackingDTO vesselTracking = vesselTrackingCreatedEvent.getVesselTracking();

		UpdateVesselTrackingCommand command = new UpdateVesselTrackingCommand(vesselTracking);

		VesselTrackingEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getVesselTracking());
		assertEquals(evt.getVesselTracking(), vesselTracking);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), vesselTracking.getId());
		assertEquals(evt.getType(), VesselTrackingEventTypes.ENRICH_UPDATE);
		assertTrue(evt.getVersion().equals(2));
	}

	// Editar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processUpdateVesselTrackingCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		VesselTrackingDeletedEvent vesselTrackingDeletedEvent = getVesselTrackingDeletedEvent();

		when(vesselTrackingsStateStore.getVesselTracking(any())).thenReturn(vesselTrackingDeletedEvent);

		VesselTrackingDTO vesselTracking = getVesselTracking();
		vesselTracking.setId(vesselTrackingDeletedEvent.getAggregateId());

		agg.process(new UpdateVesselTrackingCommand(vesselTracking));
	}

	// Editar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processUpdateVesselTrackingCommand_ThrowItemLockedException_IfItemIsLocked() {

		UpdateVesselTrackingEvent updateVesselTrackingEvent = getUpdateVesselTrackingEvent();

		when(vesselTrackingsStateStore.getVesselTracking(any())).thenReturn(updateVesselTrackingEvent);

		VesselTrackingDTO vesselTracking = getVesselTracking();
		vesselTracking.setId(updateVesselTrackingEvent.getAggregateId());

		agg.process(new UpdateVesselTrackingCommand(vesselTracking));
	}

	@Test
	public void processDeleteVesselTrackingCommand_ReturnVesselTrackingDeletedEvent_IfProcessIsOk() {

		VesselTrackingUpdatedEvent vesselTrackingUpdatedEvent = getVesselTrackingUpdatedEvent();
		when(vesselTrackingsStateStore.getVesselTracking(any())).thenReturn(vesselTrackingUpdatedEvent);

		VesselTrackingDTO vesselTracking = getVesselTracking();
		vesselTracking.setId(vesselTrackingUpdatedEvent.getAggregateId());

		DeleteVesselTrackingCommand command = new DeleteVesselTrackingCommand(vesselTracking.getId());

		DeleteVesselTrackingEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), vesselTracking.getId());
		assertEquals(evt.getType(), VesselTrackingEventTypes.DELETE);
		assertTrue(evt.getVersion().equals(3));
	}

	// Borrar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processDeleteVesselTrackingCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		VesselTrackingDeletedEvent vesselTrackingDeletedEvent = getVesselTrackingDeletedEvent();

		when(vesselTrackingsStateStore.getVesselTracking(any())).thenReturn(vesselTrackingDeletedEvent);

		VesselTrackingDTO vesselTracking = getVesselTracking();
		vesselTracking.setId(vesselTrackingDeletedEvent.getAggregateId());

		agg.process(new DeleteVesselTrackingCommand(vesselTracking.getId()));
	}

	// Borrar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processDeleteVesselTrackingCommand_ThrowItemLockedException_IfItemIsLocked() {

		UpdateVesselTrackingEvent updateVesselTrackingEvent = getUpdateVesselTrackingEvent();
		when(vesselTrackingsStateStore.getVesselTracking(any())).thenReturn(updateVesselTrackingEvent);

		VesselTrackingDTO vesselTracking = getVesselTracking();
		vesselTracking.setId(updateVesselTrackingEvent.getAggregateId());

		agg.process(new DeleteVesselTrackingCommand(vesselTracking.getId()));
	}
}
