package es.redmic.vesselscommands.commands;

import es.redmic.commandslib.commands.Command;
import es.redmic.vesselslib.dto.VesselTypeDTO;

public class UpdateVesselTypeCommand extends Command {

	private VesselTypeDTO vesselType;

	public UpdateVesselTypeCommand() {
	}

	public UpdateVesselTypeCommand(VesselTypeDTO vesselType) {
		this.setVesselType(vesselType);
	}

	public VesselTypeDTO getVesselType() {
		return vesselType;
	}

	public void setVesselType(VesselTypeDTO vesselType) {
		this.vesselType = vesselType;
	}
}
