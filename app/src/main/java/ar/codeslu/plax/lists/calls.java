package ar.codeslu.plax.lists;

public class calls {

    String from,to,id,name,ava;
    long time,dur;
    boolean incall;

    public calls() {
    }

    public calls(String from, String to, String id, String name, String ava, long time, long dur, boolean incall) {
        this.from = from;
        this.to = to;
        this.id = id;
        this.name = name;
        this.ava = ava;
        this.time = time;
        this.dur = dur;
        this.incall = incall;
    }

    public boolean isIncall() {
        return incall;
    }

    public void setIncall(boolean incall) {
        this.incall = incall;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAva() {
        return ava;
    }

    public void setAva(String ava) {
        this.ava = ava;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getDur() {
        return dur;
    }

    public void setDur(long dur) {
        this.dur = dur;
    }
}
