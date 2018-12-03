package es.redmic.vesselscommands.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.redmic.commandslib.controller.CommandGeoController;
import es.redmic.vesselscommands.service.VesselTrackingCommandService;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;

@Controller
@RequestMapping(value = "${controller.mapping.vesseltracking}")
public class VesselTrackingController extends CommandGeoController<VesselTrackingDTO> {

	@Autowired
	public VesselTrackingController(VesselTrackingCommandService service) {
		super(service);
	}
}
