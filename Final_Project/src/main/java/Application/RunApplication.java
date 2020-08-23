package Application;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Objects;

@SpringBootApplication
public class RunApplication {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(RunApplication.class, args);
	}

	@Bean
	public Driver getDriver(){
		return GraphDatabase.driver(
				env.getProperty("neo4j.url"),
				AuthTokens.basic(
						Objects.requireNonNull(env.getProperty("neo4j.username")),
						Objects.requireNonNull(env.getProperty("neo4j.password"))
				));
	}
}
