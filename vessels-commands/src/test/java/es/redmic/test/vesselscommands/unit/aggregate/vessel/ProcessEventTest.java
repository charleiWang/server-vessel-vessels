package es.redmic.test.vesselscommands.unit.aggregate.vessel;

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
import es.redmic.vesselscommands.commands.CreateVesselCommand;
import es.redmic.vesselscommands.commands.DeleteVesselCommand;
import es.redmic.vesselscommands.commands.UpdateVesselCommand;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventType;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEventTest extends AggregateBaseTest {

	@Test
	public void processCreateVesselCommand_ReturnVesselCreatedEvent_IfProcessIsOk() {

		when(vesselsStateStore.getVessel(any())).thenReturn(null);

		VesselDTO vessel = getVessel();

		CreateVesselCommand command = new CreateVesselCommand(vessel);

		CreateVesselEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getVessel());
		assertEquals(evt.getVessel(), vessel);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), vessel.getId());
		assertEquals(evt.getType(), VesselEventType.CREATE_VESSEL.name());
		assertTrue(evt.getVersion().equals(1));
	}

	@Test
	public void processUpdateVesselCommand_ReturnVesselUpdatedEvent_IfProcessIsOk() {

		when(vesselsStateStore.getVessel(any())).thenReturn(getVesselCreatedEvent());

		VesselDTO vessel = getVessel();

		UpdateVesselCommand command = new UpdateVesselCommand(vessel);

		UpdateVesselEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getVessel());
		assertEquals(evt.getVessel(), vessel);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), vessel.getId());
		assertEquals(evt.getType(), VesselEventType.UPDATE_VESSEL.name());
		assertTrue(evt.getVersion().equals(2));
	}

	// Editar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processUpdateVesselCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		when(vesselsStateStore.getVessel(any())).thenReturn(getVesselDeletedEvent());

		VesselDTO vessel = getVessel();

		agg.process(new UpdateVesselCommand(vessel));
	}

	// Editar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processUpdateVesselCommand_ThrowItemLockedException_IfItemIsLocked() {

		when(vesselsStateStore.getVessel(any())).thenReturn(getUpdateVesselEvent());

		VesselDTO vessel = getVessel();

		agg.process(new UpdateVesselCommand(vessel));
	}

	@Test
	public void processDeleteVesselCommand_ReturnVesselDeletedEvent_IfProcessIsOk() {

		when(vesselsStateStore.getVessel(any())).thenReturn(getVesselUpdatedEvent());

		VesselDTO vessel = getVessel();

		DeleteVesselCommand command = new DeleteVesselCommand(vessel.getId());

		DeleteVesselEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), vessel.getId());
		assertEquals(evt.getType(), VesselEventType.DELETE_VESSEL.name());
		assertTrue(evt.getVersion().equals(3));
	}

	// Borrar un elemento ya borrado
	@Test(expected = ItemNotFoundException.class)
	public void processDeleteVesselCommand_ThrowItemNotFoundException_IfItemIsDeleted() {

		when(vesselsStateStore.getVessel(any())).thenReturn(getVesselDeletedEvent());

		VesselDTO vessel = getVessel();

		agg.process(new DeleteVesselCommand(vessel.getId()));
	}

	// Borrar un elemento bloqueado
	@Test(expected = ItemLockedException.class)
	public void processDeleteVesselCommand_ThrowItemLockedException_IfItemIsLocked() {

		when(vesselsStateStore.getVessel(any())).thenReturn(getUpdateVesselEvent());

		VesselDTO vessel = getVessel();

		agg.process(new DeleteVesselCommand(vessel.getId()));
	}
}
