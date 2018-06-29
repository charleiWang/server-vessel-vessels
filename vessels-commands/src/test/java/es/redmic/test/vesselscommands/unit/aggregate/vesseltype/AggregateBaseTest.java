package es.redmic.test.vesselscommands.unit.aggregate.vesseltype;

import org.junit.Before;
import org.mockito.Mockito;

import es.redmic.test.vesselscommands.integration.vesseltype.VesselTypeDataUtil;
import es.redmic.vesselscommands.aggregate.VesselTypeAggregate;
import es.redmic.vesselscommands.statestore.VesselTypeStateStore;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

public abstract class AggregateBaseTest {

	private final String code = "70";

	VesselTypeStateStore vesselsTypeStateStore;

	VesselTypeAggregate agg;

	@Before
	public void setUp() {

		vesselsTypeStateStore = Mockito.mock(VesselTypeStateStore.class);

		agg = new VesselTypeAggregate(vesselsTypeStateStore);
	}

	protected VesselTypeDTO getVesselType() {

		return VesselTypeDataUtil.getVesselType(code);
	}

	protected VesselTypeCreatedEvent getVesselTypeCreatedEvent() {

		return VesselTypeDataUtil.getVesselTypeCreatedEvent(code);
	}

	protected CreateVesselTypeEvent getCreateVesselTypeEvent() {

		return VesselTypeDataUtil.getCreateEvent(code);
	}

	protected VesselTypeUpdatedEvent getVesselTypeUpdatedEvent() {

		return VesselTypeDataUtil.getVesselTypeUpdatedEvent(code);
	}

	protected UpdateVesselTypeEvent getUpdateVesselTypeEvent() {

		return VesselTypeDataUtil.getUpdateEvent(code);
	}

	protected DeleteVesselTypeEvent getDeleteVesselTypeEvent() {

		return VesselTypeDataUtil.getDeleteEvent(code);
	}

	protected VesselTypeDeletedEvent getVesselTypeDeletedEvent() {

		return VesselTypeDataUtil.getVesselTypeDeletedEvent(code);
	}
}
