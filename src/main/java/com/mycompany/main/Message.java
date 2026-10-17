package com.mycompany.main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Part 2: one chat message plus the lists that track what has been sent or stored.
 */
public class Message {

    public static final int MAX_LENGTH = 250;

    public static final String RECIPIENT_OK = "Cell phone number successfully captured.";
    public static final String RECIPIENT_BAD = "Cell phone number is incorrectly formatted or does not contain "
            + "an international code. Please correct the number and try again.";
    public static final String LENGTH_OK = "Message ready to send.";

    public static final int SEND = 1;
    public static final int DISREGARD = 2;
    public static final int STORE = 3;

    // Shared by every message while the program runs
    private static final ArrayList<Message> sentMessages = new ArrayList<>();
    private static final ArrayList<Message> storedMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;
    private static String storageFile = "messages.json";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String text;
    private final String messageHash;

    // Used by the app: the ID is generated for us
    public Message(int messageNumber, String recipient, String text) {
        this(generateMessageID(), messageNumber, recipient, text);
    }

    // Used by the tests, where we need to know the ID up front
    public Message(String messageID, int messageNumber, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    // Random ten-digit number. Padded with zeros so it is always 10 characters.
    public static String generateMessageID() {
        long number = ThreadLocalRandom.current().nextLong(10_000_000_000L);
        return String.format("%010d", number);
    }

    // ID must not be longer than ten characters
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // Re-uses the cell number check from Login
    public String checkRecipientCell() {
        return recipientStatus(recipient);
    }

    // Same check on any number, so Main can use it before a Message exists
    public static String recipientStatus(String number) {
        return Login.checkCellPhoneNumber(number) ? RECIPIENT_OK : RECIPIENT_BAD;
    }

    // Tells the user if the text is short enough, or by how much it is too long
    public static String checkMessageLength(String text) {
        if (text.length() <= MAX_LENGTH) {
            return LENGTH_OK;
        }
        int extra = text.length() - MAX_LENGTH;
        return "Message exceeds " + MAX_LENGTH + " characters by " + extra + "; please reduce the size.";
    }

    public String getMessageIDMessage() {
        return "Message ID generated: " + messageID;
    }

    // First two digits of the ID : message number : first word + last word, all caps
    // e.g. 00:0:HITONIGHT
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+");
        String firstWord = stripPunctuation(words[0]);
        String lastWord = stripPunctuation(words[words.length - 1]);
        return messageID.substring(0, 2) + ":" + messageNumber + ":"
                + (firstWord + lastWord).toUpperCase();
    }

    // Removes things like the comma in "Mike," or the ? in "tonight?"
    private static String stripPunctuation(String word) {
        return word.replaceAll("[^a-zA-Z0-9]", "");
    }

    // Does what the user picked: 1 = send, 2 = disregard, 3 = store
    public String sentMessage(int choice) {
        switch (choice) {
            case SEND:
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case DISREGARD:
                return "Press 0 to delete the message.";
            case STORE:
                if (storeMessage()) {
                    return "Message successfully stored.";
                }
                return "Message could not be stored.";
            default:
                return "Invalid option.";
        }
    }

    // All sent messages in one string
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder all = new StringBuilder();
        for (Message m : sentMessages) {
            all.append(m.getDetails()).append("\n");
        }
        return all.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Details in the order the PoE asks for
    public String getDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text + "\n";
    }

    // Adds this message to the stored list and writes the whole list to the JSON file.
    // TODO (yours to do): research JSON writing and reference your source here.
    // The rubric takes marks off if the JSON code is not attributed.
    public boolean storeMessage() {
        storedMessages.add(this);

        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            json.append(storedMessages.get(i).toJson());
            if (i < storedMessages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]\n");

        try {
            Files.writeString(Path.of(storageFile), json.toString(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            storedMessages.remove(this);
            return false;
        }
    }

    private String toJson() {
        return "  {\n"
                + "    \"messageID\": \"" + escape(messageID) + "\",\n"
                + "    \"messageNumber\": " + messageNumber + ",\n"
                + "    \"messageHash\": \"" + escape(messageHash) + "\",\n"
                + "    \"recipient\": \"" + escape(recipient) + "\",\n"
                + "    \"message\": \"" + escape(text) + "\"\n"
                + "  }";
    }

    // Escapes quotes, slashes and line breaks so the JSON stays valid
    private static String escape(String value) {
        StringBuilder out = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"':  out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }

    // Getters
    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getText() { return text; }
    public String getMessageHash() { return messageHash; }

    // Used by the tests so they start clean and don't write into the real file
    public static void resetForTesting(String testFile) {
        sentMessages.clear();
        storedMessages.clear();
        totalMessagesSent = 0;
        storageFile = testFile;
    }
}
