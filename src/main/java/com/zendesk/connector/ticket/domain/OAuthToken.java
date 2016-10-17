package com.zendesk.connector.ticket.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthToken {
    
    private String access_token;
    private String scope;
    private String token_type;
    /**
     * @return the access_token
     */
    public String getAccess_token() {
        return access_token;
    }
    /**
     * @param access_token the access_token to set
     */
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }
    /**
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }
    /**
     * @return the token_type
     */
    public String getToken_type() {
        return token_type;
    }
    /**
     * @param token_type the token_type to set
     */
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
    
    
    
}
