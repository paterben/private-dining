package paterben.privatedining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.repository.RestaurantRepository;
import paterben.privatedining.repository.RestaurantTablesRepository;

@SpringBootApplication
@EnableMongoAuditing
@OpenAPIDefinition(info = @Info(title = "Private Dining APIs", description = "The APIs for the private dining reservation system."))
public class PrivateDiningApplication implements CommandLineRunner {

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired RestaurantTablesRepository restaurantTablesRepository;

	public static void main(String[] args) {
		SpringApplication.run(PrivateDiningApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// Delete everything from the DB.
		// restaurantRepository.deleteAll();
		// restaurantTablesRepository.deleteAll();

		// // Save a couple of restaurants to the DB.
		// repository.save(new Restaurant("Bob's Pizza", "1234 Bob Ave, WA, USA", "USD"));
		// repository.save(
		// 		new Restaurant("Restaurant Vietnamien", "3 rue de Becon, 92600 Asnieres-sur-Seine, France", "EUR"));

		// Fetch all restaurants.
		System.out.println("Restaurants found with findAll():");
		System.out.println("-------------------------------");
		for (Restaurant restaurant : restaurantRepository.findAll()) {
			System.out.println(restaurant);
		}
		System.out.println();
	}
}
