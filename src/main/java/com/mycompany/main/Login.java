package com.mycompany.main;

/**
 * Part 1: registration and login.
 * Holds the registered user's details and checks them.
 */
public class Login {

    // Messages from the PoE tables
    public static final String USERNAME_OK = "Username successfully captured.";
    public static final String USERNAME_BAD = "Username is not correctly formatted; please ensure that your "
            + "username contains an underscore and is no more than five characters in length.";

    public static final String PASSWORD_OK = "Password successfully captured.";
    public static final String PASSWORD_BAD = "Password is not correctly formatted; please ensure that the "
            + "password contains at least eight characters, a capital letter, a number, and a special character.";

    public static final String CELL_OK = "Cell phone number successfully added.";
    public static final String CELL_BAD = "Cell phone number incorrectly formatted or does not contain "
            + "international code.";

    public static final String REGISTER_OK = "User has been registered successfully.";
    public static final String LOGIN_BAD = "Username or password incorrect, please try again.";

    // Name is needed for the welcome message
    private String firstName;
    private String lastName;

    // Details saved when registration works
    private String registeredUsername;
    private String registeredPassword;
    private String registeredCellPhone;

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Username needs an underscore and can be at most 5 characters
    public boolean checkUserName(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    // Password: 8+ characters, a capital letter, a number and a special character
    public boolean checkPasswordComplexity(String password) {
        return password != null
                && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    // Cell number: "+", a country code of 1 to 3 digits, then the number (up to 10 digits).
    // TODO (yours to do): research a regex checker and add your reference here, e.g.
    // Author/Site (Year) Title. Available at: <URL> (Accessed: date).
    // Rubric says the regex has to be attributed or you lose marks.
    public static boolean checkCellPhoneNumber(String number) {
        return number != null && number.matches("^\\+[0-9]{1,3}[0-9]{1,10}$");
    }

    // Status messages, one per field, so main can give feedback as the user types
    public String usernameStatus(String username) {
        return checkUserName(username) ? USERNAME_OK : USERNAME_BAD;
    }

    public String passwordStatus(String password) {
        return checkPasswordComplexity(password) ? PASSWORD_OK : PASSWORD_BAD;
    }

    public String cellPhoneStatus(String number) {
        return checkCellPhoneNumber(number) ? CELL_OK : CELL_BAD;
    }

    // Checks all three details; saves them only if everything is valid
    public String registerUser(String username, String password, String cellPhone) {
        if (!checkUserName(username)) {
            return USERNAME_BAD;
        }
        if (!checkPasswordComplexity(password)) {
            return PASSWORD_BAD;
        }
        if (!checkCellPhoneNumber(cellPhone)) {
            return CELL_BAD;
        }

        registeredUsername = username;
        registeredPassword = password;
        registeredCellPhone = cellPhone;
        return REGISTER_OK;
    }

    // True only if the details match what was saved at registration
    public boolean loginUser(String username, String password) {
        return registeredUsername != null
                && registeredUsername.equals(username)
                && registeredPassword.equals(password);
    }

    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return LOGIN_BAD;
    }
}
