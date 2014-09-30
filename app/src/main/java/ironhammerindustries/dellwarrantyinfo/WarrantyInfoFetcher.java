package ironhammerindustries.dellwarrantyinfo;


import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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

    private String inputString;
    private String serviceTag;
    private String endDate;

    public void getDellJSON(String scanContent) {
        //do the stuff here to retrieve the JSON info from dell
        this.apiUrl = baseUrl + scanContent + apiKeyUrl;
        inputStream = null;
        try {
            this.httpResponse = this.httpClient.execute(new HttpGet(this.apiUrl));
            this.inputStream = this.httpResponse.getEntity().getContent();
            if (inputStream != null) {
                this.convertInputStreamToString();

            }
            else {
                this.tempTotal = "THERE WAS AN ERROR";
            }
            Log.d("Response", tempTotal);

        } catch (Exception e) {
            Log.d("Input Stream", e.getLocalizedMessage());
        }
    }

    public void convertInputStreamToString() throws IOException {
        this.bufferedReader = new BufferedReader( new InputStreamReader(this.inputStream) );
        this.tempLine = "";
        while ( (this.tempLine = this.bufferedReader.readLine()) != null) {
            this.tempTotal += this.tempLine;
        }
        this.inputString = this.tempTotal.substring(0,10);
        this.inputStream.close();
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
