package ironhammerindustries.dellwarrantyinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MyActivity extends Activity implements OnClickListener {

    private TextView formatTxt, contentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Button scanBtn = (Button) findViewById(R.id.scan_button);
        formatTxt = ( TextView )findViewById(R.id.scan_format);
        contentTxt = ( TextView )findViewById(R.id.scan_content);
        scanBtn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        //respond to clicks
        if (v.getId() == R.id.scan_button) {
            //scan
            IntentIntegrator scanIntegrator = new IntentIntegrator( this );
            scanIntegrator.initiateScan();
        }
    }

    public void onActivityResult( int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult( requestCode, resultCode, intent);
        if( scanningResult != null ) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            formatTxt.setText( "Code Format: " + scanFormat);
            contentTxt.setText( "Service Tag: " + scanContent);
            getDellJSON(scanContent);
        }
        else {
            Toast toast = Toast.makeText( getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT );
            toast.show();
        }
    }

    public void getDellJSON(String scanContent) {
        //do the stuff here to retrieve the JSON info from dell
    }
}
