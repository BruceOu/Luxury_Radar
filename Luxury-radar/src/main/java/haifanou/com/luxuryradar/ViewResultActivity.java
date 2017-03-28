package haifanou.com.luxuryradar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class ViewResultActivity extends Activity {

    private Button newSearchBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);
        newSearchBtn=(Button) findViewById(R.id.newSearchBtn);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        String url=getIntent().getExtras().getString("shirtUrl");

        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        startActivity(browserIntent);

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
