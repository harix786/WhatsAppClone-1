
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity {

  EditText userNameEditText ;

  EditText passwordEditText ;

  TextView secondButtonText;

  Button signUpOrLoginButton ;

  String buttonText;

  String  userName;

  String userPassword;


 public void  startUserListActivity(){

   Intent intent = new Intent(getApplicationContext(),UserList.class);

   startActivity(intent);
 }
  //This function attemps to change the buttonText
  public void changeButtonText(View view){

    //check buttonText
    buttonText = signUpOrLoginButton.getText().toString();

    if(buttonText.equals("SignUp")){

      //change ButtonText To Login

      signUpOrLoginButton.setText("Login");

      //change secondButtonText to SignUp

      secondButtonText.setText("SignUp");

    }else{

      //If buttonTetx  is Login change buttonTetx to SignUp

      signUpOrLoginButton.setText("SignUp");

      //change SecondButtonTetx to SignUp

      secondButtonText.setText("Login");
    }

  }

  //this function attempts to signup or Login user depending on the buttonText
  public void signUpOrLogin(View view){


    //Logout currentUser if there is one to avoid errors

    if(ParseUser.getCurrentUser() != null){

      ParseUser.getCurrentUser().logOut();
    }


    userName = userNameEditText.getText().toString();

    userPassword = passwordEditText.getText().toString();


    //SignUp or Login Only if the user entered both username and password

    if(userName.length() > 0 && userPassword.length() > 0){


      //check buttonText

      buttonText = signUpOrLoginButton.getText().toString();

     //SignUp user
      if(buttonText.equals("SignUp")){


        //check if username dosent exist

        ParseQuery<ParseUser> getUsers = ParseUser.getQuery();

        getUsers.whereEqualTo("username",userName);


        getUsers.findInBackground(new FindCallback<ParseUser>() {
          @Override
          public void done(List<ParseUser> objects, ParseException e) {

            if(e == null){

              if(objects.size() > 0){

                //User exists then dont signup

                Toast.makeText(MainActivity.this, "User Already Exist", Toast.LENGTH_SHORT).show();
              }else{

                //UserName Dosent Exists so SignUp User

                //creating New User
                ParseUser newUser = new ParseUser();

                newUser.setUsername(userName);

                newUser.setPassword(userPassword);

                newUser.signUpInBackground(new SignUpCallback() {
                  @Override
                  public void done(ParseException e) {

                    if(e == null){

                      Toast.makeText(MainActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();

                      //redirectToUserList
                      startUserListActivity();

                    }else{

                      Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                  }
                });

              }
            }
          }
        });

      }else{


        //Login User
        //check if username and password exist and correct

        ParseUser.logInInBackground(userName, userPassword, new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {

            if(e == null ){

              Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

              //redirect to UserList
              startUserListActivity();

            }else{

              Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

          }
        });
      }

    }else{

      Toast.makeText(MainActivity.this, "Please make sure you entered both username and password", Toast.LENGTH_SHORT).show();

    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("WhatsApp Clone");

    userNameEditText = (EditText) findViewById(R.id.userNameEditTtext);

    passwordEditText = (EditText) findViewById(R.id.passwordEditText);

    secondButtonText = (TextView) findViewById(R.id.secondButtonTextView);

    signUpOrLoginButton = (Button) findViewById(R.id.signUpOrLoginButton);

    //if there a user already logged in redirect to UserList
    if(ParseUser.getCurrentUser() != null){

      startUserListActivity();
    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}
