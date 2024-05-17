package clofi.runningplanet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RunningPlanetApplication {

	public static void main(String[] args) {
		SpringApplication.run(RunningPlanetApplication.class, args);
	}

}
