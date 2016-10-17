package com.zendesk.connector.ticket;

import static com.zendesk.connector.ticket.writer.CSVWriter.writeToCSV;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.model.User;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.zendesk.connector.ticket.domain.OAuthToken;
import com.zendesk.connector.ticket.writer.ZendeskManager;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final String GRANT_TYPE_PASSWORD = "password";
    private ZendeskManager zendeskManager;

    private ObjectMapper jacksonMapper = new ObjectMapper();
    
    @Parameter(names={"--userName", "-u"}, description = "User Name", required = true)
    private String userName;

    @Parameter(names = {"--password", "-p"}, description = "User Password", required = true)
    private String password;

    @Parameter(names = {"--domain", "-d"}, description = "User Domain", required = true)
    private String zendeskDomain;

    @Parameter(names = {"--apiKey", "-k"}, description = "API Key")
    private String apiKey;

    @Parameter(names = {"--clientId", "-ci"}, description = "Client Id", required = true)
    private String clientId;

    @Parameter(names = {"--clientSecret", "-cs"}, description = "Client Secret", required = true)
    private String clientSecret;
    
    public static void main(String[] args) throws Exception {
        Application application = new Application();
        
        JCommander jCommander = new JCommander(application, args);
        Optional<OAuthToken> oAuthTokenOptional = application.getOAuthTokenPasswordGrantType();
        application.getTicketsWithToken(oAuthTokenOptional);
    }

    /**
     * Obtain ticket information from Zendesk and populate it into a CSV file.
     * 
     * @param oAuthTokenOptional the OAuth token used to call the API, if present.
     * @throws IOException
     */
    public void getTicketsWithToken(Optional<OAuthToken> oAuthTokenOptional) throws IOException {
        if(oAuthTokenOptional.isPresent()) {
            String token = oAuthTokenOptional.get().getAccess_token();
            
            // Auto closeable resource
            try(Zendesk zd = new Zendesk.Builder(String.format("https://%s.zendesk.com", zendeskDomain))
                    .setUsername(userName)
                    .setOauthToken(token)
                    .build()) {
                zendeskManager = new ZendeskManager() {
                    
                    @Override
                    public User getUser(long id) {
                        return zd.getUser(id);
                    }

                    @Override
                    public Zendesk getZendeskClient() {
                        return zd;
                    }
                };
                writeToCSV(zd.getTickets(), zendeskManager);
            }
        } else {
            LOGGER.error("Could not generate the OAuth token for clientId = {}", clientId);
        }
    }


    /***
     * Password Grant Type OAuth flow
     * 
     * @return the OAuth token
     */
    public Optional<OAuthToken> getOAuthTokenPasswordGrantType() {
        LOGGER.info("Connecting to Zendesk to get OAuth token");
        OAuthToken oAuthToken = null;
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(String.format("https://%s.zendesk.com/oauth/tokens", zendeskDomain))
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
        return Optional.ofNullable(oAuthToken);
    }
}
