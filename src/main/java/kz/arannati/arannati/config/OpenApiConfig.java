package kz.arannati.arannati.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for OpenAPI 3.0 documentation (Swagger UI)
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");
//
//        Contact contact = new Contact()
//                .name("Arannati Support")
//                .email("support@arannati.kz")
//                .url("https://arannati.kz/support");
//
//        License license = new License()
//                .name("Arannati License")
//                .url("https://arannati.kz/license");

        Info info = new Info()
                .title("Arannati API Documentation")
                .version("1.0.0")
                .description("API documentation for Arannati application");
//                .contact(contact)
//                .license(license);

        // Define security scheme
//        SecurityScheme basicAuthScheme = new SecurityScheme()
//                .type(SecurityScheme.Type.HTTP)
//                .scheme("basic")
//                .description("Basic Authentication");

        // Define tags for API groups
        List<Tag> tags = Arrays.asList(
                new Tag().name("Auth").description("Authentication operations"),
                new Tag().name("Catalog").description("Product catalog operations"),
                new Tag().name("Cart").description("Shopping cart operations"),
                new Tag().name("Wishlist").description("Wishlist operations"),
                new Tag().name("Orders").description("Order management"),
                new Tag().name("Dashboard").description("User dashboard"),
                new Tag().name("Admin").description("Admin operations"),
                new Tag().name("Cosmetologist").description("Cosmetologist operations"),
                new Tag().name("Materials").description("Material management"),
                new Tag().name("Chat").description("Chat and messaging")
        );

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .tags(tags);
//                .components(new Components()
//                        .addSecuritySchemes("basicAuth", basicAuthScheme))
//                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}
