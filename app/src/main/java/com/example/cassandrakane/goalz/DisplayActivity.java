package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.Image;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayActivity extends AppCompatActivity {

    File file;
    Bitmap image;
    String cameraId;
    @BindView(R.id.ivImage) ImageView ivImage;
    @BindView(R.id.btnConfirm) ImageView btnConfirm;
    List<Goal> goals;
    Goal goal;
    Date currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        currentDate = new Date();

        ButterKnife.bind(this);

        if (getIntent().getParcelableExtra(Goal.class.getSimpleName()) != null){
            goal = Parcels.unwrap(getIntent().getParcelableExtra(Goal.class.getSimpleName()));
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ParseFile parseFile = new ParseFile(file);
                    parseFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                Log.d("GoalsListActivity", "ParseFile has been saved");
                            } else{
                                e.printStackTrace();
                            }
                        }
                    });
                    final ArrayList<ParseObject> story = goal.getStory();
                    final Image image = new Image(parseFile, "", goal);
                    image.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            story.add(image);
                            goal.setStory(story);
                            goal.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                                    notificationHelper.cancelReminder(goal);
                                    notificationHelper.setReminder(goal);
                                    if (goal.getStory().size() == 1){
                                        goal.setProgress(1);
                                        goal.setStreak(1);
                                        goal.setItemAdded(false);
                                        goal.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Intent intent = new Intent(DisplayActivity.this, ProfileActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        if (!goal.getIsItemAdded()) {
                                            goal.setProgress(goal.getProgress() + 1);
                                            if (currentDate.getTime() <= goal.getUpdateStoryBy().getTime()) {
                                                goal.setItemAdded(true);
                                                goal.setStreak(goal.getStreak() + 1);
                                                goal.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        Intent intent = new Intent(DisplayActivity.this, ProfileActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        } else {
                                            Intent intent = new Intent(DisplayActivity.this, ProfileActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            });
        } else {
            goal = null;
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DisplayActivity.this, GoalsListActivity.class);
                    intent.putExtra("image", file);
                    intent.putExtra("goals", (Serializable) goals);
                    startActivity(intent);
                }
            });
        }

        goals = (List) getIntent().getSerializableExtra("goals");
        cameraId = getIntent().getStringExtra("cameraId");
        file = (File) getIntent().getSerializableExtra("image");
        image = BitmapFactory.decodeFile(file.getAbsolutePath());
        image = rotateBitmapOrientation(file.getAbsolutePath());

        if (cameraId.equals("1")) {
            try {
                //create a file to write bitmap data
                file = new File(Environment.getExternalStorageDirectory()+"/"+ UUID.randomUUID().toString()+"1.jpg");

                    file.createNewFile();

                //Convert bitmap to byte array
                Bitmap bitmap = image;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(image);
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        if (cameraId.equals("1")){
            rotationAngle += 180;
        }
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    public void goBack(View v) {
        finish();
    }
}
