package ironhammerindustries.dellwarrantyinfo;


import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Rujak on 9/29/2014.
 */
public class WarrantyInfoFetcher {
    private HttpClient httpClient;
    private HttpResponse httpResponse;
    private BufferedReader bufferedReader;

    private String apiUrl;
    private String baseUrl =
            "https://api.dell.com/support/v2/assetinfo/warranty/tags.json?svctags=";
    private String apiKeyUrl =
            "&apikey=1adecee8a60444738f280aad1cd87d0e";
    private InputStream inputStream;

    private String tempLine;
    private String tempTotal;

    private JSONObject jsonData;

    private String inputString;
    private String serviceTag;
    private String endDate;

    public void getDellJSON(String scanContent) {
        //do the stuff here to retrieve the JSON info from dell
        this.apiUrl = baseUrl + scanContent + apiKeyUrl;
        this.inputStream = null;
        try {
            this.httpClient = new DefaultHttpClient();
            this.httpResponse = this.httpClient.execute(new HttpGet(this.apiUrl));
            this.inputStream = this.httpResponse.getEntity().getContent();
            if (this.inputStream != null) {
                this.convertInputStreamToString();
                this.convertToJSONObject();
                Log.d("Response", tempTotal);
            }
            else {
                this.tempTotal = "THERE WAS AN ERROR";
            }


        } catch (Exception e) {
            Log.d("Input Stream", e.getLocalizedMessage());
        }
        /*try {
            HttpClient httpClient1 = new DefaultHttpClient();
            HttpResponse httpResponse1 = httpClient1.execute(new HttpGet(this.apiUrl));
            inputStream = httpResponse1.getEntity().getContent();
            if (inputStream != null) {
                this.convertInputStreamToString();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void convertToJSONObject() {
        try {
            this.jsonData = new JSONObject(this.inputString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void convertInputStreamToString() throws IOException {
        BufferedReader bufferedReader1 = new BufferedReader( new InputStreamReader(this.inputStream) );
        this.tempLine = "";
        while ( (this.tempLine = bufferedReader1.readLine()) != null) {
            this.tempTotal += this.tempLine;
        }
        this.inputStream.close();
        this.inputString = this.tempTotal;

    }

    public void initializeFetcher() {
        //initialize it
        this.httpClient = new DefaultHttpClient();
        this.tempTotal = "";
        this.tempLine = "";
        this.inputString = "";

    }

    public String getInputString() {
        Log.d("Response", this.tempTotal);
        return this.inputString;

    }

}
