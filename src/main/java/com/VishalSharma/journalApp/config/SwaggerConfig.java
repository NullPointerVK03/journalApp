package com.VishalSharma.journalApp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI get(){
        return new OpenAPI()
                .info(new Info()
                        .title("Journal Application")
                        .description("BY Vishal Sharma")
                )
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("Local"),
                        new Server().url("https://arcane-meadow-19502-48c9ab8c5a3f.herokuapp.com/").description("Live")
                ));
    }
}
