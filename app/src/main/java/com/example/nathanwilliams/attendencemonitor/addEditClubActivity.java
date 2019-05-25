package com.example.nathanwilliams.attendencemonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.IOException;
import java.util.HashMap;
import yuku.ambilwarna.AmbilWarnaDialog;

import static com.example.nathanwilliams.attendencemonitor.R.id.club_current_pic;


public class addEditClubActivity extends AppCompatActivity
{

    private RelativeLayout currentColor;
    private int pickedColor;
    private int defaultColor;

    private Boolean add = true;
    private Button addImage;
    private Button monday,tuesday,wednesday,thursday,friday,saturday,sunday;
    private boolean mon,tue,wed,thu,fri,sat,sun;
    private SelectableRoundedImageView Img;

    private TextView ClubName, ClubAgeRange, ClubLocation,ClubMentor,ClubTerm;

    private final int PICK_IMAGE_REQUEST = 71;


    private ProgressDialog addClubProgress;
    private DatabaseReference mDatabase;
    private String club;
    private String picture;
    private String uid;
    private String download_url = "default";
    private FloatingActionButton saveClub;
    private Uri resultUri;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private StorageReference mImageStorage;

    // global functions
    globalFunctions globalFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);

        globalFunctions = new globalFunctions(getApplicationContext());
        globalFunctions.centerTitle(this);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        uid = current_user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        saveClub = findViewById(R.id.club_save);
        addImage = findViewById(R.id.club_change_pic);
        monday = findViewById(R.id.club_monday);
        tuesday = findViewById(R.id.club_tuesday);
        wednesday = findViewById(R.id.club_wednesday);
        thursday = findViewById(R.id.club_thursday);
        friday = findViewById(R.id.club_friday);
        saturday = findViewById(R.id.club_saturday);
        sunday = findViewById(R.id.club_sunday);

        addClubProgress = new ProgressDialog(this);

        currentColor = findViewById(R.id.club_current_color);
        defaultColor = ContextCompat.getColor(addEditClubActivity.this,R.color.buttonGray);
        pickedColor = ContextCompat.getColor(addEditClubActivity.this,R.color.colorAccent);
        ImageButton colorPicker = findViewById(R.id.club_colorPicker);

        ClubName = findViewById(R.id.club_name);
        ClubAgeRange = findViewById(R.id.club_age_range);
        ClubLocation = findViewById(R.id.club_location);
        ClubMentor = findViewById(R.id.club_mentor);
        ClubTerm = findViewById(R.id.club_term);
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
            String termDays = intent.getStringExtra("days");
            String termSize = intent.getStringExtra("weeks");

            setTermDays(termDays);

            ClubTerm.setText(termSize);
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
                //uploadOrUpdateClub();
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
                    monday.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
                }
                else
                {
                    mon = true;
                    monday.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
                }
                break;
            case "tuesday":
                if(tue)
                {
                    tue = false;
                    tuesday.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
                }
                else
                {
                    tue = true;
                    tuesday.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
                }
                break;
            case "wednesday":
                if(wed)
                {
                    wed = false;
                    wednesday.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
                }
                else
                {
                    wed = true;
                    wednesday.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
                }
                break;
            case "thursday":
                if(thu)
                {
                    thu = false;
                    thursday.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
                }
                else
                {
                    thu = true;
                    thursday.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
                }
                break;
            case "friday":
                if(fri)
                {
                    fri = false;
                    friday.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
                }
                else
                {
                    fri = true;
                    friday.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
                }
                break;
            case "saturday":
                if(sat)
                {
                    sat = false;
                    saturday.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
                }
                else
                {
                    sat = true;
                    saturday.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
                }
                break;
            case "sunday":
                if(sun)
                {
                    sun = false;
                    sunday.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
                }
                else
                {
                    sun = true;
                    sunday.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
                }
                break;
            case "all":
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
        currentColor.setBackgroundColor(pickedColor);
        addImage.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
        saveClub.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
        ClubName.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
        ClubAgeRange.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
        ClubLocation.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
        ClubMentor.setBackgroundTintList(ColorStateList.valueOf(pickedColor));
        ClubTerm.setBackgroundTintList(ColorStateList.valueOf(pickedColor));

        if(mon) { monday.setBackgroundTintList(ColorStateList.valueOf(pickedColor)); }
        if(tue) { tuesday.setBackgroundTintList(ColorStateList.valueOf(pickedColor)); }
        if(wed) { wednesday.setBackgroundTintList(ColorStateList.valueOf(pickedColor)); }
        if(thu) { thursday.setBackgroundTintList(ColorStateList.valueOf(pickedColor)); }
        if(fri) { friday.setBackgroundTintList(ColorStateList.valueOf(pickedColor)); }
        if(sat) { saturday.setBackgroundTintList(ColorStateList.valueOf(pickedColor)); }
        if(sun) { sunday.setBackgroundTintList(ColorStateList.valueOf(pickedColor)); }
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
        resultUri = data.getData();

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            CropImage.activity(resultUri).setAspectRatio(1, 1).start(this);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                resultUri = result.getUri();

                try
                {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
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
        final StorageReference filepath = mImageStorage.child("club_profile").child(ClubName.getText().toString()+".jpg");

        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                    {

                        download_url = uri.toString();
                        uploadOrUpdateClub();
                    }
                });

            }
        });
    }

    private void uploadOrUpdateClub()
    {

        final HashMap<String,Object> clubMap = new HashMap<>();

        String name = ClubName.getText().toString();
        String age = ClubAgeRange.getText().toString();
        String location = ClubLocation.getText().toString();
        String mentor = ClubMentor.getText().toString();
        String termSize = ClubTerm.getText().toString();
        String hexColor = String.format("#%06X", (0xFFFFFF & pickedColor));

        if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(age) || !TextUtils.isEmpty(location) || !TextUtils.isEmpty(mentor))
        {
            clubMap.put("Name",name);
            clubMap.put("Color",hexColor);
            clubMap.put("Age",age);
            clubMap.put("Mentor",mentor);
            clubMap.put("Location",location);
            clubMap.put("Days",getTermDays());
            clubMap.put("Length",termSize);
            clubMap.put("Img",download_url);

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

    public String getTermDays()
    {
        String days = "";

        if(mon){days += "Mon "; };
        if(tue){days += "Tue "; };
        if(wed){days += "Wed "; };
        if(thu){days += "Thu "; };
        if(fri){days += "Fri "; };
        if(sat){days += "Sat "; };
        if(sun){days += "Sun "; };
        return days;
    }

    public void setTermDays(String days)
    {
        if(days.contains("Mon")) { mon = true; }
        if(days.contains("Tue")) { tue = true; }
        if(days.contains("Wed")) { wed = true; }
        if(days.contains("Thu")) { thu = true; }
        if(days.contains("Fri")) { fri = true; }
        if(days.contains("Sat")) { sat = true; }
        if(days.contains("Sun")) { sun = true; }
    }
}
