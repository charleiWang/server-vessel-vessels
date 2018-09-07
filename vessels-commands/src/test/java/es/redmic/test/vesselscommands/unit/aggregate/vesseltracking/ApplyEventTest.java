package es.redmic.test.vesselscommands.unit.aggregate.vesseltracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import es.redmic.brokerlib.avro.common.Event;
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
	public void applyCreateVesselTrackingEvent_ChangeAggrefateState_IfProcessIsOk() {

		CreateVesselTrackingEvent evt = getCreateVesselTrackingEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyVesselTrackingCreatedEvent_ChangeAggrefateState_IfProcessIsOk() {

		VesselTrackingCreatedEvent evt = getVesselTrackingCreatedEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyUpdateVesselTrackingEvent_ChangeAggregateState_IfProcessIsOk() {

		UpdateVesselTrackingEvent evt = getUpdateVesselTrackingEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyVesselTrackingUpdatedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselTrackingUpdatedEvent evt = getVesselTrackingUpdatedEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyDeleteVesselTrackingEvent_ChangeAggregateState_IfProcessIsOk() {

		DeleteVesselTrackingEvent evt = getDeleteVesselTrackingEvent();

		agg.apply(evt);

		checkDeleteState(evt);
	}

	@Test
	public void applyVesselTrackingDeletedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselTrackingDeletedEvent evt = getVesselTrackingDeletedEvent();

		agg.apply(evt);

		checkDeletedState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreate_IfEventIsCreate() {

		CreateVesselTrackingEvent evt = getCreateVesselTrackingEvent();

		agg.loadFromHistory(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdate_IfEventIsUpdate() {

		UpdateVesselTrackingEvent evt = getUpdateVesselTrackingEvent();

		agg.loadFromHistory(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDelete_IfEventIsDelete() {

		DeleteVesselTrackingEvent evt = getDeleteVesselTrackingEvent();

		agg.loadFromHistory(evt);

		checkDeleteState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDelete_IfLastEventIsDelete() {

		List<Event> history = new ArrayList<>();

		history.add(getCreateVesselTrackingEvent());
		history.add(getUpdateVesselTrackingEvent());
		history.add(getDeleteVesselTrackingEvent());

		history.add(getDeleteVesselTrackingEvent());

		agg.loadFromHistory(history);

		checkDeleteState((DeleteVesselTrackingEvent) history.get(3));
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleted_IfEventIsDelete() {

		List<Event> history = new ArrayList<>();

		history.add(getCreateVesselTrackingEvent());
		history.add(getUpdateVesselTrackingEvent());
		history.add(getDeleteVesselTrackingEvent());

		history.add(getVesselTrackingDeletedEvent());

		agg.loadFromHistory(history);

		checkDeletedState((VesselTrackingDeletedEvent) history.get(3));
	}

	private void checkCreateOrUpdateState(VesselTrackingEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getVesselTracking(), evt.getVesselTracking());
		assertFalse(agg.isDeleted());
	}

	private void checkDeleteState(DeleteVesselTrackingEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertFalse(agg.isDeleted());
	}

	private void checkDeletedState(VesselTrackingDeletedEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
