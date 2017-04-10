package haifanou.com.luxuryradar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.net.URL;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

public class ViewResultActivity extends Activity {

    private Button newSearchBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);
        newSearchBtn=(Button) findViewById(R.id.newSearchBtn);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final String clothsId = getIntent().getExtras().getString("id");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://api.shopstyle.com/api/v2/products/"+clothsId+"?pid=uid7616-38024704-23";

        final TextView name = (TextView) findViewById(R.id.Name);
        final TextView brand = (TextView) findViewById(R.id.Brand);
        final TextView price = (TextView) findViewById(R.id.Price);
        final WebView img = (WebView) findViewById(R.id.image);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Display the first 500 characters of the response string.
                        try{
                            JSONObject Jobject = new JSONObject(response);
                            name.setText(Jobject.getString("name"));
                            brand.setText(Jobject.getString("unbrandedName"));
                            price.setText("$" + Jobject.getString("price"));
                            String imgurl = Jobject.getJSONObject("image").getJSONObject("sizes").getJSONObject("XLarge").getString("url");
                            img.loadUrl(imgurl);
                            Shirt s = new Shirt(clothsId,Jobject.getString("name"),Jobject.getString("unbrandedName"),"$" + Jobject.getString("price"),imgurl);
                            s.save();

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                name.setText("That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        newSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newSearchIntent = new Intent(ViewResultActivity.this, CamaraIntentActivity.class);
                startActivity(newSearchIntent);
            }
        });

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

}
