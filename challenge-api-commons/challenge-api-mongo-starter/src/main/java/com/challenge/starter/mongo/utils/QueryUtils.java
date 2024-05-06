package com.challenge.starter.mongo.utils;

import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;

public interface QueryUtils {

	Query EMPTY_QUERY = new EmptyQuery();

	class EmptyQuery extends Query {

		@SuppressWarnings("NullableProblems")
		@Override
		public Query addCriteria(CriteriaDefinition criteriaDefinition) {

			throw new UnsupportedOperationException("Empty query cannot have criteria");
		}
	}
}
