package Controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
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
        Javalin app = Javalin.create(); //Here, a new instance of Javalin server is created using the create() method.
        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::postAccountHandler); // register endpoint from the test case
        app.post("/login", this::loginHandler); // Add the login endpoint 
        app.post("/messages", this::postMessageHandler); // Add the postMessage endpoint
        app.delete("/messages/{message_id}", this::deleteMessageHandler); // Add the deleteMessage endpoint
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserHandler); // Add the getMessagesByUser endpoint
        return app;
    }
    private void postAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account addedAccount = accountService.registerUser(account.getUsername(), account.getPassword()); 
        if(addedAccount!=null){
            ctx.json(mapper.writeValueAsString(addedAccount));
            ctx.status(200);
        }else{
            ctx.status(400);
        }
    }
    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }
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
//delete message handler 
private void deleteMessageHandler(Context ctx) throws JsonProcessingException {
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
private void getMessagesByUserHandler(Context ctx) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    int accountId = Integer.parseInt(ctx.pathParam("account_id"));
    List<Message> messages = messageService.getMessagesByUser(accountId);
    ctx.json(mapper.writeValueAsString(messages));
    ctx.status(200);
}

}