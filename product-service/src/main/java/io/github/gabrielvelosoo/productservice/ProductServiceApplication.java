package io.github.gabrielvelosoo.productservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ProductServiceApplication.class);
        builder.bannerMode(Banner.Mode.OFF);
        builder.run(args);
        ConfigurableApplicationContext context = builder.context();
        assert context != null;
        ConfigurableEnvironment environment = context.getEnvironment();
        String applicationName = environment.getProperty("spring.application.name");
        System.out.println("Service name: " + applicationName);
    }
}
