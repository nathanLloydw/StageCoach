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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignInActivity extends AppCompatActivity {

    //user details for log in
    private EditText Email, Password;

    //firebase authentication
    private FirebaseAuth Auth;
    //database connection
    private DatabaseReference UserDatabase;

    //progress bar
    private ProgressDialog LoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //firebase authentication and database connection instantiation
        Auth = FirebaseAuth.getInstance();
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        //connect layout fields to this activity
        Email = findViewById(R.id.login_email);
        Password = findViewById(R.id.login_password);

        CardView mLogBtn = findViewById(R.id.login_btn);
        TextView mLogTxt = findViewById(R.id.login_btn_txt);
        TextView mRegBtn = findViewById(R.id.login_register);

        //create progress bar in preparation
        LoginProgress = new ProgressDialog(this);

        mLogTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) { toLogon(); }
        });

        mLogBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) { toLogon(); }
        });

        mRegBtn.setOnClickListener(new View.OnClickListener()
        {
            Intent reg_intent = new Intent(SignInActivity.this,RegisterActivity.class);
            @Override
            public void onClick(View view)
            {
                startActivity(reg_intent);
            }
        });
    }

    public void toLogon()
    {
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

        if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password))
        {

            LoginProgress.setTitle("loging In");
            LoginProgress.setMessage("Keep Calm alan!");
            LoginProgress.setCanceledOnTouchOutside(false);
            LoginProgress.show();
            loginUser(email,password);
        }
        else
        {
            Toast.makeText(SignInActivity.this,"please make sure the username and email are filled in correctly.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginUser(String email, String password)
    {
        //instantiate log in with firebase servers
        Auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    //logging in was successful
                    LoginProgress.dismiss();

                    String current_user_id = Auth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    UserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Intent mainIntent = new Intent(SignInActivity.this,HomeActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                    });
                }
                else
                {
                    //log in failed
                    LoginProgress.hide();
                    Toast.makeText(SignInActivity.this,"Can not login, please check form and try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




}
