package es.redmic.vesselscommands.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import es.redmic.brokerlib.avro.geodata.tracking.vessels.AISTrackingDTO;
import es.redmic.commandslib.controller.CommandController;
import es.redmic.vesselscommands.service.VesselCommandService;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

@Controller
public class VesselController extends CommandController<VesselDTO> {

	VesselCommandService service;

	@Autowired
	public VesselController(VesselCommandService service) {
		super(service);
		this.service = service;
	}

	@KafkaListener(topics = "${broker.topic.realtime.tracking.vessels}")
	public void run(AISTrackingDTO dto) {

		logger.info("Procesando barco: " + dto.getMmsi() + " date: " + dto.getTstamp());
		service.create(dto);
	}

}
