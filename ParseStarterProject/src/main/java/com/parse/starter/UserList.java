package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    ListView userListView;

    ArrayList<String> usersArrayList ;

    ArrayAdapter<String> usersArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("UserList");

        userListView = (ListView) findViewById(R.id.userListView);

        usersArrayList = new ArrayList<String>();

        usersArrayAdapter = new ArrayAdapter<String>(UserList.this,android.R.layout.simple_list_item_1,usersArrayList);

        userListView.setAdapter(usersArrayAdapter);

        //get All users except current user

        //this will get all the users where each user name is not equal to the current user
        ParseQuery<ParseUser> users = ParseUser.getQuery();

        users.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());

        users.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if(e == null){

                    if(objects.size() > 0){

                        //loop through all all the objects
                        for(ParseUser user : objects){

                            //add it to the arraylist
                            usersArrayList.add(user.getUsername());
                        }

                        usersArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });



        //redirect user to chat activity with the user taped on in tbe list
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);

                intent.putExtra("chatWith",usersArrayList.get(i));

                startActivity(intent);
            }
        });
    }
}
