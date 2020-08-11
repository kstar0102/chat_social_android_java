package com.stfalcon.chatkit.me;

/**
 * Created by mostafa on 30/01/19.
 */

public class MessageFav {
    String message,type,statue,from,linkV,linkI,messId,duration,linkF,filename,linkVideo,location,thumb,react,avatar;
    long time,favtime;
    boolean seen,deleted,chat ;


    public MessageFav() {
    }

    public MessageFav(String message, String type, String statue, String from, String linkV, String linkI, String messId, String duration, String linkF, String filename, String linkVideo, String location, String thumb, String react, String avatar, long time, long favtime, boolean seen, boolean deleted, boolean chat) {
        this.message = message;
        this.type = type;
        this.statue = statue;
        this.from = from;
        this.linkV = linkV;
        this.linkI = linkI;
        this.messId = messId;
        this.duration = duration;
        this.linkF = linkF;
        this.filename = filename;
        this.linkVideo = linkVideo;
        this.location = location;
        this.thumb = thumb;
        this.react = react;
        this.avatar = avatar;
        this.time = time;
        this.favtime = favtime;
        this.seen = seen;
        this.deleted = deleted;
        this.chat = chat;
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

    public String getMessId() {
        return messId;
    }

    public void setMessId(String messId) {
        this.messId = messId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLinkF() {
        return linkF;
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

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getReact() {
        return react;
    }

    public void setReact(String react) {
        this.react = react;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getFavtime() {
        return favtime;
    }

    public void setFavtime(long favtime) {
        this.favtime = favtime;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isChat() {
        return chat;
    }

    public void setChat(boolean chat) {
        this.chat = chat;
    }
}
