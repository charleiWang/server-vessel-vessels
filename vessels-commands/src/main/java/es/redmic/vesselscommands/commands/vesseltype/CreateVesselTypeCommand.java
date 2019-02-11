package es.redmic.vesselscommands.commands.vesseltype;

import es.redmic.commandslib.commands.Command;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.utils.VesselTypeUtil;

public class CreateVesselTypeCommand extends Command {

	private VesselTypeDTO vesselType;

	public CreateVesselTypeCommand() {
	}

	public CreateVesselTypeCommand(VesselTypeDTO vesselType) {

		if (vesselType.getId() == null && vesselType.getCode() == null)
			throw new FieldNotValidException("code", "null");

		if (vesselType.getId() == null && vesselType.getCode() != null) {
			// Se crea un id único para el vessel
			vesselType.setId(VesselTypeUtil.generateId(vesselType.getCode()));
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
