package haifanou.com.luxuryradar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CamaraIntentActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "CAM_Activity";
    private static final int CAM_REQUEST = 0;
    private static final int GALL_REQUEST = 1;
    private static boolean fromCam=false;  //flag to save if the imageView is from Camera or Gallery

    private Button nextBtn;
    private ImageView photoImageView;
    private String mImageFileLocation = "";
    private Uri imageUri;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara_intent);
        nextBtn = (Button) findViewById(R.id.nextButton);
        nextBtn.setEnabled(false);
        nextBtn.setOnClickListener(this);
        photoImageView = (ImageView) findViewById(R.id.imageGalleryView);

    }

    /*
    function to save data (imageView)
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(photoImageView.getDrawable() != null) {
            savedInstanceState.putBoolean("hasImage", true);
            if (fromCam) {
                savedInstanceState.putString("imageFileLocation", mImageFileLocation);
            } else {
                savedInstanceState.putString("imageUri", imageUri.toString());
            }
        }
    }

    /*
    function to restore imageView
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean("hasImage")) {
            if (fromCam) {
                mImageFileLocation = savedInstanceState.getString("imageFileLocation");
                File file = new File(mImageFileLocation);
                imageUri = Uri.fromFile(file);
            } else {
                imageUri = Uri.parse(savedInstanceState.getString("imageUri"));
            }
            photoImageView.setImageURI(imageUri);
            nextBtn.setEnabled(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextButton:
                Intent moreInforIntent = new Intent(this, MoreInforActivity.class);
                moreInforIntent.putExtra("FromCamOrNot", fromCam);
                if(fromCam) {
                    moreInforIntent.putExtra("imageFileLocation", mImageFileLocation);
                }else{
                    moreInforIntent.putExtra("imageUri", imageUri.toString());
                }
                startActivity(moreInforIntent);
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camara_intent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.btn_camera) takePhoto();
        else if(item.getItemId()==R.id.btn_gallery) openGallery();
        return super.onOptionsItemSelected(item);
    }

    private void openGallery() {
        fromCam = false;
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALL_REQUEST);
    }

    public void takePhoto() {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        startActivityForResult(callCameraApplicationIntent, CAM_REQUEST);
        fromCam=true;
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == CAM_REQUEST && resultCode == RESULT_OK) {
            Bitmap photoReducedSizeBitmp = BitmapFactory.decodeFile(mImageFileLocation);
            photoImageView.setImageBitmap(photoReducedSizeBitmp);
            nextBtn.setEnabled(true);
        } else if(requestCode== GALL_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            photoImageView.setImageURI(imageUri);
            nextBtn.setEnabled(true);
        }

    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName,".jpg", storageDirectory);
        mImageFileLocation = image.getAbsolutePath();

        return image;

    }


}
