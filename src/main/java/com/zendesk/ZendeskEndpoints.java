package com.zendesk;

import java.io.File;
import java.io.IOException;

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

import com.google.gson.Gson;

@Path("/zendesk")
public class ZendeskEndpoints {

    @GET
    @Produces("text/plain")
    @Path("/query/password")
    // http://localhost:9998/zendesk/query/password?domain=taz&userName=taizund12@gmail.com&password=Ammar24111991
    public String getTicketsWithPassword(@QueryParam("domain") String domain, @QueryParam("userName") String userName, @QueryParam("password") String password)
            throws JSONException, IOException {
        Zendesk zd = new Zendesk.Builder("https://" + domain + ".zendesk.com").setUsername(userName).setPassword(password).build();
        return convertJSONToCSV(zd.getTickets());
    }

    @GET
    @Produces("text/plain")
    @Path("/query/token")
    // http://localhost:9998/zendesk/query/token?domain=taz&userName=taizund12@gmail.com&token=kN5jlqPWXXNc0a4a7BFImirIXg0G8VzDdMD5doHU
    // http://localhost:9998/zendesk/query/token?domain=taz&userName=taizund12@gmail.com&token=u5rQnOMdYkH3fTmpQLB4RHWVxqQkvCirQ28cXHMF
    public String getTicketsWithToken(@QueryParam("domain") String domain, @QueryParam("userName") String userName, @QueryParam("token") String token)
            throws JSONException, IOException {
        Zendesk zd = new Zendesk.Builder("https://" + domain + ".zendesk.com").setUsername(userName).setToken(token).build();
        return convertJSONToCSV(zd.getTickets());
    }

    private String convertJSONToCSV(Iterable<Ticket> tickets) throws JSONException, IOException {

        Gson gson = new Gson();

        StringBuilder builder = new StringBuilder();
        builder.append("{\"infile\": [");

        for (Ticket ticket : tickets) {
            builder.append("," + gson.toJson(ticket));
        }
        builder.append("]}");

        try {
            JSONObject output;
            output = new JSONObject(builder.toString().replaceFirst(",", ""));
            File file = new File("fromJSON.csv");
            JSONArray docs = output.getJSONArray("infile");
            System.out.println();
            System.out.println(docs.toString());
            String csv = CDL.toString(docs);
            System.out.println("csv:" + csv);
            FileUtils.writeStringToFile(file, csv);

            return "true";
        } catch (JSONException e) {
            e.printStackTrace();
            return "false";
        }

    }
}