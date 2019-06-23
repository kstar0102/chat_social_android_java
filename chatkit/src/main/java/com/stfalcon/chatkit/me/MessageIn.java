package com.stfalcon.chatkit.me;

/**
 * Created by mostafa on 30/01/19.
 */

public class MessageIn {
    String message,type,statue,from,linkV,linkI,messId,duration,linkF,filename,linkVideo,location,thumb,react;
    long time;
    boolean seen,deleted ;

    public MessageIn() {
    }

    public MessageIn(String message, String type, String statue, String from, String linkV, String linkI, long time, boolean seen,String thumb,boolean deleted) {
        this.message = message;
        this.type = type;
        this.statue = statue;
        this.from = from;
        this.linkV = linkV;
        this.linkI = linkI;
        this.time = time;
        this.seen = seen;
        this.thumb = thumb;
        this.deleted = deleted;
    }

    //local messages
    //text
    public MessageIn(String message, String type, String statue, String from, long time, boolean seen,boolean deleted,String messId,String react) {
        this.message = message;
        this.type = type;
        this.statue = statue;
        this.from = from;
        this.time = time;
        this.seen = seen;
        this.deleted = deleted;
        this.messId = messId;
        this.react = react;
    }

    //map
    public MessageIn(String location, String statue, String from, long time, boolean seen,String react,boolean deleted,String messId, String type) {
        this.location = location;
        this.type = type;
        this.statue = statue;
        this.from = from;
        this.time = time;
        this.seen = seen;
        this.deleted = deleted;
        this.messId = messId;
        this.react = react;
    }
    //voice
    public MessageIn(String linkV, String statue, String from, long time, boolean seen,boolean deleted,String messId, String type,String react,String duration) {
        this.type = type;
        this.statue = statue;
        this.from = from;
        this.linkV = linkV;
        this.time = time;
        this.seen = seen;
        this.deleted = deleted;
        this.messId = messId;
        this.duration = duration;
        this.react = react;
    }
    //video
    public MessageIn(String linkVideo, String statue, String from, long time, boolean seen,boolean deleted,String messId, String type,String duration,String thumb,String react) {
        this.linkVideo = linkVideo;
        this.type = type;
        this.statue = statue;
        this.from = from;
        this.time = time;
        this.seen = seen;
        this.deleted = deleted;
        this.messId = messId;
        this.duration = duration;
        this.thumb = thumb;
        this.react = react;
    }
    //file
    public MessageIn(String linkF, String statue, long time, boolean seen,boolean deleted,String messId, String type,String filename,String from,String react) {
        this.linkF = linkF;
        this.type = type;
        this.statue = statue;
        this.from = from;
        this.time = time;
        this.seen = seen;
        this.deleted = deleted;
        this.messId = messId;
        this.filename = filename;
        this.react = react;
    }
    //image
    public MessageIn(String linkI, String type,String messId, String statue, String from, long time, boolean seen,boolean deleted,String react) {
        this.type = type;
        this.statue = statue;
        this.from = from;
        this.linkI = linkI;
        this.time = time;
        this.seen = seen;
        this.messId = messId;
        this.deleted = deleted;
        this.react = react;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getLinkF() {
        return linkF;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public void setLinkF(String linkF) {
        this.linkF = linkF;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getReact() {
        return react;
    }

    public void setReact(String react) {
        this.react = react;
    }

    public String getMessId() {
        return messId;
    }

    public void setMessId(String messId) {
        this.messId = messId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLinkV() {
        return linkV;
    }

    public void setLinkV(String linkV) {
        this.linkV = linkV;
    }

    public String getLinkI() {
        return linkI;
    }

    public void setLinkI(String linkI) {
        this.linkI = linkI;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
