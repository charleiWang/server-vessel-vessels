package es.redmic.test.vesselscommands.unit.aggregate.vesseltracking;

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

	String tstamp = String.valueOf(new DateTime().getMillis());

	@Before
	public void setUp() {

		vesselTrackingsStateStore = Mockito.mock(VesselTrackingStateStore.class);

		agg = new VesselTrackingAggregate(vesselTrackingsStateStore);
	}

	protected CreateVesselTrackingEvent getCreateVesselTrackingEvent() {

		return VesselTrackingDataUtil.getCreateEvent(mmsi, tstamp);
	}

	protected VesselTrackingCreatedEvent getVesselTrackingCreatedEvent() {

		return VesselTrackingDataUtil.getVesselTrackingCreatedEvent(mmsi, tstamp);
	}

	protected UpdateVesselTrackingEvent getUpdateVesselTrackingEvent() {

		return VesselTrackingDataUtil.getUpdateEvent(mmsi, tstamp);
	}

	protected VesselTrackingUpdatedEvent getVesselTrackingUpdatedEvent() {

		return VesselTrackingDataUtil.getVesselTrackingUpdatedEvent(mmsi, tstamp);
	}

	protected DeleteVesselTrackingEvent getDeleteVesselTrackingEvent() {

		return VesselTrackingDataUtil.getDeleteEvent(mmsi, tstamp);
	}

	protected VesselTrackingDeletedEvent getVesselTrackingDeletedEvent() {

		return VesselTrackingDataUtil.getVesselTrackingDeletedEvent(mmsi, tstamp);
	}

	protected VesselTrackingDTO getVesselTracking() {

		return VesselTrackingDataUtil.getVesselTracking(mmsi, String.valueOf(new DateTime().getMillis()));
	}
}
