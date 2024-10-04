package org.sysc4907.votingsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class AccountGenerationRunner {

    private final AccountGenerator accountGenerator;

    public AccountGenerationRunner(AccountGenerator accountGenerator) {
        this.accountGenerator = accountGenerator;
    }

    @Bean
    public boolean run() {
        accountGenerator.generateDummyAccounts();
        return true;
    }

    public static void main(String[] args) {
        SpringApplication.run(AccountGenerationRunner.class, args);
    }

}

