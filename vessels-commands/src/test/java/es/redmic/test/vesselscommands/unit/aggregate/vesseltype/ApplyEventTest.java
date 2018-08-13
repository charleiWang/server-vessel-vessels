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
	public void applyCreateVesselTypeEvent_ChangeAggrefateState_IfProcessIsOk() {

		CreateVesselTypeEvent evt = getCreateVesselTypeEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyVesselTypeCreatedEvent_ChangeAggrefateState_IfProcessIsOk() {

		VesselTypeCreatedEvent evt = getVesselTypeCreatedEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyUpdateVesselTypeEvent_ChangeAggregateState_IfProcessIsOk() {

		UpdateVesselTypeEvent evt = getUpdateVesselTypeEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyVesselTypeUpdatedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselTypeUpdatedEvent evt = getVesselTypeUpdatedEvent();

		agg.apply(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void applyDeleteVesselTypeEvent_ChangeAggregateState_IfProcessIsOk() {

		DeleteVesselTypeEvent evt = getDeleteVesselTypeEvent();

		agg.apply(evt);

		checkDeleteState(evt);
	}

	@Test
	public void applyVesselTypeDeletedEvent_ChangeAggregateState_IfProcessIsOk() {

		VesselTypeDeletedEvent evt = getVesselTypeDeletedEvent();

		agg.apply(evt);

		checkDeletedState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToCreate_IfEventIsCreate() {

		CreateVesselTypeEvent evt = getCreateVesselTypeEvent();

		agg.loadFromHistory(evt);

		checkCreateOrUpdateState(evt);
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToUpdate_IfEventIsUpdate() {

		UpdateVesselTypeEvent evt = getUpdateVesselTypeEvent();

		agg.loadFromHistory(evt);

		checkCreateOrUpdateState(evt);
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

		history.add(getDeleteVesselTypeEvent());

		agg.loadFromHistory(history);

		checkDeleteState((DeleteVesselTypeEvent) history.get(3));
	}

	@Test
	public void loadFromHistory_ChangeAggregateStateToDeleted_IfLastEventIsDelete() {

		List<Event> history = new ArrayList<>();

		history.add(getCreateVesselTypeEvent());
		history.add(getUpdateVesselTypeEvent());

		history.add(getVesselTypeDeletedEvent());

		agg.loadFromHistory(history);

		checkDeletedState((VesselTypeDeletedEvent) history.get(2));
	}

	private void checkCreateOrUpdateState(VesselTypeEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertEquals(agg.getVesselType(), evt.getVesselType());
		assertFalse(agg.isDeleted());
	}

	private void checkDeleteState(DeleteVesselTypeEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertFalse(agg.isDeleted());
	}

	private void checkDeletedState(VesselTypeDeletedEvent evt) {

		assertEquals(agg.getVersion(), evt.getVersion());
		assertEquals(agg.getAggregateId(), evt.getAggregateId());
		assertTrue(agg.isDeleted());
	}
}
