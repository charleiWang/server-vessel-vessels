package es.redmic.vesselscommands.commands.vesseltype;

import es.redmic.commandslib.commands.Command;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselslib.dto.VesselTypeDTO;

public class CreateVesselTypeCommand extends Command {

	private final String PREFIX = "vesseltype-code-";

	private VesselTypeDTO vesselType;

	public CreateVesselTypeCommand() {
	}

	public CreateVesselTypeCommand(VesselTypeDTO vesselType) {

		if (vesselType.getId() == null && vesselType.getCode() == null)
			throw new FieldNotValidException("code", "null");

		if (vesselType.getId() == null && vesselType.getCode() != null) {
			// Se crea un id único para el vessel
			vesselType.setId(PREFIX + vesselType.getCode());
		}
		this.setVessel(vesselType);
	}

	public VesselTypeDTO getVesselType() {
		return vesselType;
	}

	public void setVessel(VesselTypeDTO vesselType) {
		this.vesselType = vesselType;
	}
}
