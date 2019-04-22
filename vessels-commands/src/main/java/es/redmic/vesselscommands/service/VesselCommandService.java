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
import org.springframework.stereotype.Service;

import es.redmic.commandslib.service.CommandServiceItfc;
import es.redmic.vesselscommands.commands.vessel.CreateVesselCommand;
import es.redmic.vesselscommands.commands.vessel.DeleteVesselCommand;
import es.redmic.vesselscommands.commands.vessel.UpdateVesselCommand;
import es.redmic.vesselscommands.handler.VesselCommandHandler;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

@Service
public class VesselCommandService implements CommandServiceItfc<VesselDTO> {

	protected static Logger logger = LogManager.getLogger();

	private final VesselCommandHandler commandHandler;

	@Autowired
	public VesselCommandService(VesselCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public VesselDTO create(VesselDTO vessel) {

		logger.debug("CreateVessel");

		return commandHandler.save(new CreateVesselCommand(vessel));
	}

	@Override
	public VesselDTO update(String id, VesselDTO vessel) {

		logger.debug("UpdateVessel");

		return commandHandler.update(id, new UpdateVesselCommand(vessel));
	}

	@Override
	public VesselDTO delete(String id) {

		logger.debug("DeleteVessel");

		return commandHandler.update(id, new DeleteVesselCommand(id));
	}
}
