package es.redmic.vesselslib.unit.events.vessel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import es.redmic.vesselslib.events.vessel.create.CreateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEnrichedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.create.EnrichCreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.CheckDeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCheckFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCheckedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselEvent;
import es.redmic.vesselslib.events.vessel.update.EnrichUpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEnrichedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;
import es.redmic.vesselslib.unit.utils.VesselAvroBaseTest;
import es.redmic.vesselslib.unit.utils.VesselDataUtil;

public class VesselEventsCheckAvroSchemaTest extends VesselAvroBaseTest {

	// Create

	@Test
	public void CreateVesselEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselEvent event = VesselDataUtil.getCreateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselEvent",
				CreateVesselEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void EnrichCreateVesselEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		EnrichCreateVesselEvent event = VesselDataUtil.getEnrichCreateVesselEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de EnrichCreateVesselEvent",
				EnrichCreateVesselEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselEnrichedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselEnrichedEvent event = VesselDataUtil.getCreateVesselEnrichedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselEnrichedEvent",
				CreateVesselEnrichedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselConfirmedEvent event = VesselDataUtil.getCreateConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselConfirmedEvent",
				CreateVesselConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void VesselCreatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect()
			throws JsonProcessingException {

		VesselCreatedEvent event = VesselDataUtil.getCreatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de VesselCreatedEvent",
				VesselCreatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselFailedEvent event = VesselDataUtil.getCreateVesselFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselFailedEvent",
				CreateVesselFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselCancelledEvent event = VesselDataUtil.getCreateVesselCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselCancelledEvent",
				CreateVesselCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Update

	@Test
	public void UpdateVesselEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselEvent event = VesselDataUtil.getUpdateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselEvent",
				UpdateVesselEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void EnrichUpdateVesselEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		EnrichUpdateVesselEvent event = VesselDataUtil.getEnrichUpdateVesselEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de EnrichUpdateVesselEvent",
				EnrichUpdateVesselEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselEnrichedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselEnrichedEvent event = VesselDataUtil.getUpdateVesselEnrichedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselEnrichedEvent",
				UpdateVesselEnrichedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselConfirmedEvent event = new UpdateVesselConfirmedEvent();

		Schema schema = ReflectData.get().getSchema(UpdateVesselConfirmedEvent.class);

		assertEquals(event.getSchema(), schema);
	}

	@Test
	public void VesselUpdatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselUpdatedEvent event = VesselDataUtil.getVesselUpdatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de VesselUpdatedEvent",
				VesselUpdatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselFailedEvent event = VesselDataUtil.getUpdateVesselFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselFailedEvent",
				UpdateVesselFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselCancelledEvent event = VesselDataUtil.getUpdateVesselCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselCancelledEvent",
				UpdateVesselCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Delete

	@Test
	public void DeleteVesselEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselEvent event = VesselDataUtil.getDeleteEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselEvent",
				DeleteVesselEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CheckDeleteVesselEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CheckDeleteVesselEvent event = VesselDataUtil.getCheckDeleteVesselEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CheckDeleteVesselEvent",
				CheckDeleteVesselEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselCheckedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselCheckedEvent event = VesselDataUtil.getDeleteVesselCheckedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselCheckedEvent",
				DeleteVesselCheckedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselCheckFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselCheckFailedEvent event = VesselDataUtil.getDeleteVesselCheckFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselCheckFailedEvent",
				DeleteVesselCheckFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselConfirmedEvent event = VesselDataUtil.getDeleteVesselConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselConfirmedEvent",
				DeleteVesselConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void VesselDeletedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselDeletedEvent event = VesselDataUtil.getVesselDeletedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de VesselDeletedEvent",
				VesselDeletedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselFailedEvent event = VesselDataUtil.getDeleteVesselFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselFailedEvent",
				DeleteVesselFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselCancelledEvent event = VesselDataUtil.getDeleteVesselCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselCancelledEvent",
				DeleteVesselCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// UpdateVesselTypeInVessel

	@Test
	public void UpdateVesselTypeInVesselEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTypeInVesselEvent event = VesselDataUtil.getUpdateVesselTypeInVesselEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTypeInVesselEvent",
				UpdateVesselTypeInVesselEvent.class.isInstance(result));

		assertEquals(result, event);
	}
}
