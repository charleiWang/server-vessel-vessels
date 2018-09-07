package es.redmic.vesselscommands.commands.vesseltracking;

import java.util.UUID;

import org.joda.time.DateTime;

import es.redmic.commandslib.commands.Command;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselscommands.commands.vessel.CreateVesselCommand;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

public class CreateVesselTrackingCommand extends Command {

	private final String PREFIX = "vesseltracking-mmsi-tstamp-";

	private VesselTrackingDTO vesselTracking;

	public CreateVesselTrackingCommand() {
	}

	public CreateVesselTrackingCommand(VesselTrackingDTO vesselTracking) {

		VesselDTO vessel = vesselTracking.getProperties().getVessel();

		// Se añade id generado a vessel para poder buscarlo
		if (vessel != null && vessel.getId() == null) {
			vesselTracking.getProperties().getVessel().setId(new CreateVesselCommand(vessel).getVessel().getId());
		}

		if (vesselTracking.getProperties().getVessel().getMmsi() == null)
			throw new FieldNotValidException("mmsi", "null");

		if (vesselTracking.getProperties().getDate() == null)
			throw new FieldNotValidException("date", "null");

		if (vesselTracking.getId() == null) {
			// Se crea un id único para vesselTracking
			vesselTracking.setId(PREFIX + vesselTracking.getProperties().getVessel().getMmsi() + "-"
					+ vesselTracking.getProperties().getDate().getMillis());
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
