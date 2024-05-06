package com.challenge.starter.mongo;

import com.challenge.general.utils.StringUtils;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConditionalOnProperty(prefix = "challenge.data.mongodb.liquibase", name = "enabled", havingValue = "true")
public class MongoLiquibaseAutoConfiguration {

	@Value("${spring.data.mongodb.uri}")
	private String uri;

	@Value("${spring.data.mongodb.database:}")
	private String database;

	@Value("${spring.data.mongodb.username:}")
	private String username;

	@Value("${spring.data.mongodb.password:}")
	private String password;

	@Value("${challenge.data.mongodb.liquibase.changelog}")
	private String changelog;

	@SneakyThrows
	@Bean
	public Liquibase liquibase() {

		String fullURI = getFullURI();
		MongoLiquibaseDatabase database = (MongoLiquibaseDatabase) DatabaseFactory.getInstance()
				.openDatabase(fullURI, username, password, null, null);
		Liquibase liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);

		//non-deprecated method is not working
		//noinspection deprecation
		liquibase.update("");
		return liquibase;
	}

	private String getFullURI() {

		if (StringUtils.nullOrEmpty(database) || uri.endsWith(database)) {
			return uri;
		}
		if (uri.endsWith("/")) {
			return uri + database;
		}
		return uri + "/" + database;
	}
}
