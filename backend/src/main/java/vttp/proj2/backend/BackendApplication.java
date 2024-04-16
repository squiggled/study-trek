package vttp.proj2.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import vttp.proj2.backend.services.CourseSearchService;

@SpringBootApplication
@EnableScheduling
public class BackendApplication implements CommandLineRunner{

	public static void main(String[] args) {
		
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// udemySvc.updateUdemyCoursesOnMongo();
	}

}
