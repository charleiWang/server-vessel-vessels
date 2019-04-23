package es.redmic.vesselsview.controller.vesseltype;

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
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventFactory;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselsview.config.MapperScanBean;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import es.redmic.vesselsview.service.vesseltype.VesselTypeESService;
import es.redmic.viewlib.data.controller.DataController;

@Controller
@RequestMapping(value = "${controller.mapping.vesseltype}")
@KafkaListener(topics = "${broker.topic.vessel-type}")
public class VesselTypeController extends DataController<VesselType, VesselTypeDTO, SimpleQueryDTO> {

	@Value("${broker.topic.vessel-type}")
	private String vessel_type_topic;

	@Autowired
	protected MapperScanBean mapper;

	VesselTypeESService service;

	@Autowired
	public VesselTypeController(VesselTypeESService service) {
		super(service);
		this.service = service;
	}

	@KafkaHandler
	public void listen(CreateVesselTypeEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.save(mapper.getMapperFacade().map(event.getVesselType(), VesselType.class));
		} catch (Exception e) {
			publishFailedEvent(VesselTypeEventFactory.getEvent(event, VesselTypeEventTypes.CREATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_type_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new CreateVesselTypeConfirmedEvent().buildFrom(event), vessel_type_topic);
		} else {
			publishFailedEvent(VesselTypeEventFactory.getEvent(event, VesselTypeEventTypes.CREATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_type_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateVesselTypeEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.update(mapper.getMapperFacade().map(event.getVesselType(), VesselType.class));
		} catch (Exception e) {
			publishFailedEvent(VesselTypeEventFactory.getEvent(event, VesselTypeEventTypes.UPDATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_type_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new UpdateVesselTypeConfirmedEvent().buildFrom(event), vessel_type_topic);
		} else {
			publishFailedEvent(VesselTypeEventFactory.getEvent(event, VesselTypeEventTypes.UPDATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_type_topic);
		}
	}

	@KafkaHandler
	public void listen(DeleteVesselTypeEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.delete(event.getAggregateId());
		} catch (Exception e) {
			publishFailedEvent(VesselTypeEventFactory.getEvent(event, VesselTypeEventTypes.DELETE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_type_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new DeleteVesselTypeConfirmedEvent().buildFrom(event), vessel_type_topic);
		} else {
			publishFailedEvent(VesselTypeEventFactory.getEvent(event, VesselTypeEventTypes.DELETE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_type_topic);
		}
	}

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}
}
