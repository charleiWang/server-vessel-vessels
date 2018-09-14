package es.redmic.vesselscommands.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.redmic.commandslib.controller.CommandController;
import es.redmic.vesselscommands.service.VesselTypeCommandService;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;

@Controller
@RequestMapping(value = "${controller.mapping.vesseltype}")
public class VesselTypeController extends CommandController<VesselTypeDTO> {

	@Autowired
	public VesselTypeController(VesselTypeCommandService service) {
		super(service);
	}
}
