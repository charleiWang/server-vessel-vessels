package es.redmic.test.vesselscommands.unit.aggregate.vessel;

/*-
 * #%L
 * Vessels-management
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
import es.redmic.vesselscommands.commands.vessel.CreateVesselCommand;
import es.redmic.vesselscommands.commands.vessel.DeleteVesselCommand;
import es.redmic.vesselscommands.commands.vessel.UpdateVesselCommand;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vessel.delete.CheckDeleteVesselEvent;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEventTest extends AggregateBaseTest {

	@Test
	public void processCreateVesselCommand_ReturnVesselCreatedEvent_IfProcessIsOk() {

		when(vesselsStateStore.getVessel(any())).thenReturn(null);

		VesselDTO vessel = getVessel();

		CreateVesselCommand command = new CreateVesselCommand(vessel);

		VesselEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getVessel());
		assertEquals(evt.getVessel(), vessel);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), vessel.getId());
		assertEquals(evt.getType(), VesselEventTypes.ENRICH_CREATE);
		assertTrue(evt.getVersion().equals(1));
	}

	@Test
	public void processUpdateVesselCommand_ReturnVesselUpdatedEvent_IfProcessIsOk() {

		when(vesselsStateStore.getVessel(any())).thenReturn(getVesselCreatedEvent());

		VesselDTO vessel = getVessel();

		UpdateVesselCommand command = new UpdateVesselCommand(vessel);

		VesselEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getVessel());
		assertEquals(evt.getVessel(), vessel);
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), vessel.getId());
		assertEquals(evt.getType(), VesselEventTypes.ENRICH_UPDATE);
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

		CheckDeleteVesselEvent evt = agg.process(command);

		assertNotNull(evt);
		assertNotNull(evt.getDate());
		assertNotNull(evt.getId());
		assertEquals(evt.getAggregateId(), vessel.getId());
		assertEquals(evt.getType(), VesselEventTypes.CHECK_DELETE);
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
