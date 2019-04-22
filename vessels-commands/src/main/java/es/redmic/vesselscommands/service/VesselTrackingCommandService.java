package es.redmic.vesselscommands.service;

/*-
 * #%L
 * Vessels-management
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.redmic.commandslib.service.CommandGeoServiceItfc;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselscommands.commands.vesseltracking.CreateVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.DeleteVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.UpdateVesselTrackingCommand;
import es.redmic.vesselscommands.handler.VesselTrackingCommandHandler;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;

@Service
public class VesselTrackingCommandService implements CommandGeoServiceItfc<VesselTrackingDTO> {

	protected static Logger logger = LogManager.getLogger();

	private final VesselTrackingCommandHandler commandHandler;

	@Value("${vesseltracking-activity-id}")
	protected String activityId;

	@Autowired
	public VesselTrackingCommandService(VesselTrackingCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public VesselTrackingDTO create(VesselTrackingDTO vesselTracking, String activityId) {

		logger.debug("CreateVesselTracking");

		checkActivityId(activityId);

		vesselTracking.getProperties().setActivity(activityId);

		return commandHandler.save(new CreateVesselTrackingCommand(vesselTracking));
	}

	@Override
	public VesselTrackingDTO update(String id, VesselTrackingDTO vesselTracking, String activityId) {

		logger.debug("UpdateVesselTracking");

		checkActivityId(activityId);

		vesselTracking.getProperties().setActivity(activityId);

		return commandHandler.update(id, new UpdateVesselTrackingCommand(vesselTracking));
	}

	@Override
	public VesselTrackingDTO delete(String id, String activityId) {

		logger.debug("DeleteVesselTracking");

		checkActivityId(activityId);

		return commandHandler.update(id, new DeleteVesselTrackingCommand(id));
	}

	private void checkActivityId(String requestId) {

		if (!requestId.equals(this.activityId))
			throw new FieldNotValidException("activityId", requestId);
	}
}
