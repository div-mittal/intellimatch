package com.divyanshu.Intellimatch;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IntellimatchApplication {

	static {
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory(".")
					.ignoreIfMalformed()
					.ignoreIfMissing()
					.load();

			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
			});

			System.out.println("✅ Environment variables loaded from .env file");
		} catch (Exception e) {
			System.out.println("⚠️ Could not load .env file: " + e.getMessage());
			System.out.println("📝 Using system environment variables as fallback");
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(IntellimatchApplication.class, args);
	}
}
