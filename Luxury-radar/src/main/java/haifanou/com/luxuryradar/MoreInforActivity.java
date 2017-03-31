package haifanou.com.luxuryradar;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MoreInforActivity extends Activity implements View.OnClickListener{

    private final static int CROP_REQUEST=3;
    public static final String PYTHON_SERVER_ADDRESS = "http://huangwc94.pythonanywhere.com";
    private static String shirtUrl;
    private static boolean crop=false;

    private Spinner genderSpinner, styleSpinner;
    private String mImageFileLocation;
    private ImageView imageView;
    private Uri imageUri;
    private Uri cropImageUri;
    private String encoded_string;
    private Bitmap bitmap;
    private Button btnSubmit, btnCrop;
    private Intent viewResultIntent;
    private Encode_image mEncode_image=new Encode_image();

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

    //method to check if network is available to use
    private boolean checkNetWorkAvailability(){
        ConnectivityManager connectivityManager	=
                (ConnectivityManager)	getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo	=
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected	=	true;
        boolean isWifiAvailable	=	networkInfo.isAvailable();
        boolean isWifiConnected	=	networkInfo.isConnected();
        networkInfo	=
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileAvailable	=	networkInfo.isAvailable();
        boolean isMobileConnnected	=	networkInfo.isConnected();
        isConnected	=	(isMobileAvailable&&isMobileConnnected)	||
                (isWifiAvailable&&isWifiConnected);
        return(isConnected);
    }


    public void addItemsOnSpinner() {

        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        List<String> list = new ArrayList<>();
        list.add("men");
        list.add("women");
        list.add("men&women");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                if(checkNetWorkAvailability()) {
                    submitForResult();
                }else{
                    Toast.makeText(this, "Network is unavailable, please connect your device to network before clicking submit", Toast.LENGTH_LONG).show();
                }
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

            cropImageUri = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            crop=true;
            startActivityForResult(cropIntent, CROP_REQUEST);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == CROP_REQUEST && resultCode == RESULT_OK) {
            try {
                crop=true;
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
                imageView.setImageBitmap(bitmap);
            }catch(FileNotFoundException fe){
                fe.printStackTrace();
            }
        }
    }

    private void submitForResult(){
        mEncode_image.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEncode_image.cancel(true);

    }

    private class Encode_image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(crop) {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
                }else{
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitmap.recycle();

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            makeRequest();
        }
    }

    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, PYTHON_SERVER_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String jsonData = response.toString();
                            JSONObject Jobject = new JSONObject(jsonData);
                            shirtUrl =Jobject.getString("url");
                            viewResultIntent=new Intent(MoreInforActivity.this, ViewResultActivity.class);
                            viewResultIntent.putExtra("shirtUrl", shirtUrl);
                            mEncode_image.cancel(true);
                            startActivity(viewResultIntent);

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("encoded_string",encoded_string);

                return map;
            }
        };
        requestQueue.add(request);
    }



}
