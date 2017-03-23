package haifanou.com.luxuryradar;

import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.webkit.WebView;

public class ViewResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView=new WebView(this);
        setContentView(webView);
        webView.loadUrl("http://www.google.com");
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
