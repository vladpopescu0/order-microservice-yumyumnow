package nl.tudelft.sem.template.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Example microservice application.
 */
@SpringBootApplication
@EntityScan(basePackages = "nl.tudelft.sem.template.model")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    //comment
}
