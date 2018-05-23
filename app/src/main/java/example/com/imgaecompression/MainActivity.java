package example.com.imgaecompression;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.imgaecompression.utils.ImageCompression;
import example.com.imgaecompression.utils.Utils;

public class MainActivity extends AppCompatActivity {
    Button takeCompressPhoto, takeUnCompressPhoto;

    ImageView withoutCompressImg, withCompressImg;

    public static String imageUrlMain = "";

    private final int requestCode = 20;

    public static final int MEDIA_TYPE_IMAGE = 1;
    String path;
    Uri photoURI = null;


    boolean photoClick = false;

    private ImageCompression imageCompression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takeCompressPhoto = findViewById(R.id.takeCompressPhoto);
        takeUnCompressPhoto = findViewById(R.id.takeUnCompressPhoto);

        withoutCompressImg = findViewById(R.id.withoutCompressImg);
        withCompressImg = findViewById(R.id.withCompressImg);

        imageCompression = new ImageCompression();

        takeUnCompressPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoClick = true;
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, requestCode);
            }
        });

        takeCompressPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoClick = false;
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                try {
                    File photoFile = Utils.getOutputMediaFile(MEDIA_TYPE_IMAGE, "22"/*editConsignmentNo.getText().toString()*/);
                    imageUrlMain = photoFile.getAbsolutePath();
                    path = photoFile.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        photoURI = FileProvider.getUriForFile(MainActivity.this,
                                getString(R.string.file_provider_authority),
                                photoFile);
                    } else {
                        photoURI = Uri.fromFile(photoFile);
                    }

                    Log.d("Image", photoURI.toString());
                } catch (Exception ex) {
                    Log.e("TakePicture", ex.getMessage());
                }
                photoCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(photoCaptureIntent, requestCode);
            }
        });


//        takePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                try {
//                    File photoFile = Utils.getOutputMediaFile(MEDIA_TYPE_IMAGE, "22"/*editConsignmentNo.getText().toString()*/);
//                    imageUrlMain = photoFile.getAbsolutePath();
//                    path = photoFile.getAbsolutePath();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        photoURI = FileProvider.getUriForFile(MainActivity.this,
//                                getString(R.string.file_provider_authority),
//                                photoFile);
//                    } else {
//                        photoURI = Uri.fromFile(photoFile);
//                    }
//
//                    Log.d("Image", photoURI.toString());
//                } catch (Exception ex) {
//                    Log.e("TakePicture", ex.getMessage());
//                }
//                photoCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(photoCaptureIntent, requestCode);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.requestCode == requestCode && resultCode == RESULT_OK) {

            if (photoClick) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                String partFilename = currentDateFormat();
                storeCameraPhotoInSDCard(bitmap, partFilename);

                // display the image from SD Card to ImageView Control
                String storeFilename = "photo_" + partFilename + ".jpg";
                Bitmap mBitmap = getImageFileFromSDCard(storeFilename);
                withoutCompressImg.setImageBitmap(mBitmap);
            } else {
                new CompressImage().execute();
            }
        }
    }


    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate) {
        File outputFile = new File(Environment.getExternalStorageDirectory(), "photo_" + currentDate + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImageFileFromSDCard(String filename) {
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() + "/" + filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }


    private class CompressImage extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //if (!(getActivity().isFinishing())) {
            //   progressDialogControllerPleaseWait.showDialog();
            //}
        }

        @Override
        protected String doInBackground(Void... params) {
            Bitmap bitmap = imageCompression.getCompressedBitmapMoreOptimized(imageUrlMain, MainActivity.this);
            if (bitmap != null) {
                String newPath = savePhoto(bitmap);
                bitmap.recycle();
                return newPath;
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // progressDialogControllerPleaseWait.hideDialog();
            try {
                File newFile = new File(s);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoURI = FileProvider.getUriForFile(MainActivity.this,
                            getString(R.string.file_provider_authority),
                            newFile);
                } else {
                    photoURI = Uri.fromFile(newFile);
                }
//                booking.setImage(utils.uriToByteArr(photoURI));


            } catch (Exception ex) {
                //   toastUtil.showToastLongTime(ex.getMessage());
                ex.printStackTrace();
                return;
            }
        }
    }


    public String savePhoto(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            File imageFile = Utils.getOutputMediaFile(MEDIA_TYPE_IMAGE, "22");
            out = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
          //  withCompressImg.setImageBitmap(bmp);
            out.flush();
            out.close();
            imageCompression.refreshGallery(imageFile.getAbsolutePath(), MainActivity.this);
            out = null;
            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


}
