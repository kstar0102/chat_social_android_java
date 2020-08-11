package ar.codeslu.plax.notify;

/**
 * Created by CodeSlu on 9/4/2018.
 */

public class Result {
    public String messageID;

    public Result() {
    }

    public Result(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
