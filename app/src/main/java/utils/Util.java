package utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class Util {

    public static void setImage(ParseUser user, String parseKey, Resources resources, ImageView ivProfile, float cornerRadius) {
        ParseFile imageFile = null;
        try {
            imageFile = user.fetch().getParseFile(parseKey);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        }
    }

}
