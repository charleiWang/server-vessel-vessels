package es.redmic.vesselsview.repository;

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

import es.redmic.elasticsearchlib.data.repository.RWDataESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.MetadataQueryDTO;
import es.redmic.vesselsview.model.Vessel;
import es.redmic.vesselsview.model.VesselType;
import es.redmic.viewlib.data.repository.IDataRepository;

@Repository
public class VesselESRepository extends RWDataESRepository<Vessel, MetadataQueryDTO>
		implements IDataRepository<Vessel, MetadataQueryDTO> {

	private static String[] INDEX = { "platform" };
	private static String[] TYPE = { "vessel" };

	// @formatter:off
 
		private final String ID_PROPERTY = "id",
				MMSI_PROPERTY = "mmsi",
				IMO_PROPERTY = "imo";
	// @formatter:on

	public VesselESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings("unchecked")
	public EventApplicationResult updateVesselTypeInVessel(String vesselId, VesselType vesselType) {

		XContentBuilder doc;

		try {
			doc = jsonBuilder().startObject().field("type", objectMapper.convertValue(vesselType, Map.class))
					.endObject();
		} catch (IllegalArgumentException | IOException e1) {
			LOGGER.debug("Error modificando el item con id " + vesselId + " en " + getIndex()[0] + " " + getType()[0]);
			return new EventApplicationResult(ExceptionType.ES_UPDATE_DOCUMENT.toString());
		}

		return update(vesselId, doc);
	}

	@Override
	protected EventApplicationResult checkInsertConstraintsFulfilled(Vessel modelToIndex) {

		// @formatter:off

		QueryBuilder idTerm = QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()),
				mmsiTerm = QueryBuilders.termQuery(MMSI_PROPERTY, modelToIndex.getMmsi()),
				imoTerm = null;
		
		if (modelToIndex.getImo() != null) {
			imoTerm = QueryBuilders.termQuery(IMO_PROPERTY, modelToIndex.getImo());
		}
		
		SearchRequestBuilder requestBuilderId = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(idTerm).setSize(1),
			requestBuilderMmsi = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(mmsiTerm).setSize(1),
			requestBuilderImo = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(imoTerm).setSize(1);

		MultiSearchRequestBuilder multiSearchRequestBuilder = ESProvider.getClient().prepareMultiSearch()
			.add(requestBuilderId)
			.add(requestBuilderMmsi);

		if (imoTerm != null)
			multiSearchRequestBuilder.add(requestBuilderImo);
		
		MultiSearchResponse sr = multiSearchRequestBuilder.get();

		// @formatter:on

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(ID_PROPERTY, modelToIndex.getId());
		}

		if (responses != null && responses[1].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(MMSI_PROPERTY, modelToIndex.getMmsi().toString());
		}

		if (imoTerm != null && responses != null && responses[2].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(IMO_PROPERTY, modelToIndex.getImo().toString());
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_INSERT_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkUpdateConstraintsFulfilled(Vessel modelToIndex) {

		// @formatter:off

		BoolQueryBuilder mmsiTerm = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery(MMSI_PROPERTY, modelToIndex.getMmsi()))
				.mustNot(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId())),
			imoTerm = null;
		
		if (modelToIndex.getImo() != null) {
			imoTerm = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery(IMO_PROPERTY, modelToIndex.getImo()))
				.mustNot(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()));
		}
		
		SearchRequestBuilder requestBuilderMmsi = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(mmsiTerm).setSize(1),
				requestBuilderImo = null;
		
		if (imoTerm != null) {
			requestBuilderImo = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(imoTerm).setSize(1);
		}

		MultiSearchRequestBuilder multiSearchRequestBuilder = ESProvider.getClient().prepareMultiSearch()
			.add(requestBuilderMmsi);
		
		if (requestBuilderImo != null) {
			multiSearchRequestBuilder.add(requestBuilderImo);
		}
		
		MultiSearchResponse sr = multiSearchRequestBuilder.get();

		// @formatter:on

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(MMSI_PROPERTY, modelToIndex.getMmsi().toString());
		}

		if (requestBuilderImo != null && responses != null && responses[1].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(IMO_PROPERTY, modelToIndex.getImo().toString());
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
