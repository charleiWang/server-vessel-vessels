package es.redmic.vesselsview.controller.vesseltracking;

/*-
 * #%L
 * Vessels-query-endpoint
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventFactory;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselsview.model.vesseltracking.VesselTracking;
import es.redmic.vesselsview.service.vesseltracking.VesselTrackingESService;
import es.redmic.viewlib.config.MapperScanBeanItfc;
import es.redmic.viewlib.geodata.controller.GeoDataController;

@Controller
@RequestMapping(value = "${controller.mapping.vesseltracking}")
@KafkaListener(topics = "${broker.topic.vessel-tracking}")
public class VesselTrackingController extends GeoDataController<VesselTracking, VesselTrackingDTO, DataQueryDTO> {

	@Value("${broker.topic.vessel-tracking}")
	private String vesseltracking_topic;

	@Autowired
	MapperScanBeanItfc mapper;

	VesselTrackingESService service;

	@Autowired
	public VesselTrackingController(VesselTrackingESService service) {
		super(service);
		this.service = service;
	}

	@KafkaHandler
	public void listen(CreateVesselTrackingEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.save(mapper.getMapperFacade().map(event.getVesselTracking(), VesselTracking.class));
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.CREATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vesseltracking_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.CREATE_CONFIRMED),
					vesseltracking_topic);
		} else {
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.CREATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vesseltracking_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateVesselTrackingEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.update(mapper.getMapperFacade().map(event.getVesselTracking(), VesselTracking.class));
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.UPDATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vesseltracking_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.UPDATE_CONFIRMED),
					vesseltracking_topic);
		} else {
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.UPDATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vesseltracking_topic);
		}
	}

	@KafkaHandler
	public void listen(DeleteVesselTrackingEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.delete(event.getAggregateId());
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.DELETE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vesseltracking_topic);
			return;
		}

		if (result.isSuccess()) {

			publishConfirmedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.DELETE_CONFIRMED),
					vesseltracking_topic);
		} else {
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.DELETE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vesseltracking_topic);
		}
	}

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}
}
