package demo.android.com.instagram_clone.model;

/**
 * Created by Darshan B.S on 10-06-2018.
 */

public class LikedUser {
    String user_id;

    public LikedUser() {
    }

    public LikedUser(String user_id) {

        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "LikedUser{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
