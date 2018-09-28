package es.redmic.test.vesselscommands.unit.aggregate.vesseltype;

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
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

@RunWith(MockitoJUnitRunner.class)
public class ApplyEventTest extends AggregateBaseTest {

	@Test
	public void applyVesselTypeCreatedEvent_ChangeAggrefateState_IfProcessIsOk() {

		VesselTypeCreatedEvent evt = getVesselTypeCreatedEvent();

		agg.apply(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test
	public void applyVesselTypeUpdatedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselTypeUpdatedEvent evt = getVesselTypeUpdatedEvent();

		agg.apply(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test
	public void applyVesselTypeDeletedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselTypeDeletedEvent evt = getVesselTypeDeletedEvent();

		agg.apply(evt);

		checkDeletedState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreated_IfEventIsCreated() {

		VesselTypeCreatedEvent evt = getVesselTypeCreatedEvent();

		agg.loadFromHistory(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsCreate() {

		CreateVesselTypeEvent evt = getCreateVesselTypeEvent();

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdated_IfEventIsUpdated() {

		VesselTypeUpdatedEvent evt = getVesselTypeUpdatedEvent();

		agg.loadFromHistory(evt);

		checkCreatedOrUpdatedState(evt);
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsUpdate() {

		UpdateVesselTypeEvent evt = getUpdateVesselTypeEvent();

		agg.loadFromHistory(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleted_IfLastEventIsDeleted() {

		List<Event> history = new ArrayList<>();

		history.add(getVesselTypeCreatedEvent());
		history.add(getVesselTypeUpdatedEvent());

		history.add(getVesselTypeDeletedEvent());

		agg.loadFromHistory(history);

		checkDeletedState((VesselTypeDeletedEvent) history.get(2));
	}

	@Test(expected = ItemLockedException.class)
	public void loadFromHistory_ThrowItemLockedException_IfEventIsDelete() {

		DeleteVesselTypeEvent evt = getDeleteVesselTypeEvent();

		agg.loadFromHistory(evt);
	}

	private void checkCreatedOrUpdatedState(VesselTypeEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getVesselType(), evt.getVesselType());
		assertFalse(agg.isDeleted());
	}

	private void checkDeletedState(VesselTypeDeletedEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
