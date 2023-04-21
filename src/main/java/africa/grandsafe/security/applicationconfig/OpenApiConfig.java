package africa.grandsafe.security.applicationconfig;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition
@Configuration
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
    Contact lekan = new Contact()
            .name("Lekan Sofuyi")
            .email("lekan.sofuyi@gmail.com")
            .url("https://github.com/ola-lekan01/grandsafe");


    @Bean
    public OpenAPI configAPI(){
        return new OpenAPI().info(new Info()
                .title("GrandSafe Wallet")
                .version("Version 1.00")
                .description("The perfect wallet for your everyday savings")
                .contact(lekan)
                .termsOfService("An online digital wallet !!! "));
    }
}
