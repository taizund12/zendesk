package com.zendesk;

import static com.zendesk.writer.CSVWriter.convertJSONToCSV;

import java.io.IOException;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zendesk.client.v2.Zendesk;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.zendesk.domain.OAuthToken;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final String GRANT_TYPE_PASSWORD = "password";

    private ObjectMapper jacksonMapper = new ObjectMapper();
    
    @Parameter(names={"--userName", "-u"}, description = "User name")
    private String userName;

    @Parameter(names = {"--password", "-p"}, description = "User Password")
    private String password;

    @Parameter(names = {"--domain", "-d"}, description = "User Password")
    private String zendeskDomain;

    @Parameter(names = {"--apiKey", "-k"}, description = "OAuth API Key")
    private String apiKey;

    @Parameter(names = {"--clientId", "-ci"}, description = "Client Id")
    private String clientId;

    @Parameter(names = {"--clientSecret", "-cs"}, description = "Client Secret")
    private String clientSecret;
    
    public static void main(String[] args) throws Exception {
        Application application = new Application();
        JCommander jCommander = new JCommander(application, args);
        String oAuthToken = application.buildHTTPRequestForPasswordGrantType();
        application.getTicketsWithToken(oAuthToken);
    }

    /**
     * Populate the CSV file with the ticket information obtained from Zendesk.
     * @param oAuthToken the OAuth token used to call the API
     * @throws JSONException
     * @throws IOException
     */
    public void getTicketsWithToken(String oAuthToken) throws JSONException, IOException {
        // Auto closeable resource
        try(Zendesk zd = new Zendesk.Builder("https://" + zendeskDomain + ".zendesk.com")
                .setUsername(userName)
                .setOauthToken(oAuthToken)
                .build()) {
            
        convertJSONToCSV(zd.getTickets());
        }
    }


    /***
     * Password grant type OAuth
     * @throws Exception
     * @return The OAuth token
     */
    public String buildHTTPRequestForPasswordGrantType() {
        OAuthToken oAuthToken = null;
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://" + zendeskDomain + ".zendesk.com/oauth/tokens")
                    .header("Content-Type", "application/json")
                    .queryString("grant_type", GRANT_TYPE_PASSWORD)
                    .queryString("client_id", clientId)
                    .queryString("client_secret", clientSecret)
                    .queryString("username", userName)
                    .queryString("password", password)
                    .queryString("scope", "read")
                    .asJson();

            oAuthToken = jacksonMapper.readValue(jsonResponse.getBody().toString(), OAuthToken.class);
        } catch (UnirestException | IOException e) {
            LOGGER.error("Exception encountered calling the Zendesk API.", e);
        }
        return oAuthToken.getAccess_token();
    }
}
