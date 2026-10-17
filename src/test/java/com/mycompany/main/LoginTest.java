package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginTest {

    private Login login;

    @BeforeEach
    public void setUp() {
        login = new Login("Kyle", "Smith");
    }

    // ---------- assertEquals tests ----------

    @Test
    public void usernameCorrectlyFormatted() {
        assertEquals("Username successfully captured.", login.usernameStatus("kyl_1"));
    }

    @Test
    public void usernameIncorrectlyFormatted() {
        assertEquals("Username is not correctly formatted; please ensure that your username contains "
                + "an underscore and is no more than five characters in length.",
                login.usernameStatus("kyle!!!!!!!"));
    }

    @Test
    public void passwordMeetsComplexity() {
        assertEquals("Password successfully captured.", login.passwordStatus("Ch&&sec@ke99!"));
    }

    @Test
    public void passwordDoesNotMeetComplexity() {
        assertEquals("Password is not correctly formatted; please ensure that the password contains "
                + "at least eight characters, a capital letter, a number, and a special character.",
                login.passwordStatus("password"));
    }

    @Test
    public void cellPhoneCorrectlyFormatted() {
        assertEquals(Login.CELL_OK, login.cellPhoneStatus("+27838968976"));
    }

    @Test
    public void cellPhoneIncorrectlyFormatted() {
        assertEquals(Login.CELL_BAD, login.cellPhoneStatus("08966553"));
    }

    @Test
    public void registerUserSuccess() {
        assertEquals(Login.REGISTER_OK,
                login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976"));
    }

    @Test
    public void registerUserBadUsernameIsReportedFirst() {
        assertEquals(Login.USERNAME_BAD,
                login.registerUser("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976"));
    }

    @Test
    public void registerUserBadPassword() {
        assertEquals(Login.PASSWORD_BAD,
                login.registerUser("kyl_1", "password", "+27838968976"));
    }

    @Test
    public void loginStatusMessages() {
        assertEquals("Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus(true));
        assertEquals("Username or password incorrect, please try again.",
                login.returnLoginStatus(false));
    }

    // ---------- assertTrue / assertFalse tests ----------

    @Test
    public void loginSuccessful() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void loginFailed() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.loginUser("kyl_1", "wrongPass1!"));
    }

    @Test
    public void loginFailsWhenNobodyIsRegistered() {
        assertFalse(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void usernameCorrectlyFormattedTrue() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void usernameIncorrectlyFormattedFalse() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void passwordMeetsComplexityTrue() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void passwordDoesNotMeetComplexityFalse() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void cellPhoneCorrectlyFormattedTrue() {
        assertTrue(Login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void cellPhoneIncorrectlyFormattedFalse() {
        assertFalse(Login.checkCellPhoneNumber("08966553"));
    }
}
