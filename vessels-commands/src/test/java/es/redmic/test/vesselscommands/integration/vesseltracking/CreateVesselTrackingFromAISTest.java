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
import es.redmic.brokerlib.avro.geodata.tracking.vessels.AISTrackingDTO;
import es.redmic.test.vesselscommands.integration.KafkaEmbeddedConfig;
import es.redmic.test.vesselscommands.integration.vessel.VesselDataUtil;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselscommands.handler.VesselTrackingCommandHandler;
import es.redmic.vesselscommands.service.VesselTrackingCommandService;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
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

	VesselDTO vessel;

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

		VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getVesselCreatedEvent(mmsi);

		vessel = vesselCreatedEvent.getVessel();

		AISTrackingDTO dto = new AISTrackingDTO();
		dto.setMmsi(vessel.getMmsi());
		dto.setImo(vessel.getImo());
		dto.setName(vessel.getName());
		dto.setType(Integer.parseInt(vessel.getType().getCode()));
		dto.setCallSign(vessel.getCallSign());
		dto.setTstamp(new DateTime());
		dto.setLatitude(2.1);
		dto.setLongitude(3.2);
		dto.setA(vessel.getLength() / 2);
		dto.setB(vessel.getLength() / 2);
		dto.setC(vessel.getBeam() / 2);
		dto.setD(vessel.getBeam() / 2);
		dto.setCog(2.3);
		dto.setSog(3.4);
		dto.setHeading(221);
		dto.setNavStat(33);
		dto.setEta("00:00 00:00");
		dto.setDest("Santa Cruz de Tenerife");

		// LLama directamente al servicio para evitar pasar por vessel
		service.create(dto);

		VesselTrackingDTO vesselTracking = (VesselTrackingDTO) blockingQueue.poll(4, TimeUnit.MINUTES);
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
