package com.stakater.lab.moviesservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import java.lang.invoke.MethodHandles;

@SpringBootApplication
@EnableNeo4jRepositories(basePackages = "com.stakater.lab.moviesservice.repository")
public class MovieServiceApplication {

	// Take advantage of a Java 8 feature to create loggers like this, to prevents copy-paste errors!
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) {
		LOGGER.info("Starting application ...");
		SpringApplication.run(MovieServiceApplication.class, args);
	}

}
