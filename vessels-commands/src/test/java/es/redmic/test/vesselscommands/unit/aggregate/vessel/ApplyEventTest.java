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
	public void applyCreateVesselEvent_ChangeAggrefateState_IfProcessIsOk() {

		CreateVesselEvent evt = getCreateVesselEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyVesselCreatedEvent_ChangeAggrefateState_IfProcessIsOk() {

		VesselCreatedEvent evt = getVesselCreatedEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyUpdateVesselEvent_ChangeAggregateState_IfProcessIsOk() {

		UpdateVesselEvent evt = getUpdateVesselEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyVesselUpdatedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselUpdatedEvent evt = getVesselUpdatedEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyDeleteVesselEvent_ChangeAggregateState_IfProcessIsOk() {

		DeleteVesselEvent evt = getDeleteVesselEvent();

		agg.apply(evt);

		checkDeleteState(evt);
	}

	@Test
	public void applyVesselDeletedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselDeletedEvent evt = getVesselDeletedEvent();

		agg.apply(evt);

		checkDeletedState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreate_IfEventIsCreate() {

		CreateVesselEvent evt = getCreateVesselEvent();

		agg.loadFromHistory(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdate_IfEventIsUpdate() {

		UpdateVesselEvent evt = getUpdateVesselEvent();

		agg.loadFromHistory(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDelete_IfEventIsDelete() {

		DeleteVesselEvent evt = getDeleteVesselEvent();

		agg.loadFromHistory(evt);

		checkDeleteState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDelete_IfLastEventIsDelete() {

		List<Event> history = new ArrayList<>();

		history.add(getCreateVesselEvent());
		history.add(getUpdateVesselEvent());
		history.add(getDeleteVesselEvent());

		history.add(getDeleteVesselEvent());

		agg.loadFromHistory(history);

		checkDeleteState((DeleteVesselEvent) history.get(3));
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleted_IfEventIsDelete() {

		List<Event> history = new ArrayList<>();

		history.add(getCreateVesselEvent());
		history.add(getUpdateVesselEvent());
		history.add(getDeleteVesselEvent());

		history.add(getVesselDeletedEvent());

		agg.loadFromHistory(history);

		checkDeletedState((VesselDeletedEvent) history.get(3));
	}

	private void checkCreateOrUpdateState(VesselEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getVessel(), evt.getVessel());
		assertFalse(agg.isDeleted());
	}

	private void checkDeleteState(DeleteVesselEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertFalse(agg.isDeleted());
	}

	private void checkDeletedState(VesselDeletedEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
