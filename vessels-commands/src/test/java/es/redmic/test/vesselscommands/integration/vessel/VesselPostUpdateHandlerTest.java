package es.redmic.test.vesselscommands.integration.vessel;

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

/*-@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel}", groupId = "VesselPostUpdateHandler")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=VesselPostUpdateHandlerTest",
		"schema.registry.port=18085" })
public class VesselPostUpdateHandlerTest extends KafkaBaseIntegrationTest {

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private static final Integer mmsi = 1111;

	@Value("${broker.topic.vessel}")
	private String VESSEL_TOPIC;

	@Value("${broker.topic.vessel-type}")
	private String VESSELTYPE_TOPIC;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected BlockingQueue<Object> blockingQueue;

	protected BlockingQueue<Object> blockingQueueForAlerts;

	protected BlockingQueue<Object> blockingQueueForUpdatedEvents;

	protected BlockingQueue<Object> blockingQueueForCancelledEvents;

	@PostConstruct
	public void VesselPostUpdateHandlerTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@Before
	public void before() {

		blockingQueue = new LinkedBlockingDeque<>();

		blockingQueueForAlerts = new LinkedBlockingDeque<>();

		blockingQueueForUpdatedEvents = new LinkedBlockingDeque<>();

		blockingQueueForCancelledEvents = new LinkedBlockingDeque<>();
	}

	// PostUpdate

	// Envía un evento de vesseltype modificado y debe provocar un evento de vessel
	// modificado para cada uno de los vessel que tiene el vesseltype modificado.
	@Test
	public void vesselTypeUpdatedEvent_SendUpdateVesselEvent_IfReceivesSuccess() throws Exception {

		// Referencia a modificar
		VesselTypeUpdatedEvent vesselTypeUpdatedEvent = VesselTypeDataUtil.getVesselTypeUpdatedEvent("70");
		vesselTypeUpdatedEvent.getVesselType().setName("other");

		// Envía created para que genere un evento postUpdate
		VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getVesselCreatedEvent(mmsi + 7);
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent.getAggregateId(), vesselCreatedEvent);
		Thread.sleep(3000);

		// Envía created para que genere un evento postUpdate
		VesselCreatedEvent vesselCreatedEvent2 = VesselDataUtil.getVesselCreatedEvent(mmsi + 8);
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent2.getAggregateId(), vesselCreatedEvent2);
		Thread.sleep(3000);

		// Envía created con vesselType actualizado para comprobar que no genera evento
		VesselCreatedEvent vesselCreatedEvent3 = VesselDataUtil.getVesselCreatedEvent(mmsi + 9);
		vesselCreatedEvent3.getVessel().setType(vesselTypeUpdatedEvent.getVesselType());
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent3.getAggregateId(), vesselCreatedEvent3);
		Thread.sleep(3000);

		// Envía create para simular uno a medias en el stream y lo saca de la cola
		CreateVesselEvent createVesselEvent = VesselDataUtil.getCreateEvent(mmsi + 10);
		kafkaTemplate.send(VESSEL_TOPIC, createVesselEvent.getAggregateId(), createVesselEvent);
		Thread.sleep(3000);

		// Envía evento de vesselType actualizado para que genere los eventos de
		// postUpdate
		kafkaTemplate.send(VESSELTYPE_TOPIC, vesselTypeUpdatedEvent.getAggregateId(), vesselTypeUpdatedEvent);

		UpdateVesselTypeInVesselEvent update = (UpdateVesselTypeInVesselEvent) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNotNull(update);
		assertEquals(VesselEventTypes.UPDATE_VESSELTYPE, update.getType());
		assertEquals(vesselTypeUpdatedEvent.getVesselType(), update.getVesselType());

		// Envía confirmación para simular que view lo insertó
		kafkaTemplate.send(VESSEL_TOPIC, update.getAggregateId(), new UpdateVesselConfirmedEvent().buildFrom(update));

		UpdateVesselTypeInVesselEvent update2 = (UpdateVesselTypeInVesselEvent) blockingQueue.poll(30,
				TimeUnit.SECONDS);
		assertNotNull(update2);
		assertEquals(VesselEventTypes.UPDATE_VESSELTYPE, update2.getType());
		assertEquals(vesselTypeUpdatedEvent.getVesselType(), update2.getVesselType());

		// Envía fallo para simular que view no lo insertó
		UpdateVesselFailedEvent updateVesselFailedEvent = new UpdateVesselFailedEvent().buildFrom(update2);
		updateVesselFailedEvent.setExceptionType(ExceptionType.ITEM_NOT_FOUND.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		updateVesselFailedEvent.setArguments(arguments);
		kafkaTemplate.send(VESSEL_TOPIC, update2.getAggregateId(), updateVesselFailedEvent);

		Event update3 = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNull(update3);

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(30, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());

		// Se modificó bien el primer vessel
		VesselUpdatedEvent updated = (VesselUpdatedEvent) blockingQueueForUpdatedEvents.poll(30, TimeUnit.SECONDS);
		assertNotNull(updated);
		assertEquals(VesselEventTypes.UPDATED, updated.getType());
		assertEquals(vesselTypeUpdatedEvent.getVesselType(), updated.getVessel().getType());
		assertEquals(update.getAggregateId(), updated.getAggregateId());

		// No se modificó bien el segundo vessel
		VesselCancelledEvent cancelled = (VesselCancelledEvent) blockingQueueForCancelledEvents.poll(30,
				TimeUnit.SECONDS);
		assertNotNull(cancelled);
		assertEquals(VesselEventTypes.UPDATE_CANCELLED, cancelled.getType());
		assertEquals(vesselCreatedEvent2.getVessel().getType(), cancelled.getVessel().getType());
		assertEquals(update2.getAggregateId(), cancelled.getAggregateId());

		// LLegó un mensaje de alerta
		Message message2 = (Message) blockingQueueForAlerts.poll(30, TimeUnit.SECONDS);
		assertNotNull(message2);
		assertEquals(AlertType.ERROR.name(), message2.getType());
	}

	@KafkaHandler
	public void updateVesselEventFromPostUpdate(UpdateVesselTypeInVesselEvent updateVesselEvent) {

		blockingQueue.offer(updateVesselEvent);
	}

	@KafkaHandler
	public void vesselUpdatedEventFromPostUpdate(VesselUpdatedEvent vesselUpdatedEvent) {

		blockingQueueForUpdatedEvents.offer(vesselUpdatedEvent);
	}

	@KafkaHandler
	public void vesselCancelledEventFromPostUpdate(VesselCancelledEvent vesselCancelledEvent) {

		blockingQueueForCancelledEvents.offer(vesselCancelledEvent);
	}

	@KafkaListener(topics = "${broker.topic.alert}", groupId = "test")
	public void errorAlert(Message message) {
		blockingQueueForAlerts.offer(message);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}-*/
