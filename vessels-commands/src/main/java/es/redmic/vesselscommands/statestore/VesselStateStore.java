package es.redmic.vesselscommands.statestore;

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

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.statestore.StateStore;

public class VesselStateStore extends StateStore {

	public VesselStateStore(StreamConfig config, AlertService alertService) {
		super(config, alertService);
		init();
	}

	public Event getVessel(String id) {

		return store.get(id);
	}
}
