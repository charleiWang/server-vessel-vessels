package es.redmic.test.vesselscommands.unit.aggregate.vessel;

import org.junit.Before;
import org.mockito.Mockito;

import es.redmic.test.vesselscommands.integration.vessel.VesselDataUtil;
import es.redmic.vesselscommands.aggregate.VesselAggregate;
import es.redmic.vesselscommands.statestore.VesselStateStore;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;

public abstract class AggregateBaseTest {

	VesselStateStore vesselsStateStore;

	VesselAggregate agg;

	Integer mmsi = 1234;

	@Before
	public void setUp() {

		vesselsStateStore = Mockito.mock(VesselStateStore.class);

		agg = new VesselAggregate(vesselsStateStore);
	}

	protected CreateVesselEvent getCreateVesselEvent() {

		return VesselDataUtil.getCreateEvent(mmsi);
	}

	protected VesselCreatedEvent getVesselCreatedEvent() {

		return VesselDataUtil.getVesselCreatedEvent(mmsi);
	}

	protected UpdateVesselEvent getUpdateVesselEvent() {

		return VesselDataUtil.getUpdateEvent(mmsi);
	}

	protected VesselUpdatedEvent getVesselUpdatedEvent() {

		return VesselDataUtil.getVesselUpdatedEvent(mmsi);
	}

	protected DeleteVesselEvent getDeleteVesselEvent() {

		return VesselDataUtil.getDeleteEvent(mmsi);
	}

	protected VesselDeletedEvent getVesselDeletedEvent() {

		return VesselDataUtil.getVesselDeletedEvent(mmsi);
	}

	protected VesselDTO getVessel() {

		return VesselDataUtil.getVessel(mmsi);
	}
}
