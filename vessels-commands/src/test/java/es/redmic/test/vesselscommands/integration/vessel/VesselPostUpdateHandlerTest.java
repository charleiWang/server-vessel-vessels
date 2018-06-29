package es.redmic.test.vesselscommands.integration.vessel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.redmic.brokerlib.alert.AlertType;
import es.redmic.brokerlib.alert.Message;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.test.vesselscommands.integration.common.CommonIntegrationTest;
import es.redmic.test.vesselscommands.integration.vesseltype.VesselTypeDataUtil;
import es.redmic.vesselslib.events.vessel.VesselEventType;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@KafkaListener(topics = "${broker.topic.vessel}", groupId = "test")
public class VesselPostUpdateHandlerTest extends CommonIntegrationTest {

	protected static Logger logger = LogManager.getLogger();

	private static final Integer mmsi = 1234;

	@Value("${broker.topic.vessel}")
	private String VESSEL_TOPIC;

	@Value("${broker.topic.vessel-type}")
	private String VESSELTYPE_TOPIC;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	protected static BlockingQueue<Object> blockingQueueForAlerts;

	@Before
	public void before() {

		blockingQueue = new LinkedBlockingDeque<>();

		blockingQueueForAlerts = new LinkedBlockingDeque<>();
	}

	// PostUpdate

	// Envía un evento de vesseltype modificado y debe provocar un evento de vessel
	// modificado para cada uno de los vessel que tiene el vesseltype modificado.
	@Test
	public void vesselTypeUpdatedEvent_SendUpdateVesselEvent_IfReceivesSuccess() throws Exception {

		// Referencia a modificar
		VesselTypeUpdatedEvent vesselTypeUpdatedEvent = VesselTypeDataUtil.getVesselTypeUpdatedEvent("70");
		vesselTypeUpdatedEvent.getVesselType().setName("other");

		// Envía created para que genere un evento postUpdate y lo saca de la cola
		VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getVesselCreatedEvent(mmsi + 7);
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent.getAggregateId(), vesselCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		Thread.sleep(2000);

		// Envía created para que genere un evento postUpdate y lo saca de la cola
		VesselCreatedEvent vesselCreatedEvent2 = VesselDataUtil.getVesselCreatedEvent(mmsi + 8);
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent2.getAggregateId(), vesselCreatedEvent2);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		Thread.sleep(2000);

		// Envía created con vesselType actualizado para comprobar que no genera evento
		VesselCreatedEvent vesselCreatedEvent3 = VesselDataUtil.getVesselCreatedEvent(mmsi + 9);
		vesselCreatedEvent3.getVessel().setType(vesselTypeUpdatedEvent.getVesselType());
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent3.getAggregateId(), vesselCreatedEvent3);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía create para simular uno a medias en el stream y lo saca de la cola
		CreateVesselEvent createVesselEvent = VesselDataUtil.getCreateEvent(mmsi + 10);
		kafkaTemplate.send(VESSEL_TOPIC, createVesselEvent.getAggregateId(), createVesselEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía evento de vesselType actualizado para que genere los eventos de
		// postUpdate
		kafkaTemplate.send(VESSELTYPE_TOPIC, vesselTypeUpdatedEvent.getAggregateId(), vesselTypeUpdatedEvent);

		Event update = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNotNull(update);
		assertEquals(VesselEventType.UPDATE_VESSEL.toString(), update.getType());
		assertEquals(vesselTypeUpdatedEvent.getVesselType(), ((UpdateVesselEvent) update).getVessel().getType());
		assertEquals(vesselCreatedEvent.getAggregateId(), ((UpdateVesselEvent) update).getAggregateId());

		Event update2 = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNotNull(update2);
		assertEquals(VesselEventType.UPDATE_VESSEL.toString(), update2.getType());
		assertEquals(vesselTypeUpdatedEvent.getVesselType(), ((UpdateVesselEvent) update2).getVessel().getType());
		assertEquals(vesselCreatedEvent2.getAggregateId(), ((UpdateVesselEvent) update2).getAggregateId());

		Event update3 = (Event) blockingQueue.poll(20, TimeUnit.SECONDS);
		assertNull(update3);

		Message message = (Message) blockingQueueForAlerts.poll(30, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());
	}

	@KafkaHandler
	public void vesselCreatedEvent(VesselCreatedEvent vesselCreatedEvent) {

		blockingQueue.offer(vesselCreatedEvent);
	}

	@KafkaHandler
	public void updateVesselEventFromPostUpdate(UpdateVesselEvent updateVesselEvent) {

		blockingQueue.offer(updateVesselEvent);
	}

	@KafkaListener(topics = "${broker.topic.alert}", groupId = "test")
	public void errorAlert(Message message) {
		blockingQueueForAlerts.offer(message);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
