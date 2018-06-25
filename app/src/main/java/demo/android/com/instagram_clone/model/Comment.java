package demo.android.com.instagram_clone.model;

import java.util.List;

/**
 * Created by Darshan B.S on 12-06-2018.
 */

public class Comment {
    private String comment, date_created, user_id;
    private List<LikedUser> likes;

    public Comment() {
    }

    public Comment(String comment, String date_created,
                   String user_id, List<LikedUser> likes) {
        this.comment = comment;
        this.date_created = date_created;
        this.user_id = user_id;
        this.likes = likes;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<LikedUser> getLikes() {
        return likes;
    }

    public void setLikes(List<LikedUser> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", date_created='" + date_created + '\'' +
                ", user_id='" + user_id + '\'' +
                ", likes=" + likes +
                '}';
    }
}
