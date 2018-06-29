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
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;

@RunWith(MockitoJUnitRunner.class)
public class ApplyEventTest extends AggregateBaseTest {

	@Test
	public void applyVesselTypeCreateEvent_ChangeAggrefateState_IfProcessIsOk() {

		CreateVesselTypeEvent evt = getCreateVesselTypeEvent();

		agg.apply(evt);

		checkCreateState(evt);
	}

	@Test
	public void applyVesselTypeUpdateEvent_ChangeAggregateState_IfProcessIsOk() {

		UpdateVesselTypeEvent evt = getUpdateVesselTypeEvent();

		agg.apply(evt);

		checkUpdateState(evt);
	}

	@Test
	public void applyVesselTypeDeleteEvent_ChangeAggregateState_IfProcessIsOk() {

		DeleteVesselTypeEvent evt = getDeleteVesselTypeEvent();

		agg.apply(evt);

		checkDeleteState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreate_IfEventIsCreate() {

		CreateVesselTypeEvent evt = getCreateVesselTypeEvent();

		agg.loadFromHistory(evt);

		checkCreateState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdate_IfEventIsUpdate() {

		UpdateVesselTypeEvent evt = getUpdateVesselTypeEvent();

		agg.loadFromHistory(evt);

		checkUpdateState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDelete_IfEventIsDelete() {

		DeleteVesselTypeEvent evt = getDeleteVesselTypeEvent();

		agg.loadFromHistory(evt);

		checkDeleteState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDelete_IfLastEventIsDelete() {

		List<Event> history = new ArrayList<>();

		history.add(getCreateVesselTypeEvent());
		history.add(getUpdateVesselTypeEvent());
		history.add(getDeleteVesselTypeEvent());

		DeleteVesselTypeEvent evt = getDeleteVesselTypeEvent();

		agg.loadFromHistory(evt);

		checkDeleteState(evt);
	}

	private void checkCreateState(CreateVesselTypeEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getVesselType(), evt.getVesselType());
		assertFalse(agg.isDeleted());
	}

	private void checkUpdateState(UpdateVesselTypeEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getVesselType(), evt.getVesselType());
		assertFalse(agg.isDeleted());
	}

	private void checkDeleteState(DeleteVesselTypeEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
