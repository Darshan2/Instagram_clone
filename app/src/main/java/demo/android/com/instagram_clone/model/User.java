package demo.android.com.instagram_clone.model;

/**
 * Created by Admin on 30-05-2018.
 */

public class User {
    private String email, user_id, user_name;
    private long phone_number;

    public User() {}

    public User(String email, long phone_number, String user_id, String user_name) {
        this.email = email;
        this.phone_number = phone_number;
        this.user_id = user_id;
        this.user_name = user_name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                '}';
    }
}
