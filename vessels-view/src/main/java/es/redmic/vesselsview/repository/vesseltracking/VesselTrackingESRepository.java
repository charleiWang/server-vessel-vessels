package es.redmic.vesselsview.repository.vesseltracking;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.elasticsearchlib.geodata.repository.RWGeoDataESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.vesselsview.model.vessel.Vessel;
import es.redmic.vesselsview.model.vesseltracking.VesselTracking;
import es.redmic.viewlib.geodata.repository.IGeoDataRepository;

@Repository
public class VesselTrackingESRepository extends RWGeoDataESRepository<VesselTracking, DataQueryDTO>
		implements IGeoDataRepository<VesselTracking, DataQueryDTO> {

	private static String[] INDEX = { "vessel" };
	private static String[] TYPE = { "tracking" };

	// @formatter:off
 
		private final String ID_PROPERTY = "id",
				UUID_PROPERTY = "uuid",
				MMSI_PROPERTY = "properties.vessel.mmsi.id",
				DATE_PROPERTY = "properties.date";
	// @formatter:on

	public VesselTrackingESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings("unchecked")
	public EventApplicationResult updateVesselTypeInVessel(String vesselTrackingId, Vessel vessel) {

		XContentBuilder doc;

		try {
			doc = jsonBuilder().startObject().field("type", objectMapper.convertValue(vessel, Map.class)).endObject();
		} catch (IllegalArgumentException | IOException e1) {
			LOGGER.debug("Error modificando el item con id " + vesselTrackingId + " en " + getIndex()[0] + " "
					+ getType()[0]);
			return new EventApplicationResult(ExceptionType.ES_UPDATE_DOCUMENT.toString());
		}

		return update(vesselTrackingId, doc);
	}

	@Override
	protected EventApplicationResult checkInsertConstraintsFulfilled(VesselTracking modelToIndex) {

		// @formatter:off

		QueryBuilder idTerm = QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()),
				uuidTerm = QueryBuilders.termQuery(UUID_PROPERTY, modelToIndex.getUuid());
		
		SearchRequestBuilder requestBuilderId = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(idTerm).setSize(1),
			requestBuilderUuid = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(uuidTerm).setSize(1);

		MultiSearchRequestBuilder multiSearchRequestBuilder = ESProvider.getClient().prepareMultiSearch()
			.add(requestBuilderId)
			.add(requestBuilderUuid);
		
		MultiSearchResponse sr = multiSearchRequestBuilder.get();

		// @formatter:on

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(ID_PROPERTY, modelToIndex.getId());
		}

		if (responses != null && responses[1].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(UUID_PROPERTY, modelToIndex.getUuid().toString());
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_INSERT_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkUpdateConstraintsFulfilled(VesselTracking modelToIndex) {

		// @formatter:off

		BoolQueryBuilder idTerm = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()))
				.mustNot(QueryBuilders.termQuery(UUID_PROPERTY, modelToIndex.getUuid())),
				
				mmsiTerm = QueryBuilders.boolQuery()
					.must(QueryBuilders.termQuery(MMSI_PROPERTY, modelToIndex.getProperties().getVessel().getMmsi()))
					.must(QueryBuilders.termQuery(DATE_PROPERTY, modelToIndex.getProperties().getDate()))
					.mustNot(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()));
		
		SearchRequestBuilder requestBuilderId = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(idTerm).setSize(1),
			requestBuilderMmsi = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(mmsiTerm).setSize(1);

		MultiSearchRequestBuilder multiSearchRequestBuilder = ESProvider.getClient().prepareMultiSearch()
			.add(requestBuilderId)
			.add(requestBuilderMmsi);
		
		MultiSearchResponse sr = multiSearchRequestBuilder.get();

		// @formatter:on

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(ID_PROPERTY, modelToIndex.getId().toString());
		}

		if (responses != null && responses[1].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(MMSI_PROPERTY, modelToIndex.getProperties().getVessel().getMmsi().toString());
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_UPDATE_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkDeleteConstraintsFulfilled(String modelToIndexId) {
		return new EventApplicationResult(true);
	}
}
