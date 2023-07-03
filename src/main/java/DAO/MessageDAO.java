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
/*Method to delete an existing message  */
    public Message deleteMessage(int message_id) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM message WHERE message_id = ?");
            statement.setInt(1, message_id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Fetch the deleted message and return it
                return getMessageById(message_id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    



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

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM message");
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
}
