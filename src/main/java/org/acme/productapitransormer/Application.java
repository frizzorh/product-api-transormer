package org.acme.productapitransormer;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static org.apache.camel.component.rest.RestConstants.REST_HTTP_QUERY;

@ApplicationScoped
public class Application extends RouteBuilder {

    @ConfigProperty(name = "services.privateProductCatalogAPIHost")
    String privateProductCatalogAPIHost;

    @ConfigProperty(name = "services.privateProductCatalogAPIPath")
    String privateProductCatalogAPIPath;

    @Override
    public void configure() {
        from("rest:get:top-10-products")
                .to("direct:cleanupHttp")
                .setHeader(REST_HTTP_QUERY, () -> "limit=10&page=1")
                .toF("rest:get:%s?host=%s", privateProductCatalogAPIPath, privateProductCatalogAPIHost)
                .to("jsonata:transformer.jsonata?inputType=JsonString&outputType=JsonString")
        ;

        from("direct:cleanupHttp")
                .removeHeaders("CamelHttp*")
                .removeHeaders("CamelRest*")
                .removeHeader("Host");
    }
}