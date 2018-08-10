package es.redmic.vesselscommands.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.brokerlib.avro.serde.hashmap.HashMapSerde;
import es.redmic.commandslib.statestore.StreamConfig;
import es.redmic.commandslib.streams.EventStreams;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vessel.VesselEventType;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class VesselEventStreams extends EventStreams {

	private String vesselTypeTopic;

	public VesselEventStreams(StreamConfig config, String vesselTypeTopic, AlertService alertService) {
		super(config, alertService);
		this.vesselTypeTopic = vesselTypeTopic;
		logger.info("Arrancado servicio de compensación de errores de edición y PostUpdate de Vessel con Id: "
				+ this.serviceId);
		init();
	}

	@Override
	protected void processCreatedStream(KStream<String, Event> vesselEvents) {

		// Stream filtrado por eventos de confirmación al crear
		KStream<String, Event> createConfirmedEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.CREATE_VESSEL_CONFIRMED.toString().equals(event.getType())));

		// Stream filtrado por eventos de petición de crear
		KStream<String, Event> createRequestEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.CREATE_VESSEL.toString().equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		createConfirmedEvents.join(createRequestEvents,
				(confirmedEvent, requestEvent) -> getCreatedEvent(confirmedEvent, requestEvent),
				JoinWindows.of(windowsTime)).to(topic);
	}

	private Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(VesselEventType.CREATE_VESSEL.name());

		assert confirmedEvent.getType().equals(VesselEventType.CREATE_VESSEL_CONFIRMED.name());

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		VesselDTO vessel = ((VesselEvent) requestEvent).getVessel();

		logger.info("Creando evento VesselCreatedEvent para: " + confirmedEvent.getAggregateId());

		VesselCreatedEvent successfulEvent = new VesselCreatedEvent().buildFrom(confirmedEvent);
		successfulEvent.setVessel(vessel);
		return successfulEvent;
	}

	@Override
	protected void processUpdatedStream(KStream<String, Event> vesselEvents) {

		// Stream filtrado por eventos de confirmación al modificar
		KStream<String, Event> updateConfirmedEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.UPDATE_VESSEL_CONFIRMED.toString().equals(event.getType())));

		// Stream filtrado por eventos de petición de modificar
		KStream<String, Event> updateRequestEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.UPDATE_VESSEL.toString().equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		updateConfirmedEvents.join(updateRequestEvents,
				(confirmedEvent, requestEvent) -> getUpdatedEvent(confirmedEvent, requestEvent),
				JoinWindows.of(windowsTime)).to(topic);
	}

	private Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(VesselEventType.UPDATE_VESSEL.name());

		assert confirmedEvent.getType().equals(VesselEventType.UPDATE_VESSEL_CONFIRMED.name());

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		VesselDTO vessel = ((VesselEvent) requestEvent).getVessel();

		logger.info("Creando evento VesselUpdatedEvent para: " + confirmedEvent.getAggregateId());

		VesselUpdatedEvent successfulEvent = new VesselUpdatedEvent().buildFrom(confirmedEvent);
		successfulEvent.setVessel(vessel);
		return successfulEvent;
	}

	@Override
	protected void processFailedChangeStream(KStream<String, Event> vesselEvents) {

		// Stream filtrado por eventos de creaciones y modificaciones correctos (solo el
		// último que se produzca por id)
		KStream<String, Event> successEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.VESSEL_CREATED.toString().equals(event.getType())
						|| VesselEventType.VESSEL_UPDATED.toString().equals(event.getType())));

		processUpdateFailedStream(vesselEvents, successEvents);

		processDeleteFailedStream(vesselEvents, successEvents);
	}

	protected void processUpdateFailedStream(KStream<String, Event> vesselEvents,
			KStream<String, Event> successEvents) {

		// Stream filtrado por eventos de fallo al modificar
		KStream<String, Event> failedEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.UPDATE_VESSEL_FAILED.toString().equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		failedEvents
				.join(successEventsTable,
						(failedEvent, lastSuccessEvent) -> getUpdateCancelledEvent(failedEvent, lastSuccessEvent))
				.to(topic);
	}

	protected void processDeleteFailedStream(KStream<String, Event> vesselEvents,
			KStream<String, Event> successEvents) {

		// Stream filtrado por eventos de fallo al borrar
		KStream<String, Event> failedEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.DELETE_VESSEL_FAILED.toString().equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		failedEvents
				.join(successEventsTable,
						(failedEvent, lastSuccessEvent) -> getDeleteCancelledEvent(failedEvent, lastSuccessEvent))
				.to(topic);
	}

	private Event getUpdateCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert lastSuccessEvent.getType().equals(VesselEventType.VESSEL_CREATED.name())
				|| lastSuccessEvent.getType().equals(VesselEventType.VESSEL_UPDATED.name());

		assert failedEvent.getType().equals(VesselEventType.UPDATE_VESSEL_FAILED.name());

		VesselDTO vessel = ((VesselEvent) lastSuccessEvent).getVessel();

		EventError eventError = (EventError) failedEvent;

		logger.info("Enviando evento UpdateVesselCancelledEvent para: " + failedEvent.getAggregateId());

		UpdateVesselCancelledEvent cancelledEvent = new UpdateVesselCancelledEvent().buildFrom(failedEvent);
		cancelledEvent.setVessel(vessel);
		cancelledEvent.setExceptionType(eventError.getExceptionType());
		cancelledEvent.setArguments(eventError.getArguments());
		return cancelledEvent;
	}

	private Event getDeleteCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert lastSuccessEvent.getType().equals(VesselEventType.VESSEL_CREATED.name())
				|| lastSuccessEvent.getType().equals(VesselEventType.VESSEL_UPDATED.name());

		assert failedEvent.getType().equals(VesselEventType.DELETE_VESSEL_FAILED.name());

		VesselDTO vessel = ((VesselEvent) lastSuccessEvent).getVessel();

		EventError eventError = (EventError) failedEvent;

		logger.info("Enviando evento DeleteVesselCancelledEvent para: " + failedEvent.getAggregateId());

		DeleteVesselCancelledEvent cancelledEvent = new DeleteVesselCancelledEvent().buildFrom(failedEvent);
		cancelledEvent.setVessel(vessel);
		cancelledEvent.setExceptionType(eventError.getExceptionType());
		cancelledEvent.setArguments(eventError.getArguments());
		return cancelledEvent;
	}

	@Override
	protected void processPostUpdateStream(KStream<String, Event> vesselEvents) {

		// Stream filtrado por eventos que contengan Vessel dentro que son los de
		// comienzo y final
		// del ciclo
		KStream<String, Event> vesselEventsStream = vesselEvents.filter((id, event) -> {
			return (event instanceof VesselEvent);
		});

		KStream<String, Event> vesselEventsStreamByTypeId = vesselEventsStream
				.selectKey((k, v) -> ((VesselEvent) v).getVessel().getType().getId());

		KTable<String, HashMap<String, VesselEvent>> vesselEventsTable = vesselEventsStreamByTypeId.groupByKey()
				.aggregate(HashMap::new, (key, value, hashMap) -> {
					// Añade a hashmap cada uno de los values
					hashMap.put(value.getAggregateId(), (VesselEvent) value);
					return hashMap;
				}, Materialized.with(Serdes.String(), new HashMapSerde<>(schemaRegistry)));

		// Vesseltypes modificados
		KStream<String, Event> vesselTypeEvents = builder.stream(vesselTypeTopic);

		KStream<String, Event> updateReferenceEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventType.VESSELTYPE_UPDATED.toString().equals(event.getType())));

		KStream<String, ArrayList<VesselEvent>> join = updateReferenceEvents.join(vesselEventsTable,
				(updateReferenceEvent, vesselWithReferenceEvents) -> getPostUpdateEvent(updateReferenceEvent,
						vesselWithReferenceEvents));

		// desagregar, cambiar clave por la de vessel y enviar a topic
		join.flatMapValues(value -> value).selectKey((k, v) -> v.getVessel().getId()).to(topic);
	}

	private ArrayList<VesselEvent> getPostUpdateEvent(Event updateReferenceEvent,
			HashMap<String, VesselEvent> vesselWithReferenceEvents) {

		ArrayList<VesselEvent> result = new ArrayList<>();

		for (Map.Entry<String, VesselEvent> entry : vesselWithReferenceEvents.entrySet()) {

			VesselEvent vesselEvent = entry.getValue();
			VesselTypeDTO vesselType = ((VesselTypeEvent) updateReferenceEvent).getVesselType();

			if (itemIsLocked(vesselEvent.getType())) {

				if (!vesselEvent.getType().equals(VesselEventType.VESSEL_DELETED.toString())) {
					String message = "Item con id " + vesselEvent.getAggregateId()
							+ " se encuentra en mitad de un ciclo de creación o edición, por lo que no se modificó la referencia "
							+ updateReferenceEvent.getAggregateId();

					logger.info(message);
					alertService.errorAlert(vesselEvent.getAggregateId(), message);
				}

			} else {

				logger.debug("Creando evento de update para Vessel " + vesselEvent.getAggregateId()
						+ " por cambio en vesselType");

				if (!vesselEvent.getVessel().getType().equals(vesselType)) {

					UpdateVesselEvent updateVesselEvent = new UpdateVesselEvent();
					updateVesselEvent.setAggregateId(vesselEvent.getAggregateId());
					updateVesselEvent.setUserId(updateReferenceEvent.getUserId());
					updateVesselEvent.setVersion(vesselEvent.getVersion() + 1);

					VesselDTO vessel = vesselEvent.getVessel();
					vessel.setType(((VesselTypeEvent) updateReferenceEvent).getVesselType());
					updateVesselEvent.setVessel(vessel);
					result.add(updateVesselEvent);
				} else {
					logger.debug("VesselType ya estaba actualizado o los campos indexados no han cambiado ");
				}
			}
		}
		return result;
	}

	private boolean itemIsLocked(String type) {

		return !(VesselEventType.VESSEL_CREATED.toString().equals(type)
				|| VesselEventType.VESSEL_UPDATED.toString().equals(type)
				|| VesselEventType.CREATE_VESSEL_CANCELLED.toString().equals(type)
				|| VesselEventType.UPDATE_VESSEL_CANCELLED.toString().equals(type)
				|| VesselEventType.DELETE_VESSEL_CANCELLED.toString().equals(type));
	}
}
