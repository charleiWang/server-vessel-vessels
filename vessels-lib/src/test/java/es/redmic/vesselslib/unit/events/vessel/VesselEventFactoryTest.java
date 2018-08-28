package es.redmic.vesselslib.unit.events.vessel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vessel.VesselEventFactory;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.create.CreateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCheckFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCheckedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;
import es.redmic.vesselslib.unit.utils.VesselDataUtil;
import es.redmic.vesselslib.unit.utils.VesselTypeDataUtil;

public class VesselEventFactoryTest {

	@Test
	public void GetEvent_ReturnDeleteVesselEvent_IfTypeIsDelete() {

		Event source = VesselDataUtil.getDeleteVesselCheckedEvent();
		DeleteVesselEvent event = (DeleteVesselEvent) VesselEventFactory.getEvent(source, VesselEventTypes.DELETE);

		assertEquals(VesselEventTypes.DELETE, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselCheckedEvent_IfTypeIsDelete_Checked() {

		Event source = VesselDataUtil.getCheckDeleteVesselEvent();
		DeleteVesselCheckedEvent event = (DeleteVesselCheckedEvent) VesselEventFactory.getEvent(source,
				VesselEventTypes.DELETE_CHECKED);

		assertEquals(VesselEventTypes.DELETE_CHECKED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselCheckedEvent_IfTypeIsDeleted() {

		Event source = VesselDataUtil.getDeleteVesselConfirmedEvent();
		VesselDeletedEvent event = (VesselDeletedEvent) VesselEventFactory.getEvent(source, VesselEventTypes.DELETED);

		assertEquals(VesselEventTypes.DELETED, event.getType());

		checkMetadataFields(source, event);
	}

	/////////////////////////

	@Test
	public void GetEvent_ReturnVesselCreatedEvent_IfTypeIsCreated() {

		Event source = VesselDataUtil.getCreateConfirmedEvent();
		VesselCreatedEvent event = (VesselCreatedEvent) VesselEventFactory.getEvent(source, VesselEventTypes.CREATED,
				VesselDataUtil.getVessel());

		assertEquals(VesselEventTypes.CREATED, event.getType());
		assertNotNull(event.getVessel());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnVesselCreatedEvent_IfTypeIsUpdated() {

		Event source = VesselDataUtil.getUpdateVesselConfirmedEvent();
		VesselUpdatedEvent event = (VesselUpdatedEvent) VesselEventFactory.getEvent(source, VesselEventTypes.UPDATED,
				VesselDataUtil.getVessel());

		assertEquals(VesselEventTypes.UPDATED, event.getType());
		assertNotNull(event.getVessel());

		checkMetadataFields(source, event);
	}

	///////////////////

	@Test
	public void GetEvent_ReturnUpdateVesselTypeInVesselEvent_IfTypeIsUpdatedVesselType() {

		Event source = VesselDataUtil.getVesselUpdatedEvent();
		VesselTypeUpdatedEvent vesselTypeUpdated = VesselTypeDataUtil.getVesselTypeUpdatedEvent();

		UpdateVesselTypeInVesselEvent event = (UpdateVesselTypeInVesselEvent) VesselEventFactory.getEvent(source,
				vesselTypeUpdated, VesselEventTypes.UPDATE_VESSELTYPE);

		assertEquals(VesselEventTypes.UPDATE_VESSELTYPE, event.getType());
		assertNotNull(event.getVesselType());
		assertEquals(source.getAggregateId(), event.getAggregateId());
		assertEquals(vesselTypeUpdated.getUserId(), event.getUserId());

		Integer versionExpected = source.getVersion() + 1;
		assertEquals(versionExpected.toString(), event.getVersion().toString());
	}

	///////////////////

	@Test
	public void GetEvent_ReturnCreateVesselFailedEvent_IfTypeIsCreateFailed() {

		CreateVesselFailedEvent exception = VesselDataUtil.getCreateVesselFailedEvent();

		Event source = VesselDataUtil.getCreateEvent();

		CreateVesselFailedEvent event = (CreateVesselFailedEvent) VesselEventFactory.getEvent(source,
				VesselEventTypes.CREATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselEventTypes.CREATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnUpdateVesselFailedEvent_IfTypeIsUpdateFailed() {

		UpdateVesselFailedEvent exception = VesselDataUtil.getUpdateVesselFailedEvent();

		Event source = VesselDataUtil.getUpdateEvent();

		UpdateVesselFailedEvent event = (UpdateVesselFailedEvent) VesselEventFactory.getEvent(source,
				VesselEventTypes.UPDATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselEventTypes.UPDATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselFailedEventt_IfTypeIsDeleteFailed() {

		DeleteVesselFailedEvent exception = VesselDataUtil.getDeleteVesselFailedEvent();

		Event source = VesselDataUtil.getDeleteEvent();

		DeleteVesselFailedEvent event = (DeleteVesselFailedEvent) VesselEventFactory.getEvent(source,
				VesselEventTypes.DELETE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselEventTypes.DELETE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselCheckFailedEvent_IfTypeIsDeleteCheckFailed() {

		DeleteVesselCheckFailedEvent exception = VesselDataUtil.getDeleteVesselCheckFailedEvent();

		Event source = VesselDataUtil.getCheckDeleteVesselEvent();

		DeleteVesselCheckFailedEvent event = (DeleteVesselCheckFailedEvent) VesselEventFactory.getEvent(source,
				VesselEventTypes.DELETE_CHECK_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselEventTypes.DELETE_CHECK_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnCreateVesselCancelledEvent_IfTypeIsCreateCancelled() {

		CreateVesselCancelledEvent exception = VesselDataUtil.getCreateVesselCancelledEvent();

		Event source = VesselDataUtil.getCreateEvent();

		CreateVesselCancelledEvent event = (CreateVesselCancelledEvent) VesselEventFactory.getEvent(source,
				VesselEventTypes.CREATE_CANCELLED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselEventTypes.CREATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	////////////////////

	@Test
	public void GetEvent_ReturnUpdateVesselCancelledEvent_IfTypeIsUpdateCancelled() {

		UpdateVesselCancelledEvent exception = VesselDataUtil.getUpdateVesselCancelledEvent();

		Event source = VesselDataUtil.getUpdateEvent();

		UpdateVesselCancelledEvent event = (UpdateVesselCancelledEvent) VesselEventFactory.getEvent(source,
				VesselEventTypes.UPDATE_CANCELLED, VesselDataUtil.getVessel(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(VesselEventTypes.UPDATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getVessel());
	}

	@Test
	public void GetEvent_ReturnDeleteVesselCancelledEvent_IfTypeIsDeleteCancelled() {

		DeleteVesselCancelledEvent exception = VesselDataUtil.getDeleteVesselCancelledEvent();

		Event source = VesselDataUtil.getDeleteEvent();

		DeleteVesselCancelledEvent event = (DeleteVesselCancelledEvent) VesselEventFactory.getEvent(source,
				VesselEventTypes.DELETE_CANCELLED, VesselDataUtil.getVessel(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(VesselEventTypes.DELETE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getVessel());
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
