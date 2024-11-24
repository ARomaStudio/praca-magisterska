package pracamagisterska.s32237.libclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pracamagisterska.s32237.libproducer.SharedClass;

@SpringBootApplication
public class LibraryClientApplication {

	public static void main(String[] args) {
		SharedClass sharedClass = new SharedClass();
		SpringApplication.run(LibraryClientApplication.class, args);
	}

}
