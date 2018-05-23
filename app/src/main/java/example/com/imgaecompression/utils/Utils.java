package example.com.imgaecompression.utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by muhammad.sohail on 5/18/2018.
 */

public class Utils {


    public static final int MEDIA_TYPE_IMAGE = 1;

    public static File getOutputMediaFile(int type, String consignment) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "PICKUP_BOOKINGS");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                // Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " +
                // IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + consignment + ".JPG");
        } else {
            return null;
        }

        return mediaFile;
    }
}
