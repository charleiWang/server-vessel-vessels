package es.redmic.vesselslib.unit.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import es.redmic.vesselslib.events.vessel.create.CreateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.VesselTypeInVesselUpdatedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
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

	@Test
	public void UpdateVesselTypeInVesselConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTypeInVesselConfirmedEvent event = VesselDataUtil.getUpdateVesselTypeInVesselConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTypeInVesselConfirmedEvent",
				UpdateVesselTypeInVesselConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void VesselTypeInVesselUpdatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselTypeInVesselUpdatedEvent event = VesselDataUtil.getVesselTypeInVesselUpdatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de VesselTypeInVesselUpdatedEvent",
				VesselTypeInVesselUpdatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselTypeInVesselFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTypeInVesselFailedEvent event = VesselDataUtil.getUpdateVesselTypeInVesselFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTypeInVesselFailedEvent",
				UpdateVesselTypeInVesselFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselTypeInVesselCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTypeInVesselCancelledEvent event = VesselDataUtil.getUpdateVesselTypeInVesselCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTypeInVesselCancelledEvent",
				UpdateVesselTypeInVesselCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}
}
