package com.gymcrm.controller;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.jaxrs2.Reader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

@Controller
@Hidden
public class SwaggerController {

    private static final Set<Class<?>> CONTROLLER_CLASSES = Set.of(
            TraineeController.class,
            TrainerController.class,
            AuthController.class,
            TrainingController.class,
            TrainingTypeController.class
    );

    @GetMapping(value = "/v3/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getOpenApiSpec() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Gym CRM REST API")
                        .version("1.0")
                        .description("REST API for Gym Customer Relationship Management System")
                        .contact(new Contact()
                                .name("Gym CRM Team")
                                .email("support@gymcrm.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ));

        Reader reader = new Reader(openAPI);
        openAPI = reader.read(CONTROLLER_CLASSES);

        return Json.pretty(openAPI);
    }

    @GetMapping("/swagger-ui.html")
    public String redirectToSwaggerUI() {
        return "redirect:/swagger-ui/index.html";
    }

    @GetMapping("/swagger-ui/index.html")
    @ResponseBody
    public String swaggerUIPage() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Gym CRM API - Swagger UI</title>
                <link rel="stylesheet" type="text/css" href="/swagger-ui/swagger-ui.css" />
                <link rel="icon" type="image/png" href="/swagger-ui/favicon-32x32.png" sizes="32x32" />
                <style>
                    html { box-sizing: border-box; overflow-y: scroll; }
                    *, *:before, *:after { box-sizing: inherit; }
                    body { margin: 0; background: #fafafa; }
                </style>
            </head>
            <body>
                <div id="swagger-ui"></div>
                <script src="/swagger-ui/swagger-ui-bundle.js" charset="UTF-8"></script>
                <script src="/swagger-ui/swagger-ui-standalone-preset.js" charset="UTF-8"></script>
                <script>
                    window.onload = function() {
                        window.ui = SwaggerUIBundle({
                            url: "/v3/api-docs",
                            dom_id: '#swagger-ui',
                            deepLinking: true,
                            presets: [
                                SwaggerUIBundle.presets.apis,
                                SwaggerUIStandalonePreset
                            ],
                            plugins: [
                                SwaggerUIBundle.plugins.DownloadUrl
                            ],
                            layout: "StandaloneLayout"
                        });
                    };
                </script>
            </body>
            </html>
            """;
    }
}

