package es.redmic.test.vesselscommands.unit.aggregate.vesseltracking;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.exceptions.ItemLockedException;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;

@RunWith(MockitoJUnitRunner.class)
public class ApplyEventTest extends AggregateBaseTest {

	@Test
	public void applyVesselTrackingCreatedEvent_ChangeAggrefateState_IfProcessIsOk() {

		VesselTrackingCreatedEvent evt = getVesselTrackingCreatedEvent();

		agg.apply(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test
	public void applyVesselTrackingUpdatedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselTrackingUpdatedEvent evt = getVesselTrackingUpdatedEvent();

		agg.apply(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test
	public void applyVesselTrackingDeletedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselTrackingDeletedEvent evt = getVesselTrackingDeletedEvent();

		agg.apply(evt);

		checkDeletedState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreated_IfEventIsCreated() {

		VesselTrackingCreatedEvent evt = getVesselTrackingCreatedEvent();

		agg.loadFromHistory(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsCreate() {

		CreateVesselTrackingEvent evt = getCreateVesselTrackingEvent();

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdated_IfEventIsUpdated() {

		VesselTrackingUpdatedEvent evt = getVesselTrackingUpdatedEvent();

		agg.loadFromHistory(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsUpdate() {

		UpdateVesselTrackingEvent evt = getUpdateVesselTrackingEvent();

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleted_IfEventIsDeleted() {

		List<Event> history = new ArrayList<>();

		history.add(getVesselTrackingCreatedEvent());
		history.add(getVesselTrackingUpdatedEvent());
		history.add(getVesselTrackingDeletedEvent());

		history.add(getVesselTrackingDeletedEvent());

		agg.loadFromHistory(history);

		checkDeletedState((VesselTrackingDeletedEvent) history.get(3));
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsDelete() {

		DeleteVesselTrackingEvent evt = getDeleteVesselTrackingEvent();

		agg.loadFromHistory(evt);
	}

	private void checkCreatedOrUpdatedState(VesselTrackingEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getVesselTracking(), evt.getVesselTracking());
		assertFalse(agg.isDeleted());
	}

	private void checkDeletedState(VesselTrackingDeletedEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
