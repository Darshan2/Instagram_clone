package demo.android.com.instagram_clone.model;

/**
 * Created by Darshan B.S on 22-06-2018.
 */

public class ChatItem {
    private String chat_text, user_id, date_created;

    public ChatItem() {
    }

    public ChatItem(String chat_text, String user_id, String date_created) {
        this.chat_text = chat_text;
        this.user_id = user_id;
        this.date_created = date_created;
    }

    public String getChat_text() {
        return chat_text;
    }

    public void setChat_text(String chat_text) {
        this.chat_text = chat_text;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    @Override
    public String toString() {
        return "ChatItem{" +
                "chat_text='" + chat_text + '\'' +
                ", user_id='" + user_id + '\'' +
                ", date_created='" + date_created + '\'' +
                '}';
    }
}
