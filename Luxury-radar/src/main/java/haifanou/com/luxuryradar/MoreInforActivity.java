package haifanou.com.luxuryradar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class MoreInforActivity extends Activity implements View.OnClickListener{

    private final static int CROP_REQUEST=3;
    private Spinner genderSpinner, styleSpinner;
    private String mImageFileLocation;
    private ImageView imageView;
    private Uri imageUri;
    private Uri uritempFile;
    private Button btnSubmit, btnCrop;
    private DatabaseHelper db = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_infor);
        imageView=(ImageView) findViewById(R.id.imageGalleryView);
        if(getIntent().getExtras().getBoolean("FromCamOrNot")) {
            mImageFileLocation=getIntent().getExtras().getString("imageFileLocation");
            File file = new File(mImageFileLocation);
            imageUri=Uri.fromFile(file);
        }else{
            imageUri=Uri.parse(getIntent().getExtras().getString("imageUri"));
        }
        imageView.setImageURI(imageUri);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        addItemsOnSpinner();

        btnSubmit =(Button) findViewById(R.id.submitBtn);
        btnSubmit.setOnClickListener(this);
        btnCrop = (Button) findViewById(R.id.cropBtn);
        btnCrop.setOnClickListener(this);

    }

    public void addItemsOnSpinner() {

        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        List<String> list = new ArrayList<>();
        list.add("men");
        list.add("women");
        list.add("men&women");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(    android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(dataAdapter);

        styleSpinner= (Spinner) findViewById(R.id.styleSpinner);
        List<String> list2 = new ArrayList<>();
        list2.add("shirt");
        list2.add("tee");
        list2.add("jacket");
        list2.add("coat");
        list2.add("pants");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleSpinner.setAdapter(dataAdapter2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitBtn:
                submitForResult();
                break;
            case R.id.cropBtn:
                cropImage();
                break;
        }
    }

    private void cropImage(){
        try{
            Intent cropIntent=new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(imageUri, "image/*");
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("outputX", 100);
            cropIntent.putExtra("outputY", 100);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("scaleUpIfNeeded", true);
            cropIntent.putExtra("aspectX", 3);
            cropIntent.putExtra("aspectY", 4);

            uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            startActivityForResult(cropIntent, CROP_REQUEST);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == CROP_REQUEST && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                imageView.setImageBitmap(bitmap);
            }catch(FileNotFoundException fe){
                fe.printStackTrace();
            }
        }
    }

    private void submitForResult(){
        Intent viewResultIntent = new Intent(this, ViewResultActivity.class);
        startActivity(viewResultIntent);
    }

}
