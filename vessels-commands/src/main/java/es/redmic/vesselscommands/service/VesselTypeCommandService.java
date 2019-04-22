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
import es.redmic.vesselscommands.commands.vesseltype.CreateVesselTypeCommand;
import es.redmic.vesselscommands.commands.vesseltype.DeleteVesselTypeCommand;
import es.redmic.vesselscommands.commands.vesseltype.UpdateVesselTypeCommand;
import es.redmic.vesselscommands.handler.VesselTypeCommandHandler;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;

@Service
public class VesselTypeCommandService implements CommandServiceItfc<VesselTypeDTO> {

	protected static Logger logger = LogManager.getLogger();

	private final VesselTypeCommandHandler commandHandler;

	@Autowired
	public VesselTypeCommandService(VesselTypeCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public VesselTypeDTO create(VesselTypeDTO vessel) {

		logger.debug("Create VesselType");

		return commandHandler.save(new CreateVesselTypeCommand(vessel));
	}

	@Override
	public VesselTypeDTO update(String id, VesselTypeDTO vessel) {

		logger.debug("Update VesselType");

		return commandHandler.update(id, new UpdateVesselTypeCommand(vessel));
	}

	@Override
	public VesselTypeDTO delete(String id) {

		logger.debug("Delete VesselType");

		return commandHandler.update(id, new DeleteVesselTypeCommand(id));
	}
}
