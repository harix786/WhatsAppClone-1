package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class ChatActivity extends AppCompatActivity {


    EditText newMessageEditText;

    String message;

    String chatWith;

    ListView chatListView;

    ArrayList<String> chatArrayList ;

    ArrayAdapter<String> chatArrayAdapter;

    Handler pastChatHandler;

    public void sendMessage (View view){

        //get the message from the EditText

        message = newMessageEditText.getText().toString();

        //save it in class Message in parse

        ParseObject newMessage = new ParseObject("Message");

        //message
        newMessage.put("message",message);

        //sender
        newMessage.put("sender", ParseUser.getCurrentUser().getUsername());

        //Recipient
        newMessage.put("recipient",chatWith);

        //save object in class Message in Parse
        newMessage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

             if(e == null){

                 newMessageEditText.setText("");
                 Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

             }
            }
        });


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        pastChatHandler = new Handler();



        chatListView = (ListView) findViewById(R.id.pastChatListView);

        chatArrayList = new ArrayList<String>();

        chatArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,chatArrayList);


        chatListView.setAdapter(chatArrayAdapter);


        newMessageEditText = (EditText) findViewById(R.id.newMesaageEditText);

        //get username taped on in the ListView

        Intent intent = getIntent();

        //username in putExtra in UserListActivity in OnClick

         chatWith = intent.getStringExtra("chatWith");

        setTitle(chatWith);


        //update the list every second to make it look like RealTime chatting

        //otherwise you have to close the activity and run it again to see new chat because teh quries taht get the chat runs once in teh onCreate

        pastChatHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                updatePastChat();

                //call it recursively every second
                pastChatHandler.postDelayed(this, 1000);
            }
        },1000);


    }


    public void updatePastChat(){

        Log.i("in runnable","yes");
        //get pastChats

        //get message between user1 and user2 , ie: between currentUser and recipient

        ParseQuery<ParseObject> user1Touser2 = new ParseQuery<ParseObject>("Message");

        user1Touser2.whereEqualTo("sender",ParseUser.getCurrentUser().getUsername());

        user1Touser2.whereEqualTo("recipient",chatWith);

        //get message between user2 to user1 , ie: between recipient and currentUser

        ParseQuery<ParseObject> user2Touser1 = new ParseQuery<ParseObject>("Message");

        user2Touser1.whereEqualTo("recipient",ParseUser.getCurrentUser().getUsername());

        user2Touser1.whereEqualTo("sender",chatWith);

        //combine both queries in a list

        List<ParseQuery<ParseObject>> combinedQuery = new ArrayList<ParseQuery<ParseObject>>();

        //add the two quires to the List

        combinedQuery.add(user1Touser2);

        combinedQuery.add(user2Touser1);

        ParseQuery<ParseObject> getAllChat = ParseQuery.or(combinedQuery);

        //newest at the bottom

        getAllChat.orderByAscending("createdAt");

        getAllChat.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e == null){

                    if(objects.size() > 0){


                        //put them in the list


                        //clear the list  , its safer we are getiing everything from start anyways
                        chatArrayList.clear();
                        for(ParseObject message : objects){


                            String messageContent = message.getString("message");

                            //if the sender is not the current user we will put a greater sign in front of the message to differentiate
                            if(!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername()))
                            {
                                messageContent = ">" + message.getString("message");
                            }
                            chatArrayList.add(messageContent);

                        }


                        chatArrayAdapter.notifyDataSetChanged();

                    }
                }

            }
        });

    }

}
