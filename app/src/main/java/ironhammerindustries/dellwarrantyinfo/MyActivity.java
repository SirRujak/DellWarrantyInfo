package ironhammerindustries.dellwarrantyinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import javax.xml.transform.Result;


public class MyActivity extends Activity implements OnClickListener {

    private TextView formatTxt, contentTxt, greenTxt, redTxt;
    private WarrantyInfoFetcher fetcher;
    private boolean isFetching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Button scanBtn = (Button) findViewById(R.id.scan_button);
        formatTxt = ( TextView )findViewById(R.id.scan_format);
        contentTxt = ( TextView )findViewById(R.id.scan_content);
        greenTxt = ( TextView )findViewById(R.id.key_green_text);
        redTxt = (TextView )findViewById(R.id.key_red_text);
        fetcher = new WarrantyInfoFetcher();
        fetcher.initializeFetcher();
        isFetching = false;
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
        if (isFetching == false) {
            isFetching = true;
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(
                    requestCode,
                    resultCode,
                    intent);
            if (scanningResult != null) {
                //we have a result
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();
                contentTxt.setText("Service Tag: " + scanContent);
                ListView updateListView = (ListView) findViewById(R.id.list_view);
                if (scanContent.length() == 7) { //simple check to see if it is actually a dell tag
                    new HttpAsyncTask(this, updateListView).execute(scanContent);
                }
                //new HttpAsyncTask(this, updateListView).execute("167L22S"); //for testing purposes


            } else {
                Toast toast = Toast.makeText(
                        getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void updateElements() {
        DellWarrantyAdapter dellWarrantyAdapter
                = new DellWarrantyAdapter(this, fetcher.getWarrantyList());
        ListView updateListView = (ListView) findViewById(R.id.list_view);
        updateListView.setAdapter(dellWarrantyAdapter);
    }

    public class HttpAsyncTask extends AsyncTask<String, Void, ArrayList<WarrantyInfoContainer>>{
        private Context context;
        private ListView listView;
        public HttpAsyncTask(Context context, ListView listView) {
            this.context = context;
            this.listView = listView;
        }
        @Override
        protected ArrayList<WarrantyInfoContainer> doInBackground(String... strings) {
            fetcher.getDellJSON(strings[0]);
            return fetcher.getWarrantyInfoContainers();
        }


        @Override
        protected void onPostExecute(ArrayList<WarrantyInfoContainer> s) {
            try {
                DellWarrantyAdapter dellWarrantyAdapter
                        = new DellWarrantyAdapter(context, s);
                this.listView.setAdapter(dellWarrantyAdapter);

            } catch (Exception e) {
                contentTxt.setText("Data was bad, pleas scan again.");
            }
            isFetching = false;
        }
    }


}
