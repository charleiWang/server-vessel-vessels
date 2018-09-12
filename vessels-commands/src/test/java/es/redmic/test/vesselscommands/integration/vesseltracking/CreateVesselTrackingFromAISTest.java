package es.redmic.test.vesselscommands.integration.vesseltracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.geodata.tracking.vessels.AISTrackingDTO;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.test.vesselscommands.integration.KafkaEmbeddedConfig;
import es.redmic.test.vesselscommands.integration.vessel.VesselDataUtil;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CreateVesselTrackingFromAISTest",
		"schema.registry.port=18182" })
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel-tracking}", groupId = "test")
public class CreateVesselTrackingFromAISTest extends KafkaBaseIntegrationTest {

	@Value("${broker.topic.realtime.tracking.vessels}")
	String REALTIME_TRACKING_VESSELS_TOPIC;

	@Value("${broker.topic.vessel}")
	String VESSEL_TOPIC;

	private Integer mmsi = 1;

	private VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getVesselCreatedEvent(mmsi);

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	@Autowired
	private KafkaTemplate<String, AISTrackingDTO> kafkaTemplate;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplateEvent;

	protected static BlockingQueue<Object> blockingQueue;

	@PostConstruct
	public void CreateVesselFromTrackingTestPostConstruct() throws Exception {
		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Test
	public void createVesselTracking_SendCreateVesselTrackingEvent_IfCommandWasSuccess() throws Exception {

		VesselDTO vesselSource = vesselCreatedEvent.getVessel();

		AISTrackingDTO dto = new AISTrackingDTO();
		dto.setMmsi(vesselSource.getMmsi());
		dto.setImo(vesselSource.getImo());
		dto.setName(vesselSource.getName());
		dto.setType(Integer.parseInt(vesselSource.getType().getCode()));
		dto.setCallSign(vesselSource.getCallSign());
		dto.setTstamp(new DateTime());
		dto.setLatitude(2.1);
		dto.setLongitude(3.2);
		dto.setA(vesselSource.getLength() / 2);
		dto.setB(vesselSource.getLength() / 2);
		dto.setC(vesselSource.getBeam() / 2);
		dto.setD(vesselSource.getBeam() / 2);
		dto.setCog(2.3);
		dto.setSog(3.4);
		dto.setHeading(221);
		dto.setNavStat(33);
		dto.setEta("00:00 00:00");
		dto.setDest("Santa Cruz de Tenerife");

		ListenableFuture<SendResult<String, AISTrackingDTO>> future = kafkaTemplate
				.send(REALTIME_TRACKING_VESSELS_TOPIC, dto.getMmsi().toString(), dto);
		future.addCallback(new SendListener());

		VesselTrackingDTO vesselTracking = (VesselTrackingDTO) blockingQueue.poll(120, TimeUnit.SECONDS);
		assertNotNull(vesselTracking);

		assertTrue(vesselTracking.getProperties().getDate().isEqual(dto.getTstamp()));
		assertEquals(vesselTracking.getProperties().getCog(), dto.getCog());
		assertEquals(vesselTracking.getProperties().getSog(), dto.getSog());
		assertEquals(vesselTracking.getProperties().getHeading(), dto.getHeading());
		assertEquals(vesselTracking.getProperties().getNavStat(), dto.getNavStat());
		assertEquals(vesselTracking.getProperties().getEta(), dto.getEta());
		assertEquals(vesselTracking.getProperties().getDest(), dto.getDest());

		VesselDTO vessel = vesselTracking.getProperties().getVessel();

		assertEquals(vessel.getMmsi(), dto.getMmsi());
		assertEquals(vessel.getName(), dto.getName());
		Double length = dto.getA() + dto.getB();
		assertEquals(vessel.getLength(), length);
		Double beam = dto.getC() + dto.getD();
		assertEquals(vessel.getBeam(), beam);

	}

	@KafkaListener(topics = "${broker.topic.vessel}", groupId = "test")
	public void listen(Event event) {

		if (event.getType().equals(VesselEventTypes.CREATE)) {
			ListenableFuture<SendResult<String, Event>> futureCreatedEvent = kafkaTemplateEvent.send(VESSEL_TOPIC,
					"vessel-mmsi-" + mmsi, vesselCreatedEvent);
			futureCreatedEvent.addCallback(new SendListener());
		}
	}

	@KafkaHandler
	public void listen(CreateVesselTrackingEvent createEvent) {

		blockingQueue.offer(createEvent.getVesselTracking());
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
