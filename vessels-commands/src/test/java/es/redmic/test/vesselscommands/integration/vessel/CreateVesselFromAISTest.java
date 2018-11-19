package es.redmic.test.vesselscommands.integration.vessel;

/*-@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CreateVesselFromAIS", "schema.registry.port=18082" })
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel}", groupId = "CreateVesselFromAISTest")
public class CreateVesselFromAISTest extends KafkaBaseIntegrationTest {

	@Value("${broker.topic.realtime.tracking.vessels}")
	String REALTIME_TRACKING_VESSELS_TOPIC;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	@Autowired
	private KafkaTemplate<String, AISTrackingDTO> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@PostConstruct
	public void CreateVesselFromAISTestPostConstruct() throws Exception {
		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Test
	public void createVessel_SendCreateVesselEvent_IfCommandWasSuccess() throws Exception {

		AISTrackingDTO dto = new AISTrackingDTO();
		dto.setMmsi(2);
		dto.setImo(1);
		dto.setName("Avatar");
		dto.setType(70);
		dto.setCallSign("1");
		dto.setTstamp(new DateTime());
		dto.setLatitude(2.1);
		dto.setLongitude(3.2);
		dto.setA(2.9);
		dto.setB(3.5);
		dto.setC(1.4);
		dto.setD(1.4);

		ListenableFuture<SendResult<String, AISTrackingDTO>> future = kafkaTemplate
				.send(REALTIME_TRACKING_VESSELS_TOPIC, "vessel-mmsi-" + dto.getMmsi(), dto);
		future.addCallback(new SendListener());

		VesselDTO vessel = (VesselDTO) blockingQueue.poll(120, TimeUnit.SECONDS);
		assertNotNull(vessel);
		assertEquals(vessel.getMmsi(), dto.getMmsi());
		assertEquals(vessel.getName(), dto.getName());
		Double length = dto.getA() + dto.getB();
		assertEquals(vessel.getLength(), length);

		Double beam = dto.getC() + dto.getD();
		assertEquals(vessel.getBeam(), beam);
	}

	@KafkaHandler
	public void listen(CreateVesselEnrichedEvent createVesselEnrichedEvent) {

		blockingQueue.offer(createVesselEnrichedEvent.getVessel());
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}-*/