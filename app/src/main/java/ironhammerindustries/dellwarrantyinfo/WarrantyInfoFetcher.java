package ironhammerindustries.dellwarrantyinfo;


import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Rujak on 9/29/2014.
 */
public class WarrantyInfoFetcher {
    private HttpClient httpClient;
    private HttpResponse httpResponse;

    private String apiUrl;
    private String baseUrl =
            "https://api.dell.com/support/v2/assetinfo/warranty/tags.json?svctags=";
    private String apiKeyUrl =
            "&apikey=1adecee8a60444738f280aad1cd87d0e";
    private InputStream inputStream;

    private String tempLine;
    private String tempTotal;

    private JSONObject jsonData;
    private JSONArray warrantiesList;
    private int numberOfWarranties;

    private String inputString;
    private String unitModel;
    private String errorMessage;

    /*private ArrayList<String> endDates;
    private ArrayList<String> startDates;
    private ArrayList<String> entitlementTypes;
    private ArrayList<String> serviceLevelDescription;*/

    private ArrayList<WarrantyInfoContainer> warrantyInfoContainers;

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
            }
            else {
                this.tempTotal = "THERE WAS AN ERROR";
            }


        } catch (Exception e) {
            //Log.d("Input Stream", e.getLocalizedMessage());
            this.errorMessage = e.getLocalizedMessage();
        }
    }

    public void convertToJSONObject() {
        try {
            this.jsonData = new JSONObject(this.inputString);
            this.unitModel = this.jsonData.getJSONObject("GetAssetWarrantyResponse")
                    .getJSONObject("GetAssetWarrantyResult").getJSONObject("Response")
                    .getJSONObject("DellAsset").getString("MachineDescription");
            this.warrantiesList = this.jsonData.getJSONObject("GetAssetWarrantyResponse")
                    .getJSONObject("GetAssetWarrantyResult").getJSONObject("Response")
                    .getJSONObject("DellAsset").getJSONObject("Warranties")
                    .getJSONArray("Warranty");
            this.numberOfWarranties = this.warrantiesList.length();

            this.warrantyInfoContainers = new ArrayList<WarrantyInfoContainer>();

            for (int i = 0; i < this.numberOfWarranties; i++) {
                this.warrantyInfoContainers.add(new WarrantyInfoContainer(
                        this.warrantiesList.getJSONObject(i).getString("EndDate"),
                        this.warrantiesList.getJSONObject(i).getString("StartDate"),
                        this.warrantiesList.getJSONObject(i).getString("EntitlementType"),
                        this.warrantiesList.getJSONObject(i).getString("ServiceLevelDescription")
                ));
                /*
                this.endDates.add(this.warrantiesList.getJSONObject(i)
                        .getString("EndDate"));
                this.startDates.add(this.warrantiesList.getJSONObject(i)
                        .getString("StartDate"));
                this.entitlementTypes.add(this.warrantiesList.getJSONObject(i)
                        .getString("EntitlementType"));
                this.serviceLevelDescription.add(this.warrantiesList.getJSONObject(i)
                        .getString("ServiceLevelDescription"));*/
            }


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
        this.warrantiesList = new JSONArray();
    }

    public ArrayList<WarrantyInfoContainer> getWarrantyList() {
        return this.warrantyInfoContainers;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
    public String getInputString() {
        Log.d("Response", this.tempTotal);
        return this.inputString;

    }

    public ArrayList<WarrantyInfoContainer> getWarrantyInfoContainers() {
        return this.warrantyInfoContainers;
    }

}
