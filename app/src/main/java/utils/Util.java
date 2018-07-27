package utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static void setImage(ParseUser user, ParseFile imageFile, Resources resources, ImageView ivProfile, float cornerRadius) {
//        ParseFile imageFile = null;
//        try {
//            imageFile = user.fetch().getParseFile(parseKey);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        if (imageFile != null) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(imageFile.getFile().getAbsolutePath());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(resources, bitmap);
            roundedBitmapDrawable.setCornerRadius(cornerRadius);
            roundedBitmapDrawable.setAntiAlias(true);
            ivProfile.setImageDrawable(roundedBitmapDrawable);
        } else {
            ivProfile.setImageDrawable(resources.getDrawable(R.drawable.placeholder_profile));
        }
    }

    public static void setRequests(ParseUser user, NavigationView navigationView) {
        ParseQuery<SentFriendRequests> query2 = ParseQuery.getQuery("SentFriendRequests");
        query2.whereEqualTo("toUser", user);
        try {
            int count = query2.count();
            if(count > 0) {
                navigationView.getMenu().getItem(3).setTitle("friend requests (" + count + ")");
            } else {
                navigationView.getMenu().getItem(3).setTitle("friend requests");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<SentFriendRequests> query3 = ParseQuery.getQuery("GoalRequests");
        query3.whereEqualTo("user", user);
        try {
            int count = query3.count();
            if(count > 0) {
                navigationView.getMenu().getItem(4).setTitle("goal requests (" + count + ")");
            } else {
                navigationView.getMenu().getItem(4).setTitle("goal requests");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static void populateStoredGoals(Context context, ParseUser user, final TextView tvProgress, final TextView tvCompleted, final TextView tvFriends, TextView tvUsername, ImageView ivProfile, final List<Goal> goals, final List<Goal> incompleted) {
        ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("Goal");
        localQuery.fromLocalDatastore();
        localQuery.whereEqualTo("user", user);
        localQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    int completedGoals = 0;
                    int progressGoals = 0;
                    goals.clear();
                    for (int i=0; i <objects.size(); i++){
                        Goal goal = (Goal) objects.get(i);

                        if (goal.getCompleted()) {
                            completedGoals += 1;
                            goals.add(goal);
                        } else {
                            progressGoals += 1;
                            goals.add(0, goal);
                            incompleted.add(0, goal);
                        }
                    }
                    tvProgress.setText(String.valueOf(progressGoals));
                    tvCompleted.setText(String.valueOf(completedGoals));
                }
            }
        });
        ParseQuery<ParseUser> localUserQuery = ParseUser.getQuery();
        localUserQuery.fromLocalDatastore();
        localUserQuery.whereNotEqualTo("objectId", user.getObjectId());
        localUserQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                tvFriends.setText(String.valueOf(objects.size()));
            }
        });

        tvUsername.setText(user.getUsername());
        //tvFriends.setText(String.valueOf(user.getList("friends").size()));
    }

    public static void populateGoals(Context context, ParseUser user, TextView tvProgress, TextView tvCompleted, TextView tvFriends, TextView tvUsername, ImageView ivProfile, List<Goal> goals, List<Goal> incompleted) {
        List<ParseObject> lGoals = user.getList("goals");
        int completedGoals = 0;
        int progressGoals = 0;
        goals.clear();
        List<Goal> completed = new ArrayList<>();
        if (lGoals != null) {
            for (int i = 0; i < lGoals.size(); i++) {
                Goal g = (Goal) lGoals.get(i);
                if (g.getCompleted()) {
                    completedGoals += 1;
                    completed.add(0, g);
                } else {
                    progressGoals += 1;
                    goals.add(0, g);
                    incompleted.add(0, g);
                }
            }
            goals.addAll(completed);
        }

        tvProgress.setText(String.valueOf(progressGoals));
        tvCompleted.setText(String.valueOf(completedGoals));
        tvFriends.setText(String.valueOf(user.getList("friends").size()));
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        ParseFile pfile = (ParseFile) user.get("image");
        setImage(user, pfile, context.getResources(), ivProfile, 16.0f);
    }

    public static void populateGoalsAsync(Context context, ParseUser user, TextView tvProgress, TextView tvCompleted, TextView tvFriends, TextView tvUsername, ImageView ivProfile, List<Goal> goals, List<Goal> incompleted, SwipeRefreshLayout swipe) {
        List<ParseObject> lGoals = user.getList("goals");
        int completedGoals = 0;
        int progressGoals = 0;
        goals.clear();
        List<Goal> completed = new ArrayList<>();
        if (lGoals != null) {
            for (int i = 0; i < lGoals.size(); i++) {
                Goal g = (Goal) lGoals.get(i);
                if (g.getCompleted()) {
                    completedGoals += 1;
                    completed.add(0, g);
                } else {
                    progressGoals += 1;
                    goals.add(0, g);
                    incompleted.add(0, g);
                }
            }
            goals.addAll(completed);
            swipe.setRefreshing(false);
        }

        tvProgress.setText(String.valueOf(progressGoals));
        tvCompleted.setText(String.valueOf(completedGoals));
        tvFriends.setText(String.valueOf(user.getList("friends").size()));
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        ParseFile pfile = (ParseFile) user.get("image");
        setImage(user, pfile, context.getResources(), ivProfile, 16.0f);
    }

    public static void setImageBitmap(Bitmap bitmap, Context context, ImageView ivProfile) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(16.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        ivProfile.setImageDrawable(roundedBitmapDrawable);
    }

    // Returns the File for a photo stored on disk given the fileName
    public static File getPhotoFileUri(String fileName, Context context) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Goals");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("Goals", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    public static void onProfile(final ParseUser user, ParseFile imageFile, final Context context) {
        user.put("image", imageFile);
        ParseACL acl = user.getACL();
        if (!acl.getPublicReadAccess()) {
            acl.setPublicReadAccess(true);
            user.setACL(acl);
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        user.fetch();
                        Toast.makeText(context, "Successfully updated profile image!", Toast.LENGTH_LONG);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("Profile Activity", "Failed to update object, with error code: " + e.toString());
                }
            }
        });
    }

    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.fbu.fileprovider".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.fbu.fileprovider".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.fbu.fileprovider".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.fbu.fileprovider".equals(uri
                .getAuthority());
    }

}
