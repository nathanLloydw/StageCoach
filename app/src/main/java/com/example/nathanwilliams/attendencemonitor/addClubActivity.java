package com.example.nathanwilliams.attendencemonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.NotificationCompatSideChannelService;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.joooonho.SelectableRoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.example.nathanwilliams.attendencemonitor.R.id.club_current_pic;

public class addClubActivity extends AppCompatActivity
{

    private RelativeLayout curentColor;
    private int defaultColor;

    private Button addImage;
    private ImageButton colorPicker;
    private SelectableRoundedImageView Img;

    private TextView ClubName, ClubAgeRange, ClubLocation,ClubMentor;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    private TextView addOrEdit;
    private ProgressDialog addClubProgress;
    private DatabaseReference mDatabase;
    private FirebaseUser current_user;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);
        centerTitle();

        Intent intent = getIntent();
        String GetaddOrEdit = intent.getStringExtra("add/edit");

        addOrEdit = findViewById(R.id.club_add_or_edit);
        addOrEdit.setText(GetaddOrEdit);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = current_user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        addImage = findViewById(R.id.club_change_pic);

        addClubProgress = new ProgressDialog(this);

        curentColor = findViewById(R.id.club_current_color);
        defaultColor = ContextCompat.getColor(addClubActivity.this,R.color.colorAccent);
        colorPicker = findViewById(R.id.club_colorPicker);

        colorPicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                openColorPicker();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chooseImage();

            }
        });

        FloatingActionButton saveClub = findViewById(R.id.club_save);
        saveClub.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addClubProgress.setTitle("Creating Club");
                addClubProgress.setMessage("");
                addClubProgress.setCanceledOnTouchOutside(false);
                addClubProgress.show();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Clubs");
                uploadImage();



            }
        });
    }

    public void openColorPicker()
    {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog)
            {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color)
            {
               defaultColor = color;
               curentColor.setBackgroundColor(color);
            }
        });
        colorPicker.show();
    }

    private void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "pick an image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filePath = data.getData();
            CropImage.activity(filePath).setAspectRatio(1, 1).start(this);

        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                try
                {
                    filePath = result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                    Img = findViewById(club_current_pic);
                    Img.setImageBitmap(bitmap);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage()
    {
        final String ImgID = "images/" + UUID.randomUUID().toString();
        final StorageReference ref = storageReference.child(ImgID);


        if(filePath != null)
        {
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {

                                    HashMap<String,String> clubMap = new HashMap<>();
                                    String ImageLink = uri.toString();

                                    if(ImageLink != null)
                                    {
                                        clubMap.put("Img",ImageLink);
                                    }
                                    else
                                    {
                                        clubMap.put("Img","default");
                                    }

                                    ClubName = findViewById(R.id.club_name);
                                    ClubAgeRange = findViewById(R.id.club_age_range);
                                    ClubLocation = findViewById(R.id.club_location);
                                    ClubMentor = findViewById(R.id.club_mentor);
                                    Img = findViewById(R.id.club_current_pic);



                                    String name = ClubName.getText().toString();
                                    String age = ClubAgeRange.getText().toString();
                                    String location = ClubLocation.getText().toString();
                                    String mentor = ClubMentor.getText().toString();

                                    if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(age) || !TextUtils.isEmpty(location) || !TextUtils.isEmpty(mentor))
                                    {
                                        String hexColor = String.format("#%06X", (0xFFFFFF & defaultColor));



                                        clubMap.put("Name",name);
                                        clubMap.put("Color",hexColor);
                                        clubMap.put("Age",age);
                                        clubMap.put("Mentor",mentor);
                                        clubMap.put("Location",location);


                                        mDatabase.push().setValue(clubMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    addClubProgress.dismiss();

                                                    Intent mainIntent = new Intent(addClubActivity.this,HomeActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();

                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        addClubProgress.hide();
                                        Toast.makeText(addClubActivity.this,"Please make sure all fields are filled in and try again.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception exception)
                                {

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(addClubActivity.this, "Image Upload Failed "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        }
                    });

        }
    }


    private void centerTitle()
    {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0)
        {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1)
            {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            }
            else
            {
                for(View v : textViews)
                {
                    if(v.getParent() instanceof Toolbar)
                    {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null)
            {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

}
