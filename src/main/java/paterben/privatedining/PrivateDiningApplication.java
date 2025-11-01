package paterben.privatedining;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Main application class.
 */
@SpringBootApplication
@EnableMongoAuditing
@OpenAPIDefinition(info = @Info(title = "Private Dining APIs", description = "The APIs for the private dining reservation system."))
public class PrivateDiningApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrivateDiningApplication.class, args);
	}
}
