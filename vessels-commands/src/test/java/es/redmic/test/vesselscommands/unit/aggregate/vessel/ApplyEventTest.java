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
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;

@RunWith(MockitoJUnitRunner.class)
public class ApplyEventTest extends AggregateBaseTest {

	@Test
	public void applyVesselCreateEvent_ChangeAggrefateState_IfProcessIsOk() {

		CreateVesselEvent evt = getCreateVesselEvent();

		agg.apply(evt);

		checkCreateState(evt);
	}

	@Test
	public void applyVesselUpdateEvent_ChangeAggregateState_IfProcessIsOk() {

		UpdateVesselEvent evt = getUpdateVesselEvent();

		agg.apply(evt);

		checkUpdateState(evt);
	}

	@Test
	public void applyVesselDeleteEvent_ChangeAggregateState_IfProcessIsOk() {

		DeleteVesselEvent evt = getDeleteVesselEvent();

		agg.apply(evt);

		checkDeleteState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreate_IfEventIsCreate() {

		CreateVesselEvent evt = getCreateVesselEvent();

		agg.loadFromHistory(evt);

		checkCreateState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdate_IfEventIsUpdate() {

		UpdateVesselEvent evt = getUpdateVesselEvent();

		agg.loadFromHistory(evt);

		checkUpdateState(evt);
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

		DeleteVesselEvent evt = getDeleteVesselEvent();

		agg.loadFromHistory(evt);

		checkDeleteState(evt);
	}

	private void checkCreateState(CreateVesselEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getVessel(), evt.getVessel());
		assertFalse(agg.isDeleted());
	}

	private void checkUpdateState(UpdateVesselEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getVessel(), evt.getVessel());
		assertFalse(agg.isDeleted());
	}

	private void checkDeleteState(DeleteVesselEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
