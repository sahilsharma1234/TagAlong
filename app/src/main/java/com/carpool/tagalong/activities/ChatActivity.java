package com.carpool.tagalong.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.ChatAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.chat.ChatModel;
import com.carpool.tagalong.models.chat.ChatSendMessageRequest;
import com.carpool.tagalong.models.chat.ModelChatSendMessageResponse;
import com.carpool.tagalong.models.chat.ModelGetChatConversationResponse;
import com.carpool.tagalong.models.chat.ModelGetChatRequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private String userName, receiverId;
    private RecyclerView chatRecyclerView;
    private List<ChatModel> chatList = new ArrayList<>();
    private ChatAdapter chatAppMsgAdapter;

    private BroadcastReceiver chatListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent != null){

                if(intent.getExtras()!= null){
                    if(intent.getExtras().containsKey(Constants.RECEIVERID)){
                        receiverId  =  intent.getExtras().getString(Constants.RECEIVERID);
                        if(receiverId!= null){
                            getChatConversation();
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.list_chat_messages);

        // Get RecyclerView object.
        final RecyclerView msgRecyclerView = findViewById(R.id.list_chat_messages);

        // Set RecyclerView layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        if (getIntent().getExtras() != null) {

            userName = getIntent().getStringExtra("userName");
            receiverId = getIntent().getStringExtra("receiverId");
        }

        setToolBar();

        final EditText msgInputText = findViewById(R.id.edit_chat_message);

        ImageButton msgSendButton = findViewById(R.id.button_chat_send);

        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgContent = msgInputText.getText().toString();
                if (!TextUtils.isEmpty(msgContent)) {

                    sendMessage(msgContent);

                    // Add a new sent message to the list.
                    ChatModel msgDto = new ChatModel();
                    msgDto.setSender(TagALongPreferenceManager.getDeviceProfile(ChatActivity.this).get_id());
                    msgDto.setMsg(msgContent);
                    chatList.add(msgDto);

                    int newMsgPosition = chatList.size() - 1;

                    // Notify recycler view insert one new data.
                    chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

                    // Scroll RecyclerView to the last message.
                    msgRecyclerView.scrollToPosition(newMsgPosition);

                    // Empty the input edit text box.
                    msgInputText.setText("");
                }
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(chatListener, new IntentFilter(Constants.CHAT_MESSAGE));
    }

    private void setToolBar() {

        toolbarLayout = findViewById(R.id.toolbar_chat);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText(userName);
        title.setVisibility(View.VISIBLE);
        titleImage.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }

        getChatConversation();
    }

    private void getChatConversation() {

        try {

            if (Utils.isNetworkAvailable(this)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ModelGetChatRequest modelGetChatRequest = new ModelGetChatRequest();
                    modelGetChatRequest.setReceiver(receiverId);

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.getConversationMessages(TagALongPreferenceManager.getToken(this), modelGetChatRequest).enqueue(new Callback<ModelGetChatConversationResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetChatConversationResponse> call, Response<ModelGetChatConversationResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    handleChatListResponse(response.body());
                                }else{
                                    Toast.makeText(ChatActivity.this, response.message(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(ChatActivity.this, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetChatConversationResponse> call, Throwable t) {
                            ProgressDialogLoader.progressDialogDismiss();
                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("GET CHAT", "FAILURE verification");
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            ProgressDialogLoader.progressDialogDismiss();
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {

        try {

            if (Utils.isNetworkAvailable(this)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ChatSendMessageRequest modelGetChatRequest = new ChatSendMessageRequest();
                    modelGetChatRequest.setReceiver(receiverId);
                    modelGetChatRequest.setMsg(message);

//                    ProgressDialogLoader.progressDialogCreation(this,getString(R.string.please_wait));

                    restClientRetrofitService.sendMessage(TagALongPreferenceManager.getToken(this), modelGetChatRequest).enqueue(new Callback<ModelChatSendMessageResponse>() {

                        @Override
                        public void onResponse(Call<ModelChatSendMessageResponse> call, Response<ModelChatSendMessageResponse> response) {

//                            ProgressDialogLoader.progressDialogDismiss();
                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                }
                            } else {
                                Toast.makeText(ChatActivity.this, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelChatSendMessageResponse> call, Throwable t) {
//                            ProgressDialogLoader.progressDialogDismiss();
                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("GET CHAT", "FAILURE verification");
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
//            ProgressDialogLoader.progressDialogDismiss();
            e.printStackTrace();
        }
    }

    private void handleChatListResponse(ModelGetChatConversationResponse response) {

        if (response.getData() != null) {

            if (response.getData().size() > 0) {

                // Create the initial data list.
                chatList = response.getData();

                // Create the data adapter with above data list.
                chatAppMsgAdapter = new ChatAdapter(chatList, this);

                // Set data adapter to RecyclerView.
                chatRecyclerView.setAdapter(chatAppMsgAdapter);
            } else {

                chatAppMsgAdapter = new ChatAdapter(chatList, this);
                // Set data adapter to RecyclerView.
                chatRecyclerView.setAdapter(chatAppMsgAdapter);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(chatListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(chatListener);
    }
}