package es.redmic.vesselscommands.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.redmic.brokerlib.avro.geodata.tracking.vessels.AISTrackingDTO;
import es.redmic.commandslib.controller.CommandGeoController;
import es.redmic.vesselscommands.service.VesselTrackingCommandService;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;

@Controller
@RequestMapping(value = "${controller.mapping.vesseltracking}")
public class VesselTrackingController extends CommandGeoController<VesselTrackingDTO> {

	VesselTrackingCommandService service;

	@Autowired
	public VesselTrackingController(VesselTrackingCommandService service) {
		super(service);
		this.service = service;
	}

	@KafkaListener(topics = "${broker.topic.realtime.tracking.vessels}")
	public void run(AISTrackingDTO dto) {

		logger.info("Procesando track para el barco: " + dto.getMmsi() + " date: " + dto.getTstamp());
		service.create(dto);
	}

}
