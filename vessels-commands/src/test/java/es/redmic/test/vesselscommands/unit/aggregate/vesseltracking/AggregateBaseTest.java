package es.redmic.test.vesselscommands.unit.aggregate.vesseltracking;

import org.joda.time.DateTime;
import org.junit.Before;
import org.mockito.Mockito;

import es.redmic.test.vesselscommands.integration.vesseltracking.VesselTrackingDataUtil;
import es.redmic.vesselscommands.aggregate.VesselTrackingAggregate;
import es.redmic.vesselscommands.statestore.VesselTrackingStateStore;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;

public abstract class AggregateBaseTest {

	VesselTrackingStateStore vesselTrackingsStateStore;

	VesselTrackingAggregate agg;

	Integer mmsi = 1234;

	@Before
	public void setUp() {

		vesselTrackingsStateStore = Mockito.mock(VesselTrackingStateStore.class);

		agg = new VesselTrackingAggregate(vesselTrackingsStateStore);
	}

	protected CreateVesselTrackingEvent getCreateVesselTrackingEvent() {

		return VesselTrackingDataUtil.getCreateEvent(mmsi);
	}

	protected VesselTrackingCreatedEvent getVesselTrackingCreatedEvent() {

		return VesselTrackingDataUtil.getVesselTrackingCreatedEvent(mmsi);
	}

	protected UpdateVesselTrackingEvent getUpdateVesselTrackingEvent() {

		return VesselTrackingDataUtil.getUpdateEvent(mmsi);
	}

	protected VesselTrackingUpdatedEvent getVesselTrackingUpdatedEvent() {

		return VesselTrackingDataUtil.getVesselTrackingUpdatedEvent(mmsi);
	}

	protected DeleteVesselTrackingEvent getDeleteVesselTrackingEvent() {

		return VesselTrackingDataUtil.getDeleteEvent(mmsi);
	}

	protected VesselTrackingDeletedEvent getVesselTrackingDeletedEvent() {

		return VesselTrackingDataUtil.getVesselTrackingDeletedEvent(mmsi);
	}

	protected VesselTrackingDTO getVesselTracking() {

		return VesselTrackingDataUtil.getVesselTracking(mmsi, String.valueOf(new DateTime().getMillis()));
	}
}
