package es.redmic.vesselscommands.commands;

import es.redmic.commandslib.commands.Command;

public class DeleteVesselTypeCommand extends Command {

	private String vesselTypeId;

	public DeleteVesselTypeCommand(String id) {

		this.setVesselTypeId(id);
	}

	public String getVesselTypeId() {
		return vesselTypeId;
	}

	public void setVesselTypeId(String vesselTypeId) {
		this.vesselTypeId = vesselTypeId;
	}
}
