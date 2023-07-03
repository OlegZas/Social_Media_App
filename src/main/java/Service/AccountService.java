package Service;

import java.sql.SQLException;

import DAO.AccountDAO;
import Model.Account;

//Service class acts as an intermediate layer between the web layer (controller) and the persistence laye
/*That means that the Service class performs tasks that aren't done through the web or
 * SQL: programming tasks like checking that the input is valid, conducting additional security checks, or saving the
 * actions undertaken by the API to a logging file. */

public class AccountService {
    private AccountDAO accountDAO; // private field named accountDAO of type AccountDAO; 
    /*This field represents the dependency of the AccountService on the AccountDAO class. It allows 
    the AccountService to interact with the persistence layer and perform database
     operations through the AccountDAO object.*/

     public AccountService(){
        accountDAO = new AccountDAO(); /* no-args constructor for creating a new AccountService with a new AccountDAO. */
    }
    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO; /*The parameterized constructor in the AccountService class allows us to inject 
        a specific instance of the AccountDAO class into the AccountService. */
    }
//register user 
    public Account registerUser(String username, String password) {

        // Validating inputs (this is first layer; if this condition is not met then method will proceed to AccountDAO, if met, it will return null)
        if (username.isBlank()) {
            return null; 
            
        }
        if (password.length() < 4) {
            return null; 
           
        }
        try {
        if (accountDAO.usernameExists(username)) {
            return null; 
            // throw new IllegalArgumentException("Username already exists!");
        }

            // Create a new Account object with the provided username and password
            Account account = new Account(username, password);
            // Persist the new account in the database using the AccountDAO
            return accountDAO.createAccount(account);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

//method for handling the login functionality 
    public Account loginUser(String username, String password) {
        try {
            Account account = accountDAO.getAccountByUsernameAndPassword(username, password);
            if (account != null) {
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    

}
