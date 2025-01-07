package org.sysc4907.votingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootApplication
public class VotingSystemApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(VotingSystemApplication.class);
		app.addInitializers(new DatabaseInitializer());
		app.run(args);
	}

	public static class DatabaseInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext context) {
			Environment environment = context.getEnvironment();
			String dbName = environment.getProperty("spring.datasource.name");
			String dbUsername = environment.getProperty("spring.datasource.username");
			String dbPassword = environment.getProperty("spring.datasource.password");
			createDB(dbName, dbUsername, dbPassword);
		}

		private void createDB(String dbName, String username, String password) {
			String url = "jdbc:postgresql://localhost:5432/postgres"; // Connect to default DB
			try (Connection connection = DriverManager.getConnection(url, username, password);
				 Statement statement = connection.createStatement()) {

				String checkDbQuery = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";
				var resultSet = statement.executeQuery(checkDbQuery);

				if (!resultSet.next()) {
					// DB does not exist
					String createDbQuery = "CREATE DATABASE " + dbName;
					statement.executeUpdate(createDbQuery);
					System.out.println("Database '" + dbName + "' created successfully!");

				} else {
					System.out.println("Database '" + dbName + "' already exists.");
				}
			} catch (SQLException e) {
				throw new RuntimeException("Failed to create database: " + e.getMessage(), e);
			}
		}
	}
}