package es.redmic.vesselscommands.commands.vesseltracking;

import es.redmic.commandslib.commands.Command;

public class DeleteVesselTrackingCommand extends Command {

	private String vesselTrackingId;

	public DeleteVesselTrackingCommand(String id) {

		this.setVesselTrackingId(id);
	}

	public String getVesselTrackingId() {
		return vesselTrackingId;
	}

	public void setVesselTrackingId(String vesselTrackingId) {
		this.vesselTrackingId = vesselTrackingId;
	}
}
