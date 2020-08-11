package ar.codeslu.plax.notify;

import java.util.Map;


/**
 * Created by CodeSlu on 9/4/2018.
 */

public class Sender {
    public String to;
    public Map<String,String> data;

    public Sender() {
    }

    public Sender(String to, Map<String, String> data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}

