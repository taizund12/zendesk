package com.zendesk.writer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zendesk.client.v2.model.Ticket;

import com.google.gson.Gson;

public final class CSVWriter {

    public static void convertJSONToCSV(Iterable<Ticket> tickets) throws JSONException, IOException {

        Gson gson = new Gson();

        StringBuilder builder = new StringBuilder();
        builder.append("{\"infile\": [");

        for (Ticket ticket : tickets) {
            String line = gson.toJson(ticket);
            int val =line.indexOf("via");
            int newVal = line.indexOf("createdAt");
            
            String subStr = line.substring(val+5, newVal-2);
            String anotherSubStrin = subStr.replaceAll(",", "  ");
            
            line = line.replace(subStr, "'"+anotherSubStrin+"'");
            System.out.println(line);
            builder.append("," + line);
        }
        builder.append("]}");
        try {
            JSONObject output;
            output = new JSONObject(builder.toString().replaceFirst(",", ""));
            File file = new File("fromJSON.csv");
            JSONArray docs = output.getJSONArray("infile");
            String csv = CDL.toString(docs);
            System.out.println("csv:" + csv);
            FileUtils.writeStringToFile(file, csv);            
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
     
}
