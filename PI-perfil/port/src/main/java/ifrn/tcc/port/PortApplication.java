package ifrn.tcc.port;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class PortApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("123"));
	}

}
