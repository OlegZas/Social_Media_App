package DAO;
import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private Connection connection;

    public MessageDAO() {
        this.connection = ConnectionUtil.getConnection();
    }
/*****************************DELETING A MESSAGE  ***********************************************/
    public Message deleteMessage(int message_id) throws SQLException { //declaring a method that returns Message object and takes in message_id parameter 
        // Check if the message exists
        PreparedStatement stmt = connection.prepareStatement( /* creatoomg a prepared stmt object   */
                         "DELETE FROM message WHERE message_id = ?"); // deleting a row where message-id matches
                 stmt.setInt(1, message_id); // setting placeholder to message-id
        Message message = getMessageById(message_id);
      if (message == null) {
        // Message not found, return an empty response
        return null;
      }else {
        return getMessageById(message_id);
   
      }}
    //     return null; // if no rows were deleted it will return null 
 /***************************************OLD: gave an error - deleting a message********************* */   
    // public Message deleteMessage(int message_id) { //declaring a method that returns Message object and takes in message_id parameter
    //     try {
    //         PreparedStatement stmt = connection.prepareStatement( /* creatoomg a prepared stmt object   */
    //                 "DELETE FROM message WHERE message_id = ?"); // deleting a row where message-id matches
    //         stmt.setInt(1, message_id); // setting placeholder to message-id
    //         int rowsAffected = stmt.executeUpdate(); // executing stmt - it returns rows affected intgr and stores in rowsAffected*/
    //         if (rowsAffected > 0) { // checks if rows affected  > 0
    //             return getMessageById(message_id); // calling getmesbyid and returning the deleted message */
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();// info about the exception that occured
    //     }
    //     return null; // if no rows were deleted it will return null
    // }


/********************************************CREATING A MESSAGE ***************************************** */
    public Message createMessage(int posted_by, String message_text, long time_posted_epoch) {
        try {
            // //adding validation to check if the message is not blank before executing sql statement (adding message)
    if (message_text.isBlank()) { //method check if string is blank 
        throw new IllegalArgumentException("You did not provide a username bonita!");}

        // //adding validation to check if the message longer than 255 characters 
    if (message_text.length()> 254) { //method check if message is longer than 255  
        throw new IllegalArgumentException("Bad bonita, message should be longer than 255 characters!");}

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, posted_by);
            statement.setString(2, message_text);
            statement.setLong(3, time_posted_epoch);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int messageId = generatedKeys.getInt(1);
                return new Message(messageId, posted_by, message_text, time_posted_epoch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
/*********************************GET ALL MESSAGES ************************************************ */
    public List<Message> getAllMessages() { // creating an empty list to store retreived values */
        List<Message> messages = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement(); // creatung stmt object to execute slq*/
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM message"); /* executing sql to retreive all msgs */
            while (resultSet.next()) {// iterating over each orw in the resultSet obj*/
                int messageId = resultSet.getInt("message_id");/*extracting values from columns and assigning to messageID etc. */
                int postedBy = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long timePostedEpoch = resultSet.getLong("time_posted_epoch");
                Message message = new Message(messageId, postedBy, messageText, timePostedEpoch);/* creating a Message's object via extracted values  */
                messages.add(message); // adding single msg object to the list of messages 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages; // returning the list of messages 
    }
/*************************************GET MESSAGES BY MESSAGE ID ************************************************* */
    public Message getMessageById(int message_id) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM message WHERE message_id = ?");
            statement.setInt(1, message_id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int messageId = resultSet.getInt("message_id");
                int postedBy = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long timePostedEpoch = resultSet.getLong("time_posted_epoch");
                return new Message(messageId, postedBy, messageText, timePostedEpoch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
/********************************************* CHECK IF USER (account_id) EXISTS IN THE SYSTEM ********************************** */
    public boolean userExists(int user_id) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM account WHERE account_id = ?");
            statement.setInt(1, user_id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
/**********************************************GETTING MESSAGE BY ACCOUNT ID ************************************ */
    public List<Message> getMessagesByUser(int account_id) {
        List<Message> messages = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM message WHERE posted_by = ?");
            statement.setInt(1, account_id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int messageId = resultSet.getInt("message_id");
                int postedBy = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long timePostedEpoch = resultSet.getLong("time_posted_epoch");
                Message message = new Message(messageId, postedBy, messageText, timePostedEpoch);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
/* **************UPDATING A MESSAGE *********************** */



    
}
