package demo.android.com.instagram_clone.model;

/**
 * Created by Admin on 31-05-2018.
 */

public class UserAndSettings {
    private User user;
    private UserAccountSettings userAccountSettings;

    public UserAndSettings() {
    }

    public UserAndSettings(User user, UserAccountSettings userAccountSettings) {
        this.user = user;
        this.userAccountSettings = userAccountSettings;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSettings getUserAccountSettings() {
        return userAccountSettings;
    }

    public void setUserAccountSettings(UserAccountSettings userAccountSettings) {
        this.userAccountSettings = userAccountSettings;
    }

    @Override
    public String toString() {
        return "UserAndSettings{" +
                "user=" + user +
                ", userAccountSettings=" + userAccountSettings +
                '}';
    }
}
