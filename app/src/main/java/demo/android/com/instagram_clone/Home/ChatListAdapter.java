package demo.android.com.instagram_clone.Home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.model.ChatItem;

/**
 * Created by Darshan B.S on 22-06-2018.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<ChatItem> mChatItemsList;
    private String currentUserId;

    public ChatListAdapter(Context mContext, ArrayList<ChatItem> chatItemsList, String currentUserId) {
        this.mContext = mContext;
        this.mChatItemsList = chatItemsList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       LayoutInflater inflater = LayoutInflater.from(mContext);
       View view = inflater.inflate(R.layout.view_chat_item, null);

       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatItem chatItem = mChatItemsList.get(position);

        if(chatItem.getUser_id().equals(currentUserId)) {
            //it is sent message
            holder.mSentMessage.setVisibility(View.VISIBLE);
            holder.mReceivedMessage.setVisibility(View.GONE);

            holder.mSentMessage.setText(chatItem.getChat_text());
        } else {
            //it is a received message
            holder.mSentMessage.setVisibility(View.GONE);
            holder.mReceivedMessage.setVisibility(View.VISIBLE);

            holder.mReceivedMessage.setText(chatItem.getChat_text());
        }

    }


    @Override
    public int getItemCount() {
        return mChatItemsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mSentMessage, mReceivedMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            mSentMessage = itemView.findViewById(R.id.sent_messsage);
            mReceivedMessage = itemView.findViewById(R.id.received_message);
        }
    }
}
