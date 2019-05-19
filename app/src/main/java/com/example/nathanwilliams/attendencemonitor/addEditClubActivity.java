package com.example.nathanwilliams.attendencemonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.example.nathanwilliams.attendencemonitor.R.id.club_current_pic;
import static com.example.nathanwilliams.attendencemonitor.R.id.club_location;

public class addEditClubActivity extends AppCompatActivity
{

    private RelativeLayout curentColor;
    private int pickedColor = 1620180;
    private int defaultColor;

    private Boolean add = true;
    private Button addImage;
    private Button monday,tuesday,wednesday,thursday,friday,saturday,sunday;
    private boolean mon,tue,wed,thu,fri,sat,sun;
    private ImageButton colorPicker;
    private SelectableRoundedImageView Img;

    private TextView ClubName, ClubAgeRange, ClubLocation,ClubMentor,clubSize;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri resultUri;

    private ProgressDialog addClubProgress;
    private DatabaseReference mDatabase;
    private FirebaseUser current_user;
    private String club;
    private String hexColor;
    private String picture;
    private String uid;
    private String download_url;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private StorageReference mImageStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);
        centerTitle();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        uid = current_user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        addImage = findViewById(R.id.club_change_pic);
        monday = findViewById(R.id.club_monday);
        tuesday = findViewById(R.id.club_tuesday);
        wednesday = findViewById(R.id.club_wednesday);
        thursday = findViewById(R.id.club_thursday);
        friday = findViewById(R.id.club_friday);
        saturday = findViewById(R.id.club_saturday);
        sunday = findViewById(R.id.club_sunday);

        addClubProgress = new ProgressDialog(this);

        curentColor = findViewById(R.id.club_current_color);
        defaultColor = ContextCompat.getColor(addEditClubActivity.this,R.color.colorAccent);
        colorPicker = findViewById(R.id.club_colorPicker);

        ClubName = findViewById(R.id.club_name);
        ClubAgeRange = findViewById(R.id.club_age_range);
        ClubLocation = findViewById(R.id.club_location);
        ClubMentor = findViewById(R.id.club_mentor);
        clubSize = findViewById(R.id.club_term);
        Img = findViewById(R.id.club_current_pic);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        String GetAddOrEdit = intent.getStringExtra("add/edit");

        if(GetAddOrEdit.contains("edit"))
        {
            add = false;
            club = intent.getStringExtra("name");
            String age = intent.getStringExtra("age");
            String location = intent.getStringExtra("location");
            picture = intent.getStringExtra("imgURL");

            String color = intent.getStringExtra("color");
            pickedColor = Color.parseColor(color);

            String mentor = intent.getStringExtra("mentor");

            ClubName.setText(club);
            ClubAgeRange.setText(age);
            ClubLocation.setText(location);
            Picasso.get().load(picture).placeholder(R.drawable.avatar).into(Img);
            updateColors();


            ClubMentor.setText(mentor);
        }

        colorPicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) { openColorPicker(); }
        });

        addImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chooseImage();

            }
        });

        monday.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { day("monday"); }});
        tuesday.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { day("tuesday"); }});
        wednesday.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { day("wednesday"); }});
        thursday.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { day("thursday"); }});
        friday.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { day("friday"); }});
        saturday.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { day("saturday"); }});
        sunday.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { day("sunday"); }});

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

                uploadOrUpdateClub();
            }
        });
    }

    public void day(String day)
    {
        switch(day)
        {
            case "monday":
                if(mon)
                {
                    mon = false;
                    //monday.setBackgroundColor(defaultColor);
                    monday.setBackgroundColor(Color.BLUE);
                    System.out.println("COLOR OFF, COLOR: "+pickedColor);
                }
                else
                {
                    mon = true;
                    //monday.setBackgroundColor(pickedColor);
                    monday.setBackgroundColor(Color.GREEN);
                    System.out.println("COLOR ON, COLOR: "+pickedColor);
                }
                break;
            case "tuesday":
                if(tue)
                {
                    tue = false;
                    tuesday.setBackgroundColor(defaultColor);
                }
                else
                {
                    tue = true;
                    tuesday.setBackgroundColor(pickedColor);
                }
                break;
            case "wednesday":
                if(wed)
                {
                    wed = false;
                    wednesday.setBackgroundColor(defaultColor);
                }
                else
                {
                    wed = true;
                    wednesday.setBackgroundColor(pickedColor);
                }
                break;
            case "thursday":
                if(thu)
                {
                    thu = false;
                    thursday.setBackgroundColor(defaultColor);
                }
                else
                {
                    thu = true;
                    thursday.setBackgroundColor(pickedColor);
                }
                break;
            case "friday":
                if(fri)
                {
                    fri = false;
                    friday.setBackgroundColor(defaultColor);
                }
                else
                {
                    fri = true;
                    friday.setBackgroundColor(pickedColor);
                }
                break;
            case "saturday":
                if(sat)
                {
                    sat = false;
                    saturday.setBackgroundColor(defaultColor);
                }
                else
                {
                    sat = true;
                    saturday.setBackgroundColor(pickedColor);
                }
                break;
            case "sunday":
                if(sun)
                {
                    sun = false;
                    sunday.setBackgroundColor(defaultColor);
                }
                else
                {
                    sun = true;
                    sunday.setBackgroundColor(pickedColor);
                }
                break;
        }


    }

    public void openColorPicker()
    {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, pickedColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog)
            {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color)
            {
               pickedColor = color;
               updateColors();
               //curentColor.setBackgroundColor(color);
               //hexColor = String.format("#%06X", (0xFFFFFF & defaultColor));
            }
        });
        colorPicker.show();
    }
    public void updateColors()
    {
        System.out.println("coloooorrrrrrrr is : "+pickedColor);
        curentColor.setBackgroundColor(pickedColor);
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
            resultUri = data.getData();
            CropImage.activity(resultUri).setAspectRatio(1, 1).start(this);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                try
                {
                    resultUri = result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

                    Img = findViewById(club_current_pic);
                    Img.setImageBitmap(bitmap);

                    final StorageReference filepath = mImageStorage.child("club_profile").child(uid+".jpg");

                    filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    download_url = uri.toString();
                                }
                            });
                        }
                    });


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

    private void uploadOrUpdateClub()
    {
        final HashMap<String,Object> clubMap = new HashMap<>();

        if(download_url != null)
        {
            clubMap.put("Img",download_url);
        }
        else
        {
            if(picture != null)
            {
                clubMap.put("Img",picture);
            }
            else
            {
                clubMap.put("Img","default");
            }

        }

        String name = ClubName.getText().toString();
        String age = ClubAgeRange.getText().toString();
        String location = ClubLocation.getText().toString();
        String mentor = ClubMentor.getText().toString();
        String hexColor = String.format("#%06X", (0xFFFFFF & pickedColor));

        if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(age) || !TextUtils.isEmpty(location) || !TextUtils.isEmpty(mentor))
        {
            clubMap.put("Name",name);

            clubMap.put("Color",hexColor);
            clubMap.put("Age",age);
            clubMap.put("Mentor",mentor);
            clubMap.put("Location",location);

            if(add)
            {
                mDatabase.push().setValue(clubMap).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            addClubProgress.dismiss();

                            Intent mainIntent = new Intent(addEditClubActivity.this,HomeActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                    }
                });
            }
            else
            {
                Query editQuery = mDatabase.orderByChild("Name").equalTo(club);
                editQuery.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot editSnapshot: dataSnapshot.getChildren())
                        {
                            DatabaseReference clubRef = editSnapshot.getRef();
                            clubRef.updateChildren(clubMap).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        addClubProgress.dismiss();

                                        Intent mainIntent = new Intent(addEditClubActivity.this,HomeActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }

                });
            }
        }
        else
        {
            addClubProgress.hide();
            Toast.makeText(addEditClubActivity.this,"Please make sure all fields are filled in and try again.", Toast.LENGTH_SHORT).show();
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
