package Service;

import Model.Message;

import java.sql.SQLException;
import java.util.List;

import DAO.MessageDAO;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    // public void addMessage(Message message) {
    //     messageDAO.addMessage(message);
    // }

    public Message createMessage(int posted_by, String message_text, long time_posted_epoch) {
        
/*If condition is not met, then message is valid, and the method proceeds to call the 
createMessage method of the messageDAO object to actually create the message in the database. */
        if (message_text.isBlank()) {
            return null; 
            }

            if (message_text.length() > 254) {
                return null; 
                }

        return messageDAO.createMessage(posted_by, message_text, time_posted_epoch);
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int message_id) {
        return messageDAO.getMessageById(message_id);
    }

    public boolean userExists(int user_id) {
        return messageDAO.userExists(user_id);
    }

    public Message deleteMessage(int message_id) throws SQLException { // method takes message_id param and returns MEssage object 
        return messageDAO.deleteMessage(message_id); // calling delete mes from dao (where it will be deleted) and returning Message object representing the body of deleted message  
    }
    public List<Message> getMessagesByUser(int account_id) {/*method returns a List of Message objects and takes an int parameter account_id, 
        which represents the user account ID for which we want to retrieve the messages. */
        return messageDAO.getMessagesByUser(account_id);/*calling messageDAO object and passing account_id as an argument */
    }

    // public boolean messageExists(int message_id) {
    //     return messageDAO.messageExists(message_id);
    // }
    // public boolean messageExists(int message_id) {
    //     return messageDAO.messageExists(message_id);
    // }

    public Message updateMessageText(int message_id, String newMessageText) throws SQLException {
        // Check if the message exists
        Message existingMessage = getMessageById(message_id);
        if (existingMessage == null) {
            return null;
        }

        // Check if the new message text is not blank and does not exceed 255 characters
        if (newMessageText == null || newMessageText.isBlank() || newMessageText.length() > 255) {
            return null;
        } else {

        // Update the message text through the DAO
        return messageDAO.updateMessageText(message_id, newMessageText);}
    }


}
