package com.stfalcon.chatkit.me;

import android.support.annotation.Nullable;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

/**
 * Created by mostafa on 29/01/19.
 */

public class Message implements IMessage,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType /*and this one is for custom content type (in this case - voice message)*/ {

    private String id,type,react;
    private String text,statue,messid,thumb;
    private boolean deleted;
    private Date createdAt;
    private UserIn user;
    private Image image;
    private Voice voice;
    private Video video;
    private File file;
    private Map map;

    public Message(String id, UserIn user, String text,String statue,String type,String messid,boolean deleted,String react) {
        this(id, user, text, new Date(),statue,type,messid,deleted,react);
    }

    public Message(String id, UserIn user, String text, Date createdAt,String statue,String type,String messid,boolean deleted,String react) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
        this.statue = statue;
        this.type = type;
        this.messid = messid;
        this.deleted = deleted;
        this.react = react;
    }
    public Message(String id, UserIn user, String text,String statue,String type,String messid,String thumb,boolean deleted,String react) {
        this(id, user, text, new Date(),statue,type,messid,thumb,deleted,react);
    }

    public Message(String id, UserIn user, String text, Date createdAt,String statue,String type,String messid,String thumb,boolean deleted,String react) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
        this.statue = statue;
        this.type = type;
        this.messid = messid;
        this.thumb = thumb;
        this.deleted = deleted;
        this.react = react;
    }
    public Message(String id, UserIn user, String text) {
        this(id, user, text, new Date());
    }

    public Message(String id, UserIn user, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Message() {
    }

    public String getReact() {
        return react;
    }

    public void setReact(String react) {
        this.react = react;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setUser(UserIn user) {
        this.user = user;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public UserIn getUser() {
        return this.user;
    }

    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }

    public Voice getVoice() {
        return voice;
    }

    public String getStatus() {
        return statue;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public String getMessid() {
        return messid;
    }

    public void setMessid(String messid) {
        this.messid = messid;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    public static class Voice {

        private String url;
        private String duration;

        public Voice(String url, String duration) {
            this.url = url;
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public String getDuration() {
            return duration;
        }
    }
    public static class Video {

        private String url;
        private String duration,thumb;

        public Video(String url,String duration,String thumb) {
            this.url = url;
            this.duration = duration;
            this.thumb = thumb;

        }

        public String getThumb() {
            return thumb;
        }

        public String getDuration() {
            return duration;
        }

        public String getUrl() {
            return url;
        }

    }
    public static class File {

        private String linkF;
        private String filename;

        public File(String linkF, String filename) {
            this.linkF = linkF;
            this.filename = filename;
        }

        public String getUrl() {
            return linkF;
        }

        public String getFilename() {
            return filename;
        }

    }
    public static class Map {

        private String location;

        public Map(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }


    }
}