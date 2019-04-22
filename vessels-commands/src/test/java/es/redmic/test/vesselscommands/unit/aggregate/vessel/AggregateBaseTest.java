package es.redmic.test.vesselscommands.unit.aggregate.vessel;

/*-
 * #%L
 * Vessels-management
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
