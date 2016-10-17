package com.zendesk.connector.ticket.writer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zendesk.client.v2.model.Ticket;

import com.google.gson.Gson;

/**
 * Utility class to write Zendesk Ticket information to a CSV file
 */
public final class CSVWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVWriter.class);
    
    private static final String CSV_FILENAME = "TicketInfo.csv";
    private static final String COMMA_DELIMITER = ",";
    private static final String EMPTY_STRING = "";

    private CSVWriter() {
    }

    public static void writeToCSV(Iterable<Ticket> tickets, ZendeskManager zendeskManager) throws IOException {
        LOGGER.info("Writing Ticket Information to CSV file.");
        
        Gson gson = new Gson();
        StringBuilder builder = new StringBuilder();
        builder.append("{\"infile\": [");
        String subName = null;
        String reqName = null;
        for (Ticket ticket : tickets) {
            String line = gson.toJson(ticket);
            Long requestorId = ticket.getRequesterId();
            if(requestorId != null){
                if(zendeskManager.getUser(requestorId)!=null){
                    reqName = zendeskManager.getUser(requestorId).getName();   
                }
            }
            
            Long submitterId = ticket.getSubmitterId();
            if(submitterId != null){
                if(zendeskManager.getUser(submitterId)!=null){
                    subName = zendeskManager.getUser(submitterId).getName();   
                }
            }
            
            // Handle the "," in the "via" field
            int viaPosition =line.indexOf("via");
            int createdAtPosition = line.indexOf("createdAt");
            String subStr = line.substring(viaPosition + 5, createdAtPosition - 2);
            String anotherSubString = subStr.replaceAll(",", "  ");
            line = line.replace(subStr, "'" + anotherSubString + "'");
            line = line.substring(0,line.length()-1);
            line = line + ", \"submittername\" : " + "\""+subName+"\"";
            line = line + ", \"requesterName\" : " + "\""+reqName+"\"}";
            
            builder.append(COMMA_DELIMITER + line);
        }
        
        builder.append("]}");
        try {
            JSONObject output = new JSONObject(builder.toString().replaceFirst(",", EMPTY_STRING));
            File file = new File(CSV_FILENAME);
            JSONArray docs = output.getJSONArray("infile");
            String csv = CDL.toString(docs);
            FileUtils.writeStringToFile(file, csv);
        } catch (JSONException e) {
           LOGGER.error("Exception encountered while writing to CSV file", e);
        }

    }
}
