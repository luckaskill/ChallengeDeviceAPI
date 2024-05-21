package com.challenge.starter.mongo.utils;

import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;

public interface QueryUtils {

	Query EMPTY_QUERY = new EmptyQuery();

	static Query byId(Object id) {

		return new Query().addCriteria(CriteriaUtils.byId(id));
	}

	class EmptyQuery extends Query {

		@SuppressWarnings("NullableProblems")
		@Override
		public Query addCriteria(CriteriaDefinition criteriaDefinition) {

			throw new UnsupportedOperationException("Empty query cannot have criteria");
		}
	}
}
