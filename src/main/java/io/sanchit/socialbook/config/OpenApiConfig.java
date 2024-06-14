package io.sanchit.socialbook.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "sanchit.io",
                        email = "contactme@sanchit.io",
                        url = "https://www.sanchit.io"
                ),
                description = "OpenAPI docs for socialbooks",
                title = "OpenAPI specs",
                version = "1.0",
                license = @License(
                        name = "MyLisence",
                        url = "https://mylicense.url.org"
                ),
                termsOfService = "Terms of Service"
        ),
        servers = {
                @Server(
                        description = "Local ENV ",
                        url = "http://localhost:8088/api/v1"),
                @Server(
                        description = "DEV ENV ",
                        url = "http://dev.socialbook.com/api/v1"),

                @Server(
                        description = "PRD ENV ",
                        url = "http://socialbook.com/api/v1")
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
