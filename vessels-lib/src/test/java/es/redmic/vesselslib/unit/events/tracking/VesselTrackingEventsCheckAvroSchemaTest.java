package es.redmic.vesselslib.unit.events.tracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.EnrichCreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.CheckDeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCheckFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCheckedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel.UpdateVesselInVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.EnrichUpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;
import es.redmic.vesselslib.unit.utils.VesselAvroBaseTest;
import es.redmic.vesselslib.unit.utils.VesselTrackingDataUtil;

public class VesselTrackingEventsCheckAvroSchemaTest extends VesselAvroBaseTest {

	// Create

	@Test
	public void CreateVesselTrackingEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselTrackingEvent event = VesselTrackingDataUtil.getCreateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTrackingEvent",
				CreateVesselTrackingEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void EnrichCreateVesselTrackingEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		EnrichCreateVesselTrackingEvent event = VesselTrackingDataUtil.getEnrichCreateVesselTrackingEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de EnrichCreateVesselTrackingEvent",
				EnrichCreateVesselTrackingEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselTrackingEnrichedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselTrackingEnrichedEvent event = VesselTrackingDataUtil.getCreateVesselTrackingEnrichedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTrackingEnrichedEvent",
				CreateVesselTrackingEnrichedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselTrackingConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselTrackingConfirmedEvent event = VesselTrackingDataUtil.getCreateConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTrackingConfirmedEvent",
				CreateVesselTrackingConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void VesselTrackingCreatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect()
			throws JsonProcessingException {

		VesselTrackingCreatedEvent event = VesselTrackingDataUtil.getCreatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de VesselTrackingCreatedEvent",
				VesselTrackingCreatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselTrackingFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselTrackingFailedEvent event = VesselTrackingDataUtil.getCreateVesselTrackingFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTrackingFailedEvent",
				CreateVesselTrackingFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselTrackingCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselTrackingCancelledEvent event = VesselTrackingDataUtil.getCreateVesselTrackingCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTrackingCancelledEvent",
				CreateVesselTrackingCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Update

	@Test
	public void UpdateVesselTrackingEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTrackingEvent event = VesselTrackingDataUtil.getUpdateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTrackingEvent",
				UpdateVesselTrackingEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void EnrichUpdateVesselTrackingEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		EnrichUpdateVesselTrackingEvent event = VesselTrackingDataUtil.getEnrichUpdateVesselTrackingEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de EnrichUpdateVesselTrackingEvent",
				EnrichUpdateVesselTrackingEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselTrackingEnrichedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTrackingEnrichedEvent event = VesselTrackingDataUtil.getUpdateVesselTrackingEnrichedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTrackingEnrichedEvent",
				UpdateVesselTrackingEnrichedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselTrackingConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTrackingConfirmedEvent event = new UpdateVesselTrackingConfirmedEvent();

		Schema schema = ReflectData.get().getSchema(UpdateVesselTrackingConfirmedEvent.class);

		assertEquals(event.getSchema(), schema);
	}

	@Test
	public void VesselTrackingUpdatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselTrackingUpdatedEvent event = VesselTrackingDataUtil.getVesselTrackingUpdatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de VesselTrackingUpdatedEvent",
				VesselTrackingUpdatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselTrackingFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTrackingFailedEvent event = VesselTrackingDataUtil.getUpdateVesselTrackingFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTrackingFailedEvent",
				UpdateVesselTrackingFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselTrackingCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTrackingCancelledEvent event = VesselTrackingDataUtil.getUpdateVesselTrackingCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTrackingCancelledEvent",
				UpdateVesselTrackingCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Delete

	@Test
	public void DeleteVesselTrackingEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselTrackingEvent event = VesselTrackingDataUtil.getDeleteEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselTrackingEvent",
				DeleteVesselTrackingEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CheckDeleteVesselTrackingEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CheckDeleteVesselTrackingEvent event = VesselTrackingDataUtil.getCheckDeleteVesselTrackingEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CheckDeleteVesselTrackingEvent",
				CheckDeleteVesselTrackingEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselTrackingCheckedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselTrackingCheckedEvent event = VesselTrackingDataUtil.getDeleteVesselTrackingCheckedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselTrackingCheckedEvent",
				DeleteVesselTrackingCheckedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselTrackingCheckFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselTrackingCheckFailedEvent event = VesselTrackingDataUtil.getDeleteVesselTrackingCheckFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselTrackingCheckFailedEvent",
				DeleteVesselTrackingCheckFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselTrackingConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselTrackingConfirmedEvent event = VesselTrackingDataUtil.getDeleteVesselTrackingConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselTrackingConfirmedEvent",
				DeleteVesselTrackingConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void VesselTrackingDeletedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselTrackingDeletedEvent event = VesselTrackingDataUtil.getVesselTrackingDeletedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de VesselTrackingDeletedEvent",
				VesselTrackingDeletedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselTrackingFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselTrackingFailedEvent event = VesselTrackingDataUtil.getDeleteVesselTrackingFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselTrackingFailedEvent",
				DeleteVesselTrackingFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselTrackingCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselTrackingCancelledEvent event = VesselTrackingDataUtil.getDeleteVesselTrackingCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTrackingCancelledEvent",
				DeleteVesselTrackingCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// UpdateVesselTrackingTypeInVesselTracking

	@Test
	public void UpdateVesselInVesselTrackingEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselInVesselTrackingEvent event = VesselTrackingDataUtil.getUpdateVesselInVesselTrackingEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselInVesselTrackingEvent",
				UpdateVesselInVesselTrackingEvent.class.isInstance(result));

		assertEquals(result, event);
	}
}
