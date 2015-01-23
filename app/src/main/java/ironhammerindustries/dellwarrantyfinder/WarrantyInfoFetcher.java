package ironhammerindustries.dellwarrantyfinder;


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
    private JSONArray multipleDevicesList;
    private int numberOfWarranties;

    private String inputString;
    private String serviceTag;
    private String unitModel;
    private String errorMessage;

    private ArrayList<WarrantyInfoContainer> warrantyInfoContainers;
    private JSONObject singularWarranty;

    public void getDellJSON(String scanContent) {
        //do the stuff here to retrieve the JSON info from dell
        this.serviceTag = scanContent;
        this.apiUrl = baseUrl + scanContent + apiKeyUrl;
        this.inputStream = null;
        inputString = "";
        try {
            this.warrantyInfoContainers = new ArrayList<WarrantyInfoContainer>();
            this.httpClient = new DefaultHttpClient();
            this.httpResponse = this.httpClient.execute(new HttpGet(this.apiUrl));
            this.inputStream = this.httpResponse.getEntity().getContent();
            if (this.inputStream != null) {
                this.convertInputStreamToString();
                this.convertToJSONObject();
            }
            else {
                this.tempTotal = "THERE WAS AN ERROR";
                WarrantyInfoContainer tempContainer2 = new WarrantyInfoContainer(
                        "And error occurred, please try again."
                );
                this.warrantyInfoContainers.add(tempContainer2);
            }


        } catch (Exception e) {
            this.tempTotal = "Could not connect";

            WarrantyInfoContainer tempContainer2 = new WarrantyInfoContainer(
                    "Unable to contact server!"
            );
            this.warrantyInfoContainers.add(tempContainer2);
        }
    }

    public void convertToJSONObject() {
        try {
            this.jsonData = null;
            this.jsonData = new JSONObject(this.inputString);
            int faultData;
            faultData = this.jsonData.getJSONObject("GetAssetWarrantyResponse")
                    .getJSONObject("GetAssetWarrantyResult").getJSONObject("Faults")
                    .getJSONObject("FaultException").getInt("Code");
            if (faultData != 4001){
                throw new Exception();
            }

            WarrantyInfoContainer tempContainer2 = new WarrantyInfoContainer(
                    "Invalid Service Code");
            this.warrantyInfoContainers.add(tempContainer2);

        } catch (Exception e1) {
            try {
                this.unitModel = this.jsonData.getJSONObject("GetAssetWarrantyResponse")
                        .getJSONObject("GetAssetWarrantyResult").getJSONObject("Response")
                        .getJSONObject("DellAsset").getString("MachineDescription");
                this.warrantiesList = this.jsonData.getJSONObject("GetAssetWarrantyResponse")
                        .getJSONObject("GetAssetWarrantyResult").getJSONObject("Response")
                        .getJSONObject("DellAsset").getJSONObject("Warranties")
                        .getJSONArray("Warranty");
                this.numberOfWarranties = this.warrantiesList.length();


                for (int i = 0; i < this.numberOfWarranties; i++) {
                    this.warrantyInfoContainers.add(new WarrantyInfoContainer(
                            this.warrantiesList.getJSONObject(i)
                                    .getString("EndDate"),
                            this.warrantiesList.getJSONObject(i)
                                    .getString("StartDate"),
                            this.warrantiesList.getJSONObject(i)
                                    .getString("EntitlementType"),
                            this.warrantiesList.getJSONObject(i)
                                    .getString("ServiceLevelDescription")
                    ));
                }

                if (this.warrantyInfoContainers.isEmpty()) {
                    WarrantyInfoContainer tempContainer = new WarrantyInfoContainer();
                    this.warrantyInfoContainers.add(tempContainer);
                }

            } catch (Exception e2) {
                try {
                    multipleDevicesList = this.jsonData.getJSONObject("GetAssetWarrantyResponse")
                            .getJSONObject("GetWarrantyResult").getJSONObject("Response")
                            .getJSONArray("DellAsset");
                    for (int j = 0; j < multipleDevicesList.length(); j++) {
                        if (multipleDevicesList.getJSONObject(0).getString("").toUpperCase()
                                == this.serviceTag.toUpperCase()) {

                            this.warrantyInfoContainers.add(new WarrantyInfoContainer(
                                    this.multipleDevicesList.getJSONObject(j)
                                            .getString("EndDate"),
                                    this.multipleDevicesList.getJSONObject(j)
                                            .getString("StartDate"),
                                    this.multipleDevicesList.getJSONObject(j)
                                            .getString("EntitlementType"),
                                    this.multipleDevicesList.getJSONObject(j)
                                            .getString("ServiceLevelDescription"))
                            );
                        }
                    }

                } catch (Exception e3) {
                    try {
                        multipleDevicesList = this.jsonData.getJSONObject("GetAssetWarrantyResponse")
                                .getJSONObject("GetWarrantyResult").getJSONObject("Response")
                                .getJSONArray("DellAsset");
                        for (int j = 0; j < multipleDevicesList.length(); j++) {
                            if (multipleDevicesList.getJSONObject(0).getString("").toUpperCase()
                                    == this.serviceTag.toUpperCase()) {
                                for (int k = 0; k < this.multipleDevicesList.getJSONObject(j)
                                        .getJSONObject("Warranties").getJSONArray("Warranty")
                                        .length(); k++)
                                this.warrantyInfoContainers.add(new WarrantyInfoContainer(
                                                this.multipleDevicesList.getJSONObject(j)
                                                        .getJSONObject("Warranties")
                                                        .getJSONArray("Warranty")
                                                        .getJSONObject(k)
                                                        .getString("EndDate"),
                                                this.multipleDevicesList.getJSONObject(j)
                                                        .getJSONObject("Warranties")
                                                        .getJSONArray("Warranty")
                                                        .getJSONObject(k)
                                                        .getString("StartDate"),
                                                this.multipleDevicesList.getJSONObject(j)
                                                        .getJSONObject("Warranties")
                                                        .getJSONArray("Warranty")
                                                        .getJSONObject(k)
                                                        .getString("EntitlementType"),
                                                this.multipleDevicesList.getJSONObject(j)
                                                        .getJSONObject("Warranties")
                                                        .getJSONArray("Warranty")
                                                        .getJSONObject(k)
                                                        .getString("ServiceLevelDescription"))
                                );
                            }
                        }
                    } catch (Exception e4) {
                        try {
                            this.unitModel = this.jsonData.getJSONObject("GetAssetWarrantyResponse")
                                    .getJSONObject("GetAssetWarrantyResult").getJSONObject("Response")
                                    .getJSONObject("DellAsset").getString("MachineDescription");
                            this.singularWarranty = this.jsonData.getJSONObject("GetAssetWarrantyResponse")
                                    .getJSONObject("GetAssetWarrantyResult").getJSONObject("Response")
                                    .getJSONObject("DellAsset").getJSONObject("Warranties")
                                    .getJSONObject("Warranty");
                            this.numberOfWarranties = this.warrantiesList.length();


                            this.warrantyInfoContainers.add(new WarrantyInfoContainer(
                                    this.singularWarranty
                                            .getString("EndDate"),
                                    this.singularWarranty
                                            .getString("StartDate"),
                                    this.singularWarranty
                                            .getString("EntitlementType"),
                                    this.singularWarranty
                                            .getString("ServiceLevelDescription")
                            ));



                            if (this.warrantyInfoContainers.isEmpty()) {
                                WarrantyInfoContainer tempContainer = new WarrantyInfoContainer();
                                this.warrantyInfoContainers.add(tempContainer);
                            }
                        } catch (Exception e5) {
                            WarrantyInfoContainer tempContainer2 = new WarrantyInfoContainer();
                            this.warrantyInfoContainers.add(tempContainer2);
                            e1.printStackTrace();
                        }
                    }

                }

            }

        }
    }

    public void convertInputStreamToString() throws IOException {
        this.tempTotal = "";
        BufferedReader bufferedReader1 = new BufferedReader(
                new InputStreamReader(this.inputStream) );
        this.tempLine = "";
        while ( (this.tempLine = bufferedReader1.readLine()) != null) {
            this.tempTotal += this.tempLine;
        }
        this.inputStream.close();
        this.inputString= "";
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
