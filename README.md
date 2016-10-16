## Zendesk Ticket Fetcher Connector

### Prerequisites

Make sure that you have Java 1.8 installed on your system.

### Running the application
 
 The application is an executable jar which can be run as follows:
##### 1. Go to the directory where the ticket-connector-*.jar is
##### 2. Run using the following command:
``` java
java -jar ticket-connector-0.0.1-SNAPSHOT.jar -u <user_name> -p <password> -ci <client_Id> -cs <client_secret> -d <zendesk_domain>
```
Substitute the actual values for <i>user_name</i>, <i>password</i> etc. in the above command.
##### 3. After succesful execution, a CSV file named TicketInfo.csv will be generated in the same directory where the application is run.

### Working

The application first connects to Zendesk using the user credentials and the Client ID and Client Secret provided via the command line.  
<br/>
It then obtains an OAuth token using the Password Grant Type flow as described in https://support.zendesk.com/hc/en-us/articles/203663836-Using-OAuth-authentication-with-your-application  
<br/>
It calls the Zendesk API using this OAuth token and gets the relevant ticket information and then writes it into a CSV file.

 ___

#### Technology stack

 * The application is a standalone, executable `jar` and has been primarily developed using Java.
 * Zendesk Java client to connect to Zendesk.
 * Unirest Java library to call the Zendesk API.
 * JCommander to parse the command line arguments.
