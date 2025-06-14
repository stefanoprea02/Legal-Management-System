package ord.example.LegalManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class LegalManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LegalManagementSystemApplication.class, args);
	}

}
