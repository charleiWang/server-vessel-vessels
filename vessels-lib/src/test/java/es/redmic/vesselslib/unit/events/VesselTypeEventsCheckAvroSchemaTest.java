package es.redmic.vesselslib.unit.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;
import es.redmic.vesselslib.unit.utils.VesselAvroBaseTest;
import es.redmic.vesselslib.unit.utils.VesselTypeDataUtil;

public class VesselTypeEventsCheckAvroSchemaTest extends VesselAvroBaseTest {

	// Create

	@Test
	public void CreateVesselTypeEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselTypeEvent event = VesselTypeDataUtil.getCreateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTypeEvent",
				CreateVesselTypeEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselTypeConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselTypeConfirmedEvent event = VesselTypeDataUtil.getCreateVesselTypeConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTypeConfirmedEvent",
				CreateVesselTypeConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void VesselTypeCreatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselTypeCreatedEvent event = VesselTypeDataUtil.getVesselTypeCreatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de VesselTypeCreatedEvent",
				VesselTypeCreatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselTypeFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselTypeFailedEvent event = VesselTypeDataUtil.getCreateVesselTypeFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTypeFailedEvent",
				CreateVesselTypeFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void CreateVesselTypeCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		CreateVesselTypeCancelledEvent event = VesselTypeDataUtil.getCreateVesselTypeCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de CreateVesselTypeCancelledEvent",
				CreateVesselTypeCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Update

	@Test
	public void UpdateVesselTypeEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTypeEvent event = VesselTypeDataUtil.getUpdateEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTypeEvent",
				UpdateVesselTypeEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselTypeConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTypeConfirmedEvent event = VesselTypeDataUtil.getUpdateVesselTypeConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTypeConfirmedEvent",
				UpdateVesselTypeConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void VesselTypeUpdatedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselTypeUpdatedEvent event = VesselTypeDataUtil.getVesselTypeUpdatedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de VesselTypeUpdatedEvent",
				VesselTypeUpdatedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselTypeFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTypeFailedEvent event = VesselTypeDataUtil.getUpdateVesselTypeFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTypeFailedEvent",
				UpdateVesselTypeFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void UpdateVesselTypeCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		UpdateVesselTypeCancelledEvent event = VesselTypeDataUtil.getUpdateVesselTypeCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de UpdateVesselTypeCancelledEvent",
				UpdateVesselTypeCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	// Delete

	@Test
	public void DeleteVesselTypeEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselTypeEvent event = VesselTypeDataUtil.getDeleteEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselTypeEvent",
				DeleteVesselTypeEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselTypeConfirmedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselTypeConfirmedEvent event = VesselTypeDataUtil.getDeleteVesselTypeConfirmedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselTypeConfirmedEvent",
				DeleteVesselTypeConfirmedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void VesselTypeDeletedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		VesselTypeDeletedEvent event = VesselTypeDataUtil.getVesselTypeDeletedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de VesselTypeDeletedEvent",
				VesselTypeDeletedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselTypeFailedEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselTypeFailedEvent event = VesselTypeDataUtil.getDeleteVesselTypeFailedEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselTypeFailedEvent",
				DeleteVesselTypeFailedEvent.class.isInstance(result));

		assertEquals(result, event);
	}

	@Test
	public void DeleteVesselTypeCancelledEventSerializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		DeleteVesselTypeCancelledEvent event = VesselTypeDataUtil.getDeleteVesselTypeCancelledEvent();

		Object result = serializerAndDeserializer(event);

		assertTrue("El objeto obtenido debe ser una instancia de DeleteVesselTypeCancelledEvent",
				DeleteVesselTypeCancelledEvent.class.isInstance(result));

		assertEquals(result, event);
	}
}
