package es.redmic.test.vesselscommands.unit.aggregate.vessel;

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
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;

@RunWith(MockitoJUnitRunner.class)
public class ApplyEventTest extends AggregateBaseTest {

	@Test
	public void applyVesselCreatedEvent_ChangeAggrefateState_IfProcessIsOk() {

		VesselCreatedEvent evt = getVesselCreatedEvent();

		agg.apply(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test
	public void applyVesselUpdatedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselUpdatedEvent evt = getVesselUpdatedEvent();

		agg.apply(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test
	public void applyVesselDeletedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselDeletedEvent evt = getVesselDeletedEvent();

		agg.apply(evt);

		checkDeletedState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreated_IfEventIsCreated() {

		VesselCreatedEvent evt = getVesselCreatedEvent();

		agg.loadFromHistory(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsCreate() {

		CreateVesselEvent evt = getCreateVesselEvent();

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdated_IfEventIsUpdated() {

		VesselUpdatedEvent evt = getVesselUpdatedEvent();

		agg.loadFromHistory(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsUpdate() {

		UpdateVesselEvent evt = getUpdateVesselEvent();

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleted_IfEventIsDeleted() {

		List<Event> history = new ArrayList<>();

		history.add(getVesselCreatedEvent());
		history.add(getVesselUpdatedEvent());
		history.add(getVesselDeletedEvent());

		history.add(getVesselDeletedEvent());

		agg.loadFromHistory(history);

		checkDeletedState((VesselDeletedEvent) history.get(3));
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsDelete() {

		DeleteVesselEvent evt = getDeleteVesselEvent();

		agg.loadFromHistory(evt);
	}

	private void checkCreatedOrUpdatedState(VesselEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getVessel(), evt.getVessel());
		assertFalse(agg.isDeleted());
	}

	private void checkDeletedState(VesselDeletedEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
