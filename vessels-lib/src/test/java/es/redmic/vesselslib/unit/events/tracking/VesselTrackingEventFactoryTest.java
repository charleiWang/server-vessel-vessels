package es.redmic.vesselslib.unit.events.tracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventFactory;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCheckFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCheckedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel.UpdateVesselInVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;
import es.redmic.vesselslib.unit.utils.VesselDataUtil;
import es.redmic.vesselslib.unit.utils.VesselTrackingDataUtil;

public class VesselTrackingEventFactoryTest {

	@Test
	public void GetEvent_ReturnDeleteVesselTrackingEvent_IfTypeIsDelete() {

		Event source = VesselTrackingDataUtil.getDeleteVesselTrackingCheckedEvent();
		DeleteVesselTrackingEvent event = (DeleteVesselTrackingEvent) VesselTrackingEventFactory.getEvent(source,
				VesselTrackingEventTypes.DELETE);

		assertEquals(VesselTrackingEventTypes.DELETE, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTrackingCheckedEvent_IfTypeIsDelete_Checked() {

		Event source = VesselTrackingDataUtil.getCheckDeleteVesselTrackingEvent();
		DeleteVesselTrackingCheckedEvent event = (DeleteVesselTrackingCheckedEvent) VesselTrackingEventFactory
				.getEvent(source, VesselTrackingEventTypes.DELETE_CHECKED);

		assertEquals(VesselTrackingEventTypes.DELETE_CHECKED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnCreateVesselTrackingConfirmedEvent_IfTypeIsCreateConfirmed() {

		Event source = VesselTrackingDataUtil.getCreateConfirmedEvent();
		CreateVesselTrackingConfirmedEvent event = (CreateVesselTrackingConfirmedEvent) VesselTrackingEventFactory
				.getEvent(source, VesselTrackingEventTypes.CREATE_CONFIRMED);

		assertEquals(VesselTrackingEventTypes.CREATE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnUpdateVesselTrackingConfirmedEvent_IfTypeIsUpdateConfirmed() {

		Event source = VesselTrackingDataUtil.getUpdateVesselTrackingConfirmedEvent();
		UpdateVesselTrackingConfirmedEvent event = (UpdateVesselTrackingConfirmedEvent) VesselTrackingEventFactory
				.getEvent(source, VesselTrackingEventTypes.UPDATE_CONFIRMED);

		assertEquals(VesselTrackingEventTypes.UPDATE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTrackingConfirmedEvent_IfTypeIsDeleteConfirmed() {

		Event source = VesselTrackingDataUtil.getDeleteVesselTrackingConfirmedEvent();
		DeleteVesselTrackingConfirmedEvent event = (DeleteVesselTrackingConfirmedEvent) VesselTrackingEventFactory
				.getEvent(source, VesselTrackingEventTypes.DELETE_CONFIRMED);

		assertEquals(VesselTrackingEventTypes.DELETE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTrackingCheckedEvent_IfTypeIsDeleted() {

		Event source = VesselTrackingDataUtil.getDeleteVesselTrackingConfirmedEvent();
		VesselTrackingDeletedEvent event = (VesselTrackingDeletedEvent) VesselTrackingEventFactory.getEvent(source,
				VesselTrackingEventTypes.DELETED);

		assertEquals(VesselTrackingEventTypes.DELETED, event.getType());

		checkMetadataFields(source, event);
	}

	/////////////////////////

	@Test
	public void GetEvent_ReturnCreateVesselTrackingEnrichedEvent_IfTypeIsCreateEnriched() {

		Event source = VesselTrackingDataUtil.getEnrichCreateVesselTrackingEvent();
		CreateVesselTrackingEnrichedEvent event = (CreateVesselTrackingEnrichedEvent) VesselTrackingEventFactory
				.getEvent(source, VesselTrackingEventTypes.CREATE_ENRICHED,
						((VesselTrackingEvent) source).getVesselTracking());

		assertEquals(VesselTrackingEventTypes.CREATE_ENRICHED, event.getType());
		assertNotNull(event.getVesselTracking());
		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnUpdateVesselTrackingEnrichedEvent_IfTypeIsUpdateEnriched() {

		Event source = VesselTrackingDataUtil.getEnrichUpdateVesselTrackingEvent();
		UpdateVesselTrackingEnrichedEvent event = (UpdateVesselTrackingEnrichedEvent) VesselTrackingEventFactory
				.getEvent(source, VesselTrackingEventTypes.UPDATE_ENRICHED,
						((VesselTrackingEvent) source).getVesselTracking());

		assertEquals(VesselTrackingEventTypes.UPDATE_ENRICHED, event.getType());
		assertNotNull(event.getVesselTracking());
		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnCreateVesselTrackingEvent_IfTypeIsCreate() {

		Event source = VesselTrackingDataUtil.getCreateVesselTrackingEnrichedEvent();
		CreateVesselTrackingEvent event = (CreateVesselTrackingEvent) VesselTrackingEventFactory.getEvent(source,
				VesselTrackingEventTypes.CREATE, VesselTrackingDataUtil.getVesselTracking());

		assertEquals(VesselTrackingEventTypes.CREATE, event.getType());
		assertNotNull(event.getVesselTracking());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnVesselTrackingCreatedEvent_IfTypeIsCreated() {

		Event source = VesselTrackingDataUtil.getCreateConfirmedEvent();
		VesselTrackingCreatedEvent event = (VesselTrackingCreatedEvent) VesselTrackingEventFactory.getEvent(source,
				VesselTrackingEventTypes.CREATED, VesselTrackingDataUtil.getVesselTracking());

		assertEquals(VesselTrackingEventTypes.CREATED, event.getType());
		assertNotNull(event.getVesselTracking());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnUpdateVesselTrackingEvent_IfTypeIsUpdate() {

		Event source = VesselTrackingDataUtil.getUpdateVesselTrackingEnrichedEvent();
		UpdateVesselTrackingEvent event = (UpdateVesselTrackingEvent) VesselTrackingEventFactory.getEvent(source,
				VesselTrackingEventTypes.UPDATE, VesselTrackingDataUtil.getVesselTracking());

		assertEquals(VesselTrackingEventTypes.UPDATE, event.getType());
		assertNotNull(event.getVesselTracking());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnVesselTrackingCreatedEvent_IfTypeIsUpdated() {

		Event source = VesselTrackingDataUtil.getUpdateVesselTrackingConfirmedEvent();
		VesselTrackingUpdatedEvent event = (VesselTrackingUpdatedEvent) VesselTrackingEventFactory.getEvent(source,
				VesselTrackingEventTypes.UPDATED, VesselTrackingDataUtil.getVesselTracking());

		assertEquals(VesselTrackingEventTypes.UPDATED, event.getType());
		assertNotNull(event.getVesselTracking());

		checkMetadataFields(source, event);
	}

	///////////////////

	@Test
	public void GetEvent_ReturnUpdateVesselInVesselTrackingEvent_IfTypeIsUpdatedVessel() {

		Event source = VesselTrackingDataUtil.getVesselTrackingUpdatedEvent();
		VesselUpdatedEvent vesselUpdated = VesselDataUtil.getVesselUpdatedEvent();

		UpdateVesselInVesselTrackingEvent event = (UpdateVesselInVesselTrackingEvent) VesselTrackingEventFactory
				.getEvent(source, vesselUpdated, VesselTrackingEventTypes.UPDATE_VESSEL);

		assertEquals(VesselTrackingEventTypes.UPDATE_VESSEL, event.getType());
		assertNotNull(event.getVessel());
		assertEquals(source.getAggregateId(), event.getAggregateId());
		assertEquals(vesselUpdated.getUserId(), event.getUserId());

		Integer versionExpected = source.getVersion() + 1;
		assertEquals(versionExpected.toString(), event.getVersion().toString());
	}

	///////////////////

	@Test
	public void GetEvent_ReturnCreateVesselTrackingFailedEvent_IfTypeIsCreateFailed() {

		CreateVesselTrackingFailedEvent exception = VesselTrackingDataUtil.getCreateVesselTrackingFailedEvent();

		Event source = VesselTrackingDataUtil.getCreateEvent();

		CreateVesselTrackingFailedEvent event = (CreateVesselTrackingFailedEvent) VesselTrackingEventFactory.getEvent(
				source, VesselTrackingEventTypes.CREATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselTrackingEventTypes.CREATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnUpdateVesselTrackingFailedEvent_IfTypeIsUpdateFailed() {

		UpdateVesselTrackingFailedEvent exception = VesselTrackingDataUtil.getUpdateVesselTrackingFailedEvent();

		Event source = VesselTrackingDataUtil.getUpdateEvent();

		UpdateVesselTrackingFailedEvent event = (UpdateVesselTrackingFailedEvent) VesselTrackingEventFactory.getEvent(
				source, VesselTrackingEventTypes.UPDATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselTrackingEventTypes.UPDATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTrackingFailedEventt_IfTypeIsDeleteFailed() {

		DeleteVesselTrackingFailedEvent exception = VesselTrackingDataUtil.getDeleteVesselTrackingFailedEvent();

		Event source = VesselTrackingDataUtil.getDeleteEvent();

		DeleteVesselTrackingFailedEvent event = (DeleteVesselTrackingFailedEvent) VesselTrackingEventFactory.getEvent(
				source, VesselTrackingEventTypes.DELETE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselTrackingEventTypes.DELETE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTrackingCheckFailedEvent_IfTypeIsDeleteCheckFailed() {

		DeleteVesselTrackingCheckFailedEvent exception = VesselTrackingDataUtil
				.getDeleteVesselTrackingCheckFailedEvent();

		Event source = VesselTrackingDataUtil.getCheckDeleteVesselTrackingEvent();

		DeleteVesselTrackingCheckFailedEvent event = (DeleteVesselTrackingCheckFailedEvent) VesselTrackingEventFactory
				.getEvent(source, VesselTrackingEventTypes.DELETE_CHECK_FAILED, exception.getExceptionType(),
						exception.getArguments());

		assertEquals(VesselTrackingEventTypes.DELETE_CHECK_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnCreateVesselTrackingCancelledEvent_IfTypeIsCreateCancelled() {

		CreateVesselTrackingCancelledEvent exception = VesselTrackingDataUtil.getCreateVesselTrackingCancelledEvent();

		Event source = VesselTrackingDataUtil.getCreateEvent();

		CreateVesselTrackingCancelledEvent event = (CreateVesselTrackingCancelledEvent) VesselTrackingEventFactory
				.getEvent(source, VesselTrackingEventTypes.CREATE_CANCELLED, exception.getExceptionType(),
						exception.getArguments());

		assertEquals(VesselTrackingEventTypes.CREATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	////////////////////

	@Test
	public void GetEvent_ReturnUpdateVesselTrackingCancelledEvent_IfTypeIsUpdateCancelled() {

		UpdateVesselTrackingCancelledEvent exception = VesselTrackingDataUtil.getUpdateVesselTrackingCancelledEvent();

		Event source = VesselTrackingDataUtil.getUpdateEvent();

		UpdateVesselTrackingCancelledEvent event = (UpdateVesselTrackingCancelledEvent) VesselTrackingEventFactory
				.getEvent(source, VesselTrackingEventTypes.UPDATE_CANCELLED, VesselTrackingDataUtil.getVesselTracking(),
						exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselTrackingEventTypes.UPDATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getVesselTracking());
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTrackingCancelledEvent_IfTypeIsDeleteCancelled() {

		DeleteVesselTrackingCancelledEvent exception = VesselTrackingDataUtil.getDeleteVesselTrackingCancelledEvent();

		Event source = VesselTrackingDataUtil.getDeleteEvent();

		DeleteVesselTrackingCancelledEvent event = (DeleteVesselTrackingCancelledEvent) VesselTrackingEventFactory
				.getEvent(source, VesselTrackingEventTypes.DELETE_CANCELLED, VesselTrackingDataUtil.getVesselTracking(),
						exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselTrackingEventTypes.DELETE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getVesselTracking());
	}

	////////////////////

	private void checkMetadataFields(Event source, Event evt) {

		assertEquals(source.getAggregateId(), evt.getAggregateId());
		assertEquals(source.getVersion(), evt.getVersion());
		assertEquals(source.getSessionId(), evt.getSessionId());
		assertEquals(source.getUserId(), evt.getUserId());
	}

	private void checkErrorFields(EventError source, EventError evt) {

		assertEquals(source.getExceptionType(), evt.getExceptionType());
		assertEquals(source.getArguments(), evt.getArguments());
	}
}
