package paterben.privatedining;

import java.time.Clock;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Generic configuration class.
 */
@Configuration
public class AppConfig {
    // This model mapper is used to convert between core and API model
    // representations.
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
