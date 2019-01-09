package es.redmic.vesselscommands.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.redmic.commandslib.controller.CommandController;
import es.redmic.models.es.common.dto.SuperDTO;
import es.redmic.vesselscommands.service.VesselCommandService;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

@Controller
public class VesselController extends CommandController<VesselDTO> {

	@Autowired
	public VesselController(VesselCommandService service) {
		super(service);
	}

	@Override
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public SuperDTO delete(@PathVariable("id") String id) {
		return null;
	}
}
