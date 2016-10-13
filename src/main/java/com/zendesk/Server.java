package com.zendesk;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.model.Ticket;

import com.google.gson.Gson;

public class Server {

    @SuppressWarnings("null")
    public static void main(String[] args) throws IOException, JSONException {
        if (args.length < 4) {
            System.out.println("usage: <domain> <username> <password> <oAuthToken>");
            System.out.println("to escape username or oAuthToken pass null as the value");
            System.exit(1);
        }
        String domain = args[0];
        String username = args[1];
        String password = args[2];
        String oAuthToken = args[3];
        Zendesk zd = null;
        if (password != null && !password.equals("null") && !password.equals("")) {
            zd = new Zendesk.Builder("https://" + domain + ".zendesk.com").setUsername(username).setPassword(password).build();

        } else if (oAuthToken != null && !oAuthToken.equals("null") && !oAuthToken.equals("")) {
            zd = new Zendesk.Builder("https://" + domain + ".zendesk.com").setUsername(username).setToken(oAuthToken).build();
        } else {
            System.out.println("FAILED!");
            System.exit(0);
        }
        if (convertJSONToCSV(zd.getTickets())) {
            System.out.println("SUCCESS: look for a file called fromJSON.csv in your project");
        }
        System.out.println("FAILED!");
        System.exit(0);
    }

    private static boolean convertJSONToCSV(Iterable<Ticket> tickets) throws JSONException, IOException {

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
            String csv = CDL.toString(docs);
            FileUtils.writeStringToFile(file, csv);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
