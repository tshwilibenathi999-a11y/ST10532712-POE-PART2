package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageTest {

    // Test data from the PoE. IDs are fixed here so the hashes can be predicted.
    private static final String[] RECIPIENTS = {"+27718693002", "08575975889"};
    private static final String[] TEXTS = {
        "Hi Mike, can you join us for dinner tonight?",
        "Hi Keegan, did you receive the payment?"
    };
    private static final String[] EXPECTED_HASHES = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};

    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("quickchat-test", ".json");
        tempFile.deleteOnExit();
        Message.resetForTesting(tempFile.getAbsolutePath());
    }

    private Message testMessage(int index) {
        return new Message("0012345678", index, RECIPIENTS[index], TEXTS[index]);
    }

    // ---------- message length ----------

    @Test
    public void messageLengthSuccess() {
        assertEquals("Message ready to send.", Message.checkMessageLength(TEXTS[0]));
    }

    @Test
    public void messageLengthExactly250IsAllowed() {
        assertEquals("Message ready to send.", Message.checkMessageLength("a".repeat(250)));
    }

    @Test
    public void messageLengthFailure() {
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength("a".repeat(260)));
    }

    // ---------- recipient number ----------

    @Test
    public void recipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.", testMessage(0).checkRecipientCell());
    }

    @Test
    public void recipientIncorrectlyFormatted() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                testMessage(1).checkRecipientCell());
    }

    // ---------- message hash ----------

    @Test
    public void messageHashForTestCase1() {
        assertEquals("00:0:HITONIGHT", testMessage(0).createMessageHash());
    }

    @Test
    public void messageHashesInALoop() {
        for (int i = 0; i < TEXTS.length; i++) {
            assertEquals(EXPECTED_HASHES[i], testMessage(i).createMessageHash());
        }
    }

    // ---------- message ID ----------

    @Test
    public void messageIDIsCreated() {
        String id = Message.generateMessageID();
        assertEquals(10, id.length());
        assertTrue(id.matches("[0-9]{10}"));
        assertTrue(new Message(0, RECIPIENTS[0], TEXTS[0]).checkMessageID());
    }

    @Test
    public void messageIDMessage() {
        assertEquals("Message ID generated: 0012345678", testMessage(0).getMessageIDMessage());
    }

    @Test
    public void messageIDLongerThanTenFails() {
        Message tooLong = new Message("12345678901", 0, RECIPIENTS[0], TEXTS[0]);
        assertFalse(tooLong.checkMessageID());
    }

    // ---------- send / disregard / store ----------

    @Test
    public void userSelectsSend() {
        assertEquals("Message successfully sent.", testMessage(0).sentMessage(Message.SEND));
    }

    @Test
    public void userSelectsDisregard() {
        assertEquals("Press 0 to delete the message.", testMessage(1).sentMessage(Message.DISREGARD));
    }

    @Test
    public void userSelectsStore() {
        assertEquals("Message successfully stored.", testMessage(0).sentMessage(Message.STORE));
    }

    @Test
    public void invalidChoiceIsRejected() {
        assertEquals("Invalid option.", testMessage(0).sentMessage(9));
    }

    // ---------- totals and printing ----------

    @Test
    public void totalMessagesOnlyCountsSentOnes() {
        testMessage(0).sentMessage(Message.SEND);
        testMessage(1).sentMessage(Message.DISREGARD);
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void printMessagesShowsDetailsInOrder() {
        testMessage(0).sentMessage(Message.SEND);
        String expected = "Message ID: 0012345678\n"
                + "Message Hash: 00:0:HITONIGHT\n"
                + "Recipient: +27718693002\n"
                + "Message: Hi Mike, can you join us for dinner tonight?\n\n";
        assertEquals(expected, Message.printMessages());
    }

    @Test
    public void printMessagesWhenNothingSent() {
        assertEquals("No messages have been sent yet.", Message.printMessages());
    }

    // ---------- JSON file ----------

    @Test
    public void storedMessageIsWrittenToJsonFile() throws IOException {
        testMessage(0).sentMessage(Message.STORE);
        String json = Files.readString(tempFile.toPath(), StandardCharsets.UTF_8);
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
        assertTrue(json.contains("\"recipient\": \"+27718693002\""));
    }

    @Test
    public void quotesInMessagesAreEscapedInJson() throws IOException {
        new Message("0012345678", 0, "+27718693002", "Say \"hi\" now").storeMessage();
        String json = Files.readString(tempFile.toPath(), StandardCharsets.UTF_8);
        assertTrue(json.contains("Say \\\"hi\\\" now"));
    }
}
