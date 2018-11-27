package es.redmic.vesselscommands.commands.vesseltracking;

import java.util.UUID;

import org.joda.time.DateTime;

import es.redmic.commandslib.commands.Command;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.utils.VesselTrackingUtil;
import es.redmic.vesselslib.utils.VesselTypeUtil;
import es.redmic.vesselslib.utils.VesselUtil;

public class CreateVesselTrackingCommand extends Command {

	private VesselTrackingDTO vesselTracking;

	public CreateVesselTrackingCommand() {
	}

	public CreateVesselTrackingCommand(VesselTrackingDTO vesselTracking) {

		VesselDTO vessel = vesselTracking.getProperties().getVessel();

		if (vessel == null || (vessel.getMmsi() == null && vessel.getId() == null))
			throw new FieldNotValidException("mmsi", "null");

		// Se añade id generado a vessel para poder buscarlo
		if (vessel != null && vessel.getId() == null) {
			vesselTracking.getProperties().getVessel().setId(VesselUtil.generateId(vessel.getMmsi()));
			vesselTracking.getProperties().getVessel().getType()
					.setId(VesselTypeUtil.generateId(vessel.getType().getCode()));
		}

		if (vesselTracking.getProperties().getDate() == null)
			throw new FieldNotValidException("date", "null");

		if (vesselTracking.getId() == null) {
			// Se crea un id único para vesselTracking
			vesselTracking.setId(VesselTrackingUtil.generateId(vesselTracking.getProperties().getVessel().getMmsi(),
					vesselTracking.getProperties().getDate().getMillis()));
		}

		if (vesselTracking.getUuid() == null) {
			vesselTracking.setUuid(UUID.randomUUID().toString());
		}

		vesselTracking.getProperties().setInserted(DateTime.now());
		vesselTracking.getProperties().setUpdated(DateTime.now());

		this.setVesselTracking(vesselTracking);
	}

	public VesselTrackingDTO getVesselTracking() {
		return vesselTracking;
	}

	public void setVesselTracking(VesselTrackingDTO vesselTracking) {
		this.vesselTracking = vesselTracking;
	}
}
