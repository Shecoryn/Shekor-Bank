package com.shekor.ShekorBank;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class ShekorBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShekorBankApplication.class, args);
	}

}
