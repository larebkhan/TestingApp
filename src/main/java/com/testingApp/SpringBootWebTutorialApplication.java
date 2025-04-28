package com.testingApp;

import com.testingApp.services.DataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringBootWebTutorialApplication implements CommandLineRunner {

	//private final DataService dataService;



	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebTutorialApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		//System.out.println("Data is : "+ dataService.getData());
	}
}
