package demo.android.com.instagram_clone.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Darshan B.S on 14-06-2018.
 */

public class UserMatched implements Parcelable {
    private String user_name, user_id, profile_photo, email;

    public UserMatched(String user_id, String user_name, String profile_photo, String email) {
        this.user_name = user_name;
        this.user_id = user_id;
        this.profile_photo = profile_photo;
        this.email = email;
    }

    public UserMatched() {
    }

    protected UserMatched(Parcel in) {
        user_name = in.readString();
        user_id = in.readString();
        profile_photo = in.readString();
        email = in.readString();
    }

    public static final Creator<UserMatched> CREATOR = new Creator<UserMatched>() {
        @Override
        public UserMatched createFromParcel(Parcel in) {
            return new UserMatched(in);
        }

        @Override
        public UserMatched[] newArray(int size) {
            return new UserMatched[size];
        }
    };

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserMatched{" +
                "user_name='" + user_name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_name);
        dest.writeString(user_id);
        dest.writeString(profile_photo);
        dest.writeString(email);
    }
}
