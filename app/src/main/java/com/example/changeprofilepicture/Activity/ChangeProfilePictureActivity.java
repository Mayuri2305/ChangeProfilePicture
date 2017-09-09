package com.example.changeprofilepicture.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.changeprofilepicture.CustomUI.CircularImageView;
import com.example.changeprofilepicture.Data.ProfilePicture;
import com.example.changeprofilepicture.DataBase.ProfilePictureTable;
import com.example.changeprofilepicture.R;
import com.example.changeprofilepicture.Utils.CameraPermissions;
import com.example.changeprofilepicture.Utils.ChangeProfileImage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChangeProfilePictureActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CAMERA_PERMISSION = 111;
    public static final int GALLERY_PERMISSION = 112;
    private static final String TAG = "ChangeProfilePictcure";
    public static int count = 0;
    private final String dir = Environment.getExternalStorageDirectory() + "/Profile/";
    Bitmap bitmap = null;
    ProfilePictureTable profilePictureTable;
    ProfilePicture profilePicture;
    private CircularImageView profileImage;
    private ImageView getProflieImage;


    private void init() {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        profilePictureTable = new ProfilePictureTable(this);
        profilePicture = new ProfilePicture();
        profileImage = (CircularImageView) findViewById(R.id.profile_imageview);
        getProflieImage = (ImageView) findViewById(R.id.get_proflie_imageview);
        List<ProfilePicture> profilePictures = profilePictureTable.getAllProfilePicture();
        if (profilePictures.size() > 0) {
            profilePicture = profilePictures.get(0);
            ChangeProfileImage.getInstance().setProfilePicture(profilePicture);
        }
        if (profilePicture.getImage() != null) {
            try {
                Log.d("decode", "Iamge: " + profilePicture.getImage());
                Bitmap myBitmapAgain = base64ToBitmap(profilePicture.getImage());
                profileImage.setImageBitmap(myBitmapAgain);

            } catch (Exception e) {
                profileImage.setImageResource(R.drawable.empty_profile_image);
                e.printStackTrace();

            }
        } else {
            profilePicture.setId(101);
            profileImage.setImageResource(R.drawable.empty_profile_image);
            profilePicture.setImage(" ");
            profilePictureTable.create(profilePicture);
        }
        getProflieImage.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);
        getSupportActionBar().hide();
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_proflie_imageview:
                getProfileImage();
                break;
            default:
                break;
        }

    }

    public void getProfileImage() {
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.gallery_alert_dailog, null);
        alertDialogBuilder.setView(dialogView);
        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(true);
        LinearLayout camera = (LinearLayout) dialogView.findViewById(R.id.linear_camera_content);
        LinearLayout gallery = (LinearLayout) dialogView.findViewById(R.id.linear_gallery_content);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfileImageFromCamera();
                alert.dismiss();

            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfileImageFromGallery();
                alert.dismiss();

            }
        });
        alert.show();


    }

    public void getProfileImageFromCamera() {
        CameraPermissions permissions = new CameraPermissions(this);
        if (!permissions.checkPermissionForCamera()) {
            permissions.requestPermissionForCamera();
        } else {
            if (!permissions.checkPermissionForExternalStorage()) {
                permissions.requestPermissionForExternalStorage();
            } else {
                count++;
                File file = new File(dir + count + ".jpg");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.fromFile(file);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(cameraIntent, CAMERA_PERMISSION);

            }
        }
    }

    public void getProfileImageFromGallery() {
        CameraPermissions permissions = new CameraPermissions(this);
        if (!permissions.checkPermissionForExternalRead()) {
            permissions.requestPermissionForExternalRead();
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY_PERMISSION);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CameraPermissions.CAMERA_PERMISSION_REQUEST_CODE || requestCode == CameraPermissions.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(ChangeProfilePictureActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(ChangeProfilePictureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    getProfileImageFromCamera();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PERMISSION) {
            try {
                bitmap = BitmapFactory.decodeFile(dir + count + ".jpg");
                profileImage.setImageBitmap(bitmap);
                String myBase64Image = bitmapToBase64(bitmap);
                profilePicture.setImage(resizeBase64Image(myBase64Image));
                profilePictureTable.update(profilePicture);
                Log.d(TAG, "Profile Image Changed from camera");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to update Image");
            }
        }
        if (resultCode == RESULT_OK && requestCode == GALLERY_PERMISSION) {
            try {
                Uri selectedImageUri = data.getData();
                String imagepath = getPath(selectedImageUri);
                bitmap = BitmapFactory.decodeFile(imagepath);
                profileImage.setImageBitmap(bitmap);
                String myBase64Image = bitmapToBase64(bitmap);
                profilePicture.setImage(resizeBase64Image(myBase64Image));
                profilePictureTable.update(profilePicture);
                Log.d(TAG, "Profile Image Changed from gallery" + imagepath);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to update Image");
            }
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private String bitmapToBase64(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    public static Bitmap base64ToBitmap(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String resizeBase64Image(String base64image) {
        byte[] encodeByte = Base64.decode(base64image.getBytes(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);


        if (image.getHeight() <= 400 && image.getWidth() <= 400) {
            return base64image;
        }
        image = Bitmap.createScaledBitmap(image, 100, 100, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] b = baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);

    }

}
