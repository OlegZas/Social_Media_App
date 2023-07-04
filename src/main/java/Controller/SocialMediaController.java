package Controller;


import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
/** This class is responsible for creating a Javalin controller with endpoints and defining handler methods to handle HTTP requests.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

     /*creating instances of AccountService and MessageService so we can interact with these services */
     AccountService accountService;
     MessageService messageService; 

    /* The constructor LibraryController() initializes the bookService
      and authorService fields by creating new instances of the BookService and AuthorService classes.*/
      public SocialMediaController (){
        this.accountService = new AccountService(); 
        this.messageService = new MessageService();
      }
    public Javalin startAPI() {
        Javalin app = Javalin.create(); //a new instance of Javalin server is created using the create() method.
       
        app.post("/register", this::postAccountHandler); // registering endpoint from the test case
        app.post("/login", this::loginHandler); // Adding the login endpoint 
        app.post("/messages", this::postMessageHandler); // Adding the postMessage endpoint
        app.delete("/messages/{message_id}", this::deleteMessageHandler); // Adding the deleteMessage endpoint
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserHandler); // Adding the getMessagesByUser endpoint
        app.get("/messages", this::getAllMessagesHandler); // Adding the getAllMessages endpoint
        app.get("/messages/{message_id}", this::getMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler); // Adding the updateMessage endpoint
        return app;
    }
    
    private void updateMessageHandler(Context ctx) throws JsonProcessingException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        JsonNode requestBody = mapper.readTree(ctx.body());
    
        if (!requestBody.has("message_text")) {
            ctx.status(400);
            return;
        }
    
        String newMessageText = requestBody.get("message_text").asText();
        if (newMessageText.isBlank() || newMessageText.length() > 254) {
            ctx.status(400);
            return;
        }
    
        Message updatedMessage = messageService.updateMessageText(messageId, newMessageText);
    
        if (updatedMessage != null) {
            ctx.json(mapper.writeValueAsString(updatedMessage));
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }
    
    /*************************************CREATING ACCOUNT ************************************************** */
    private void postAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(); // creating object mapper instance (from Jackson libary) to convert Java objects to JSON*/
        Account account = mapper.readValue(ctx.body(), Account.class); /*using mapper and readValue() to read JSON data from ctx.body, and convert into Account (Java) object.
         */
        Account addedAccount = accountService.registerUser(account.getUsername(), account.getPassword()); /* calling registerUser from service and passing 
        deserialized (conv from JASON to Java) username and password  */
        if(addedAccount!=null){ // if account not null means registration was successful 
            ctx.json(mapper.writeValueAsString(addedAccount));
            ctx.status(200);
        }else{ // registration failed 
            ctx.status(400);
        }
    }


    /***************************************LOGIN ****************************************************** */
    private void loginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account loggedInAccount = accountService.loginUser(account.getUsername(), account.getPassword());
        if (loggedInAccount != null) {
            ctx.json(mapper.writeValueAsString(loggedInAccount));
            ctx.status(200);
        } else {
            ctx.status(401); // Unauthorized
        }


    }

    /*************************************POSTING A MESSAGE ************************************************** */
    private void postMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        if (messageService.userExists(message.getPosted_by())) {
            Message addedMessage = messageService.createMessage(
                message.getPosted_by(),
                message.getMessage_text(),
                message.getTime_posted_epoch()
            );
            if (addedMessage != null) {
                ctx.json(mapper.writeValueAsString(addedMessage));
                ctx.status(200);
            } else {
                ctx.status(400);
            }
        } else {
            ctx.status(400);
        }
    }
/**************************************DELETING A MESSAGE ************************************************** 
 * @throws SQLException*/
private void deleteMessageHandler(Context ctx) throws JsonProcessingException, SQLException {
    ObjectMapper mapper = new ObjectMapper();
    int messageId = Integer.parseInt(ctx.pathParam("message_id"));
    Message deletedMessage = messageService.deleteMessage(messageId);
    if (deletedMessage != null) {
      // ctx.json(deletedMessage);
         ctx.json(mapper.writeValueAsString(deletedMessage));
        ctx.status(200);
    } else {
        ctx.status(200); // Setting an empty response body when the message is not found
    } 
}

/************************************GET MESSAGE FOR SPECIFIC USER ******************************************* */
private void getMessagesByUserHandler(Context ctx) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();// creating instance of object mapper to deserialize Java objects to jason 
    int accountId = Integer.parseInt(ctx.pathParam("account_id"));// retreives account id from the requested path parameters 
    List<Message> messages = messageService.getMessagesByUser(accountId);// calling getMessageByUser() from service and passing accountId as parameter 
    ctx.json(mapper.writeValueAsString(messages));
    ctx.status(200);
}
/********************************************RETRIEVE ALL MESSAGES ******************************************** */
private void getAllMessagesHandler(Context ctx) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    List<Message> messages = messageService.getAllMessages();
    
    if (messages != null && !messages.isEmpty()) {
        ctx.json(mapper.writeValueAsString(messages));
        ctx.status(200);
    } else {
        ctx.json("[]"); // Return an empty list as JSON representation
        ctx.status(200);
    }
}
/*************************************RETRIEVE A MESSAGE BY MESSAGE ID ********************************* */
private void getMessageHandler(Context ctx) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    int messageId = Integer.parseInt(ctx.pathParam("message_id"));
    Message message = messageService.getMessageById(messageId);
    if (message != null) {
        ctx.json(mapper.writeValueAsString(message));
        ctx.status(200);
    } else {
        
        ctx.status(200); // Setting an empty response body when the message is not found
    }
}

}




