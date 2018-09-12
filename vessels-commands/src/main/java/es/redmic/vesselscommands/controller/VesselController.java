package es.redmic.vesselscommands.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import es.redmic.commandslib.controller.CommandController;
import es.redmic.vesselscommands.service.VesselCommandService;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

@Controller
public class VesselController extends CommandController<VesselDTO> {

	@Autowired
	public VesselController(VesselCommandService service) {
		super(service);
	}
}
