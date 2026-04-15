package io.github.gabrielvelosoo.apigateway;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ApiGatewayApplication.class);
        builder.bannerMode(Banner.Mode.OFF);
        builder.run(args);
        ConfigurableApplicationContext context = builder.context();
        assert context != null;
        ConfigurableEnvironment environment = context.getEnvironment();
        String applicationName = environment.getProperty("spring.application.name");
        System.out.println("Application name: " + applicationName);
	}

}
