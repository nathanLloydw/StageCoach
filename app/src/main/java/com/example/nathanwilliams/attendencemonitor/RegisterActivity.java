package com.example.nathanwilliams.attendencemonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity
{
    //details for registration
    private EditText userName, Email, Password;

    //simple progress bar
    private ProgressDialog progressBar;

    //firebase authentication
    private FirebaseAuth Auth;
    private DatabaseReference Database;

    //button for activating registration
    private CardView mRegBtnCard;
    private TextView mRegBtn;

    //button for log in page
    private TextView mLogBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Auth = FirebaseAuth.getInstance();

        progressBar = new ProgressDialog(this);
        userName = findViewById(R.id.signup_username);
        Email = findViewById(R.id.signup_email);
        Password = findViewById(R.id.signup_password);


        //registers you an account
        mRegBtn = findViewById(R.id.signup_btn_txt);
        mRegBtnCard = findViewById(R.id.signup_btn);

        mRegBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String display_name = userName.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password))
                {
                    progressBar.setTitle("Registration");
                    progressBar.setMessage("Creating user... .. .");
                    progressBar.setCanceledOnTouchOutside(false);
                    progressBar.show();
                    registerUser(display_name,email,password);
                }
            }
        });

        mRegBtnCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String display_name = userName.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password))
                {
                    progressBar.setTitle("Registration");
                    progressBar.setMessage("Creating user... .. .");
                    progressBar.setCanceledOnTouchOutside(false);
                    progressBar.show();
                    registerUser(display_name,email,password);
                }

            }
        });

        //takes you to the log in page
        Tologin();



    }

    public void registerUser(final String display_name, String email, String password)
    {

        Auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    Database = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",display_name);
                    userMap.put("device_token",deviceToken);

                    Database.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {

                                progressBar.dismiss();
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = Auth.getCurrentUser();

                                Intent mainIntent = new Intent(RegisterActivity.this,HomeActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }

                        }
                    });



                }
                else
                {
                    progressBar.hide();
                    // If sign up fails, display a message to the user.
                    Toast.makeText(RegisterActivity.this,"Can not register, please check form and try again.", Toast.LENGTH_SHORT).show();

                }


            }
        });

    }

    public void Tologin()
    {
        //takes you to the log in page
        mLogBtn = findViewById(R.id.signup_login_btn);
        mLogBtn.setOnClickListener(new View.OnClickListener()
        {
            Intent log_intent = new Intent(RegisterActivity.this,SignInActivity.class);

            @Override
            public void onClick(View view)
            {
                startActivity(log_intent);
            }
        });
    }
}


