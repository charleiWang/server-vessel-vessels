package es.redmic.vesselscommands.commands;

import es.redmic.commandslib.commands.Command;

public class DeleteVesselCommand extends Command {

	private String vesselId;

	public DeleteVesselCommand(String id) {

		this.setVesselId(id);
	}

	public String getVesselId() {
		return vesselId;
	}

	public void setVesselId(String vesselId) {
		this.vesselId = vesselId;
	}
}
