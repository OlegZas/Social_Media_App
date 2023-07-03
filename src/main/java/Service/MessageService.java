package Service;

import Model.Message;
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

    public Message deleteMessage(int message_id) {
        return messageDAO.deleteMessage(message_id);
    }
    public List<Message> getMessagesByUser(int account_id) {
        return messageDAO.getMessagesByUser(account_id);
    }
}
