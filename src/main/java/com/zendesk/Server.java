package com.zendesk;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
/**
 * Hello world!
 *
 */
public class Server 
{
    public static final URI BASE_URI = UriBuilder.fromUri("http://localhost").port(9998).build();
    public static void main(String[] args) throws IOException {
        System.out.println("Starting grizzly...");
        ResourceConfig rc = new PackagesResourceConfig("com.zendesk");
        HttpServer myServer = GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
        System.out.println(String.format("Jersey app started with WADL available at %s/application.wadl\n" +
                "Try out %s/simpleREST\nHit enter to stop it...", BASE_URI, BASE_URI));
        System.in.read();
        myServer.stop();
    }
}
