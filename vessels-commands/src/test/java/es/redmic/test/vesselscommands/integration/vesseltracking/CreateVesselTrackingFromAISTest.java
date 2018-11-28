package es.redmic.test.vesselscommands.integration.vesseltracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.test.vesselscommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselscommands.handler.VesselTrackingCommandHandler;
import es.redmic.vesselscommands.service.VesselTrackingCommandService;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CreateVesselTrackingFromAIS",
		"schema.registry.port=18182" })
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel-tracking}", groupId = "CreateVesselTrackingFromAISTest")
public class CreateVesselTrackingFromAISTest extends KafkaBaseIntegrationTest {

	@Value("${broker.topic.realtime.tracking.vessels}")
	String REALTIME_TRACKING_VESSELS_TOPIC;

	@Value("${broker.topic.vessel}")
	String VESSEL_TOPIC;

	private Integer mmsi = 1;

	private String tstamp = "343232132";

	@Value("${vesseltracking-activity-id}")
	protected String activityId;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	@Autowired
	VesselTrackingCommandService service;

	@Autowired
	VesselTrackingCommandHandler vesselTrackingCommandHandler;

	protected static BlockingQueue<Object> blockingQueue;

	@PostConstruct
	public void CreateVesselFromTrackingTestPostConstruct() throws Exception {
		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Test
	public void createVesselTracking_SendCreateVesselTrackingEvent_IfCommandWasSuccess() throws Exception {

		VesselTrackingDTO source = VesselTrackingDataUtil.getCreateEvent(mmsi, tstamp).getVesselTracking();

		// LLama directamente al servicio para evitar pasar por vessel
		service.create(source, activityId);

		VesselTrackingDTO vesselTracking = (VesselTrackingDTO) blockingQueue.poll(4, TimeUnit.MINUTES);
		assertNotNull(vesselTracking);

		assertTrue(vesselTracking.getProperties().getDate().isEqual(source.getProperties().getDate()));
		assertEquals(vesselTracking.getProperties().getCog(), source.getProperties().getCog());
		assertEquals(vesselTracking.getProperties().getSog(), source.getProperties().getSog());
		assertEquals(vesselTracking.getProperties().getHeading(), source.getProperties().getHeading());
		assertEquals(vesselTracking.getProperties().getNavStat(), source.getProperties().getNavStat());
		assertEquals(vesselTracking.getProperties().getEta(), source.getProperties().getEta());
		assertEquals(vesselTracking.getProperties().getDest(), source.getProperties().getDest());

		VesselDTO vessel = vesselTracking.getProperties().getVessel();

		assertEquals(vessel.getMmsi(), source.getProperties().getVessel().getMmsi());
		assertEquals(vessel.getName(), source.getProperties().getVessel().getName());
		assertEquals(vessel.getLength(), source.getProperties().getVessel().getLength());
		assertEquals(vessel.getBeam(), source.getProperties().getVessel().getBeam());
	}

	@KafkaHandler
	public void listen(CreateVesselTrackingEvent createVesselTrackingEvent) throws Exception {

		// Resuelve la espera para que siga la ejecución
		Whitebox.invokeMethod(vesselTrackingCommandHandler, "resolveCommand", createVesselTrackingEvent.getSessionId());

		blockingQueue.offer(createVesselTrackingEvent.getVesselTracking());
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Event def) {
	}
}
