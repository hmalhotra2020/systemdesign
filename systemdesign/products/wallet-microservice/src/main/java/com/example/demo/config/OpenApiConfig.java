package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI walletOpenApi() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Digital Wallet API")
                        .version("1.0.0")
                        .description("API documentation for a digital wallet service with customers, accounts, ledger entries, transactions, KYC, credit assessments, and idempotency intents.")
                        .termsOfService("https://example.com/terms")
                        .contact(new Contact().name("Digital Wallet Team").email("support@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
