package com.zendesk;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/simpleREST")
public class HelloWorld {
    @GET
    @Produces("text/plain")
    public String getMessage()
    {
        return "Message from server\n";
    }
}