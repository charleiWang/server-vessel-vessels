package es.redmic.vesselslib.unit.events.vesseltype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventFactory;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCheckFailedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCheckedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;
import es.redmic.vesselslib.unit.utils.VesselTypeDataUtil;

public class VesselTypeEventFactoryTest {

	@Test
	public void GetEvent_ReturnDeleteVesselTypeEvent_IfTypeIsDelete() {

		Event source = VesselTypeDataUtil.getDeleteVesselTypeCheckedEvent();
		DeleteVesselTypeEvent event = (DeleteVesselTypeEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.DELETE);

		assertEquals(VesselTypeEventTypes.DELETE, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTypeCheckedEvent_IfTypeIsDelete_Checked() {

		Event source = VesselTypeDataUtil.getCheckDeleteVesselTypeEvent();
		DeleteVesselTypeCheckedEvent event = (DeleteVesselTypeCheckedEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.DELETE_CHECKED);

		assertEquals(VesselTypeEventTypes.DELETE_CHECKED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnCreateVesselTypeConfirmedEvent_IfTypeIsCreateConfirmed() {

		Event source = VesselTypeDataUtil.getCreateVesselTypeConfirmedEvent();
		CreateVesselTypeConfirmedEvent event = (CreateVesselTypeConfirmedEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.CREATE_CONFIRMED);

		assertEquals(VesselTypeEventTypes.CREATE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnUpdateVesselTypeConfirmedEvent_IfTypeIsUpdateConfirmed() {

		Event source = VesselTypeDataUtil.getUpdateVesselTypeConfirmedEvent();
		UpdateVesselTypeConfirmedEvent event = (UpdateVesselTypeConfirmedEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.UPDATE_CONFIRMED);

		assertEquals(VesselTypeEventTypes.UPDATE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTypeConfirmedEvent_IfTypeIsDeleteConfirmed() {

		Event source = VesselTypeDataUtil.getDeleteVesselTypeConfirmedEvent();
		DeleteVesselTypeConfirmedEvent event = (DeleteVesselTypeConfirmedEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.DELETE_CONFIRMED);

		assertEquals(VesselTypeEventTypes.DELETE_CONFIRMED, event.getType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTypeCheckedEvent_IfTypeIsDeleted() {

		Event source = VesselTypeDataUtil.getDeleteVesselTypeConfirmedEvent();
		VesselTypeDeletedEvent event = (VesselTypeDeletedEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.DELETED);

		assertEquals(VesselTypeEventTypes.DELETED, event.getType());

		checkMetadataFields(source, event);
	}

	/////////////////////////

	@Test
	public void GetEvent_ReturnVesselTypeCreatedEvent_IfTypeIsCreated() {

		Event source = VesselTypeDataUtil.getCreateVesselTypeConfirmedEvent();
		VesselTypeCreatedEvent event = (VesselTypeCreatedEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.CREATED, VesselTypeDataUtil.getVesselType());

		assertEquals(VesselTypeEventTypes.CREATED, event.getType());
		assertNotNull(event.getVesselType());

		checkMetadataFields(source, event);
	}

	@Test
	public void GetEvent_ReturnVesselTypeCreatedEvent_IfTypeIsUpdated() {

		Event source = VesselTypeDataUtil.getUpdateVesselTypeConfirmedEvent();
		VesselTypeUpdatedEvent event = (VesselTypeUpdatedEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.UPDATED, VesselTypeDataUtil.getVesselType());

		assertEquals(VesselTypeEventTypes.UPDATED, event.getType());
		assertNotNull(event.getVesselType());

		checkMetadataFields(source, event);
	}

	///////////////////

	@Test
	public void GetEvent_ReturnCreateVesselTypeFailedEvent_IfTypeIsCreateFailed() {

		CreateVesselTypeFailedEvent exception = VesselTypeDataUtil.getCreateVesselTypeFailedEvent();

		Event source = VesselTypeDataUtil.getCreateEvent();

		CreateVesselTypeFailedEvent event = (CreateVesselTypeFailedEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.CREATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselTypeEventTypes.CREATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnUpdateVesselTypeFailedEvent_IfTypeIsUpdateFailed() {

		UpdateVesselTypeFailedEvent exception = VesselTypeDataUtil.getUpdateVesselTypeFailedEvent();

		Event source = VesselTypeDataUtil.getUpdateEvent();

		UpdateVesselTypeFailedEvent event = (UpdateVesselTypeFailedEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.UPDATE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselTypeEventTypes.UPDATE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTypeFailedEventt_IfTypeIsDeleteFailed() {

		DeleteVesselTypeFailedEvent exception = VesselTypeDataUtil.getDeleteVesselTypeFailedEvent();

		Event source = VesselTypeDataUtil.getDeleteEvent();

		DeleteVesselTypeFailedEvent event = (DeleteVesselTypeFailedEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.DELETE_FAILED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselTypeEventTypes.DELETE_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTypeCheckFailedEvent_IfTypeIsDeleteCheckFailed() {

		DeleteVesselTypeCheckFailedEvent exception = VesselTypeDataUtil.getDeleteVesselTypeCheckFailedEvent();

		Event source = VesselTypeDataUtil.getCheckDeleteVesselTypeEvent();

		DeleteVesselTypeCheckFailedEvent event = (DeleteVesselTypeCheckFailedEvent) VesselTypeEventFactory.getEvent(
				source, VesselTypeEventTypes.DELETE_CHECK_FAILED, exception.getExceptionType(),
				exception.getArguments());

		assertEquals(VesselTypeEventTypes.DELETE_CHECK_FAILED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	@Test
	public void GetEvent_ReturnCreateVesselTypeCancelledEvent_IfTypeIsCreateCancelled() {

		CreateVesselTypeCancelledEvent exception = VesselTypeDataUtil.getCreateVesselTypeCancelledEvent();

		Event source = VesselTypeDataUtil.getCreateEvent();

		CreateVesselTypeCancelledEvent event = (CreateVesselTypeCancelledEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.CREATE_CANCELLED, exception.getExceptionType(), exception.getArguments());

		assertEquals(VesselTypeEventTypes.CREATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
	}

	////////////////////

	@Test
	public void GetEvent_ReturnUpdateVesselTypeCancelledEvent_IfTypeIsUpdateCancelled() {

		UpdateVesselTypeCancelledEvent exception = VesselTypeDataUtil.getUpdateVesselTypeCancelledEvent();

		Event source = VesselTypeDataUtil.getUpdateEvent();

		UpdateVesselTypeCancelledEvent event = (UpdateVesselTypeCancelledEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.UPDATE_CANCELLED, VesselTypeDataUtil.getVesselType(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(VesselTypeEventTypes.UPDATE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getVesselType());
	}

	@Test
	public void GetEvent_ReturnDeleteVesselTypeCancelledEvent_IfTypeIsDeleteCancelled() {

		DeleteVesselTypeCancelledEvent exception = VesselTypeDataUtil.getDeleteVesselTypeCancelledEvent();

		Event source = VesselTypeDataUtil.getDeleteEvent();

		DeleteVesselTypeCancelledEvent event = (DeleteVesselTypeCancelledEvent) VesselTypeEventFactory.getEvent(source,
				VesselTypeEventTypes.DELETE_CANCELLED, VesselTypeDataUtil.getVesselType(), exception.getExceptionType(),
				exception.getArguments());

		assertEquals(VesselTypeEventTypes.DELETE_CANCELLED, event.getType());

		checkMetadataFields(source, event);
		checkErrorFields(exception, event);
		assertNotNull(event.getVesselType());
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
