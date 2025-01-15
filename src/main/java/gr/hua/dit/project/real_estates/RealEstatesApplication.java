package gr.hua.dit.project.real_estates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RealEstatesApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealEstatesApplication.class, args);
	}

}
