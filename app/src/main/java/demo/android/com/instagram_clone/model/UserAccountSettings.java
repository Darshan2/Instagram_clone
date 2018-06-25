package demo.android.com.instagram_clone.model;

import com.nostra13.universalimageloader.utils.L;

import java.util.HashMap;

/**
 * Created by Admin on 30-05-2018.
 */

public class UserAccountSettings {
    private String description, display_name, profile_photo, user_name, website;
    private HashMap<String, LikedUser> followers, following;
    private long posts;

    public UserAccountSettings() {
    }

    public UserAccountSettings(String description, String display_name, String profile_photo,
                               String user_name, String website, HashMap<String, LikedUser> followers,
                               HashMap<String, LikedUser> following, long posts) {
        this.description = description;
        this.display_name = display_name;
        this.profile_photo = profile_photo;
        this.user_name = user_name;
        this.website = website;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public HashMap<String, LikedUser> getFollowers() {
        return followers;
    }

    public void setFollowers(HashMap<String, LikedUser> followers) {
        this.followers = followers;
    }

    public HashMap<String, LikedUser> getFollowing() {
        return following;
    }

    public void setFollowing(HashMap<String, LikedUser> following) {
        this.following = following;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "description='" + description + '\'' +
                ", display_name='" + display_name + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", user_name='" + user_name + '\'' +
                ", website='" + website + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                '}';
    }
}
