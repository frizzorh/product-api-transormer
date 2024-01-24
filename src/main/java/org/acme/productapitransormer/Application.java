package org.acme.productapitransormer;

import org.apache.camel.builder.RouteBuilder;

public class Application extends RouteBuilder {

    @Override
    public void configure() {
        from("rest:get:top-10-products")
                .to("direct:cleanupHttp")
                .to("rest:get:{{services.privateProductCatalogAPIPath}}" +
                        "?host={{services.privateProductCatalogAPIHost}}" +
                        "&queryParameters=limit=10&page=1")
                .to("jsonata:transformer.jsonata?inputType=JsonString&outputType=JsonString")
        ;

        from("direct:cleanupHttp")
                .removeHeaders("CamelHttp*")
                .removeHeaders("CamelRest*")
                .removeHeader("Host");
    }
}