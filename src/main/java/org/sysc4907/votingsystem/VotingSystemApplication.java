package org.sysc4907.votingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootApplication
public class VotingSystemApplication {
	public static void main(String[] args) {
		// Create accounts_db if it does not already exist
		createDB("accounts_db", "postgres", "postgres");
		SpringApplication.run(VotingSystemApplication.class, args);
	}


	/**
	 * Creates accounts_db with default username/password if it does not exist.
	 *
	 * @param dbName accounts_db
	 * @param username postgres
	 * @param password postgres
	 */
	private static void createDB(String dbName, String username, String password) {
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
			e.printStackTrace();
		}
	}

}
