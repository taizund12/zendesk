package com.zendesk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.model.Ticket;

@Path("/zendesk")
public class ZendeskEndpoints {

    @GET
    @Produces("text/plain")
    @Path("/query/password")
    // http://localhost:9998/zendesk/query/password?domain=taz&userName=taizund12@gmail.com&password=Ammar24111991
    public String getTicketsWithPassword(@QueryParam("domain") String domain, @QueryParam("userName") String userName,
            @QueryParam("password") String password) throws JSONException {
        Zendesk zd = new Zendesk.Builder("https://" + domain + ".zendesk.com").setUsername(userName).setPassword(password).build();
        return convertJSONToCSV(zd.getTickets());
    }

    @GET
    @Produces("text/plain")
    @Path("/query/token")
    // http://localhost:9998/zendesk/query/token?domain=taz&userName=taizund12@gmail.com&token=kN5jlqPWXXNc0a4a7BFImirIXg0G8VzDdMD5doHU
    public String getTicketsWithToken(@QueryParam("domain") String domain, @QueryParam("userName") String userName, @QueryParam("token") String token)
            throws JSONException {
        Zendesk zd = new Zendesk.Builder("https://" + domain + ".zendesk.com").setUsername(userName).setToken(token).build();
        return convertJSONToCSV(zd.getTickets());
    }

    private String convertJSONToCSV(Iterable<Ticket> tickets) throws JSONException {
        
        List<String> list = new ArrayList<>();
        
        for(Ticket ticket : tickets){
            list.add(ticket.toString());
        }
        System.out.println(list);
            return list.toString();

    }

}