package es.redmic.vesselscommands.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.commandslib.service.CommandServiceItfc;
import es.redmic.vesselscommands.commands.vesseltype.CreateVesselTypeCommand;
import es.redmic.vesselscommands.commands.vesseltype.DeleteVesselTypeCommand;
import es.redmic.vesselscommands.commands.vesseltype.UpdateVesselTypeCommand;
import es.redmic.vesselscommands.handler.VesselTypeCommandHandler;
import es.redmic.vesselslib.dto.VesselTypeDTO;

@Service
public class VesselTypeCommandService implements CommandServiceItfc<VesselTypeDTO> {

	protected static Logger logger = LogManager.getLogger();

	private final VesselTypeCommandHandler commandHandler;

	@Autowired
	public VesselTypeCommandService(VesselTypeCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public VesselTypeDTO create(VesselTypeDTO vessel) {

		logger.debug("Create VesselType");

		return commandHandler.save(new CreateVesselTypeCommand(vessel));
	}

	@Override
	public VesselTypeDTO update(String id, VesselTypeDTO vessel) {

		logger.debug("Update VesselType");

		return commandHandler.update(id, new UpdateVesselTypeCommand(vessel));
	}

	@Override
	public VesselTypeDTO delete(String id) {

		logger.debug("Delete VesselType");

		return commandHandler.update(id, new DeleteVesselTypeCommand(id));
	}
}
