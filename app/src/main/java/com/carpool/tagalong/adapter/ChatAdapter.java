package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ContactList;
import com.carpool.tagalong.models.chat.ChatModel;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;

import java.util.List;


/**
 * Created by sahilsharma on 31/1/18.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatModel> dataList;
    public  static ContactList selectedContactList;
    private  int  selectedCount = 0;
    private Context context;

    public ChatAdapter(List<ChatModel> dataList, Context context) {

        this.dataList = dataList;
        this.selectedContactList = new ContactList();
        this.context = context;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {

        ChatModel chatModel = this.dataList.get(position);

        if(chatModel.getSender().equalsIgnoreCase(TagALongPreferenceManager.getDeviceProfile(context).get_id())){
            holder.leftMsgLayout.setVisibility(LinearLayout.GONE);

            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.rightMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.rightMsgcom.setText(chatModel.getMsg());

        }else{

            // Show sent message in right linearlayout.
            holder.rightMsgLayout.setVisibility(LinearLayout.GONE);

            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.leftMsgcom.setText(chatModel.getMsg());

        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftMsgLayout;

        LinearLayout rightMsgLayout;

        com.carpool.tagalong.views.RegularTextView leftMsgcom;

        com.carpool.tagalong.views.RegularTextView rightMsgcom;

        public ViewHolder(View view) {
            super(view);

            if(view!=null) {
                leftMsgLayout    =  view.findViewById(R.id.chat_left_msg_layout);
                rightMsgLayout   =  view.findViewById(R.id.chat_right_msg_layout);
                leftMsgcom =  view.findViewById(R.id.chat_left_msg_text_view);
                rightMsgcom =  view.findViewById(R.id.chat_right_msg_text_view);
            }
        }
    }
}