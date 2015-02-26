package ironhammerindustries.dellwarrantyfinder;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity implements OnClickListener {

    private TextView formatTxt, contentTxt, greenTxt, redTxt;
    private TextView coinTitleTxt, coinPart1, coinPart2, coinPart3, coinPart4;
    private TextView coinPart5, coinPart6, coinPart7;
    private EditText manualEditTxt;
    private RelativeLayout mainLayout, addressLayout, manualLayout;
    private WarrantyInfoFetcher fetcher;
    private boolean isFetching;
    private String manualCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Button scanBtn = (Button) findViewById(R.id.scan_button);
        Button bitShow = (Button) findViewById(R.id.show_bit_button);
        Button bitCopy = (Button) findViewById(R.id.copy_bit_button);
        Button dogeShow = (Button) findViewById(R.id.show_doge_button);
        Button dogeCopy = (Button) findViewById(R.id.copy_doge_button);
        this.formatTxt = ( TextView )findViewById(R.id.scan_format);
        this.contentTxt = ( TextView )findViewById(R.id.scan_content);
        this.greenTxt = ( TextView )findViewById(R.id.key_green_text);
        this.redTxt = (TextView )findViewById(R.id.key_red_text);
        this.mainLayout = (RelativeLayout) findViewById(R.id.main_rel_layout);
        this.addressLayout = ( RelativeLayout )findViewById(R.id.address_layout);
        fetcher = new WarrantyInfoFetcher();
        fetcher.initializeFetcher();
        isFetching = false;
        scanBtn.setOnClickListener(this);
        bitCopy.setOnClickListener(this);
        bitShow.setOnClickListener(this);
        dogeCopy.setOnClickListener(this);
        dogeShow.setOnClickListener(this);

        this.coinTitleTxt = ( TextView )findViewById(R.id.coin_title_text);
        this.coinPart1 = ( TextView )findViewById(R.id.coin_part_1);
        this.coinPart2 = ( TextView )findViewById(R.id.coin_part_2);
        this.coinPart3 = ( TextView )findViewById(R.id.coin_part_3);
        this.coinPart4 = ( TextView )findViewById(R.id.coin_part_4);
        this.coinPart5 = ( TextView )findViewById(R.id.coin_part_5);
        this.coinPart6 = ( TextView )findViewById(R.id.coin_part_6);
        this.coinPart7 = ( TextView )findViewById(R.id.coin_part_7);
        Button returnToMainButton =( Button )findViewById(R.id.return_to_app);
        returnToMainButton.setOnClickListener(this);

        // for the manual entry
        Button openManualEntry = ( Button )findViewById(R.id.open_manual_entry);
        openManualEntry.setOnClickListener(this);
        Button returnFromManualEntry = ( Button )findViewById(R.id.manual_entry_button);
        returnFromManualEntry.setOnClickListener(this);
        this.manualLayout = ( RelativeLayout )findViewById(R.id.manual_entry_layout);
        manualEditTxt = ( EditText )findViewById(R.id.manual_entry_box);
        Button manualClearText = ( Button )findViewById(R.id.manual_entry_clear);
        manualClearText.setOnClickListener(this);
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
        else if (v.getId() == R.id.copy_bit_button){
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(
                    "text",
                    "1MJN2mccLB47ht2FQtS5fgRhbiSF7edUjh");
            clipboard.setPrimaryClip(clipData);
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    "Copied BitCoin Address!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if (v.getId() == R.id.copy_doge_button){
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(
                    "text",
                    "DHMCCzCzbU5iAMU61nJYp4tyyDsU8ES8YJ");
            clipboard.setPrimaryClip(clipData);
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    "Copied DogeCoin Address!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if (v.getId() == R.id.show_bit_button){
            this.showAddress("BitCoin",
                    "1MJN2mccLB47ht2FQtS5fgRhbiSF7edUjh");
        }
        else if (v.getId() == R.id.show_doge_button){
            this.showAddress("DogeCoin",
                    "DHMCCzCzbU5iAMU61nJYp4tyyDsU8ES8YJ");
        }
        else if (v.getId() == R.id.return_to_app){
            this.hideAddress();
        }
        else if (v.getId() == R.id.open_manual_entry){
            this.showManualEntry();
        }
        else if (v.getId() == R.id.manual_entry_button){
            this.hideManualEntry();
        }
        else if (v.getId() == R.id.manual_entry_clear){
            this.clearManualText();
        }
    }

    public void showAddress(String addressType, String addressValue) {
        this.mainLayout.setVisibility(View.INVISIBLE);
        this.coinTitleTxt.setText(addressType + " Address");
        List<String> tempList =  new ArrayList<String>((addressValue.length() + 5 - 1)/5);
        for (int i = 0; i < addressValue.length(); i+=5){
            tempList.add(addressValue.substring(i, Math.min(addressValue.length(), i + 5)));
        }

        this.coinPart1.setText(tempList.get(0));
        this.coinPart2.setText(tempList.get(1));
        this.coinPart3.setText(tempList.get(2));
        this.coinPart4.setText(tempList.get(3));
        this.coinPart5.setText(tempList.get(4));
        this.coinPart6.setText(tempList.get(5));
        this.coinPart7.setText(tempList.get(6));

        this.addressLayout.setVisibility(View.VISIBLE);

    }

    public void clearManualText(){
        this.manualEditTxt.setText("");
    }

    public void hideAddress(){
        this.addressLayout.setVisibility(View.INVISIBLE);
        this.mainLayout.setVisibility(View.VISIBLE);

    }

    public void showManualEntry(){
        this.mainLayout.setVisibility(View.INVISIBLE);
        this.manualLayout.setVisibility(View.VISIBLE);
    }

    public void hideManualEntry(){
        this.manualLayout.setVisibility(View.INVISIBLE);
        this.mainLayout.setVisibility(View.VISIBLE);
        this.manualCode = this.manualEditTxt.getText().toString();
        this.manualFetch();
    }

    public void manualFetch(){
        if (isFetching == false) {
            isFetching = true;
            this.fetchData(this.manualCode);
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
            if (scanningResult != null && intent !=null) {
                //we have a result
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();
                this.fetchData(scanContent);

                //new HttpAsyncTask(this, updateListView).execute("167L22S"); //for testing purposes


            } else {
                this.isFetching = false;
                Toast toast = Toast.makeText(
                        getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }
    }

    public void fetchData(String scanContent){
        contentTxt.setText("Service Tag: " + scanContent);
        ListView updateListView = (ListView) findViewById(R.id.list_view);
        if (scanContent.length() == 7) { //simple check to see if it is actually a dell tag
            new HttpAsyncTask(this, updateListView).execute(scanContent);
            Toast toast1 = Toast.makeText(
                    getApplicationContext(),
                    "Attempting to retrieve warranty data!",
                    Toast.LENGTH_LONG);
            toast1.setGravity(Gravity.CENTER, 0, 0);
            toast1.show();
        } else {
            this.updateElements();
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    "Not a recognized Dell™ product.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            isFetching = false;
        }
    }

    public void updateElements() {
        ArrayList<WarrantyInfoContainer> dummyList = new ArrayList<WarrantyInfoContainer>();
        WarrantyInfoContainer tempContainer = new WarrantyInfoContainer();
        dummyList.add(tempContainer);
        int x = 1;
        DellWarrantyAdapter dellWarrantyAdapter
                = new DellWarrantyAdapter(this, dummyList);
        ListView updateListView = (ListView) findViewById(R.id.list_view);
        updateListView.setAdapter(dellWarrantyAdapter);
    }

    public class HttpAsyncTask extends AsyncTask<String, Void, ArrayList<WarrantyInfoContainer>>{
        private Context context;
        private ListView listView;
        private TextView testText;

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
                // Save to database here.
                DellWarrantyAdapter dellWarrantyAdapter
                        = new DellWarrantyAdapter(context, s);
                this.listView.setAdapter(dellWarrantyAdapter);
                Toast toast1 = Toast.makeText(
                        getApplicationContext(),
                        "Data updated!",
                        Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER, 0, 0);
                toast1.show();

            } catch (Exception e) {
                contentTxt.setText("Data was bad, please scan again.");
            }
            isFetching = false;
        }
    }


}
