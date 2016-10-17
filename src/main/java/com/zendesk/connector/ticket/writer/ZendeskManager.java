package com.zendesk.connector.ticket.writer;

import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.model.User;

public interface ZendeskManager {

    public User getUser(long id);
    public Zendesk getZendeskClient();
    
}
