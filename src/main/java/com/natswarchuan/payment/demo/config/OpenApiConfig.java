package com.natswarchuan.payment.demo.config;

import com.natswarchuan.payment.demo.constant.OpenApiConstant;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    final String securitySchemeName = OpenApiConstant.SECURITY_SCHEME_NAME;
    final SecurityScheme securityScheme =
        new SecurityScheme()
            .name(securitySchemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme(OpenApiConstant.SECURITY_SCHEME)
            .bearerFormat(OpenApiConstant.SECURITY_SCHEME_FORMAT)
            .in(SecurityScheme.In.HEADER);

    final Info info =
        new Info()
            .title(OpenApiConstant.INFO_TITLE)
            .version(OpenApiConstant.INFO_VERSION)
            .description(OpenApiConstant.INFO_DESCRIPTION)
            .contact(
                new Contact()
                    .name(OpenApiConstant.CONTACT_NAME)
                    .email(OpenApiConstant.CONTACT_EMAIL))
            .license(
                new License().name(OpenApiConstant.LICENSE_NAME).url(OpenApiConstant.LICENSE_URL));

    final Server localServer =
        new Server()
            .url(OpenApiConstant.SERVER_URL)
            .description(OpenApiConstant.SERVER_DESCRIPTION);

    return new OpenAPI()
        .info(info)
        .servers(List.of(localServer))
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .components(new Components().addSecuritySchemes(securitySchemeName, securityScheme));
  }
}
