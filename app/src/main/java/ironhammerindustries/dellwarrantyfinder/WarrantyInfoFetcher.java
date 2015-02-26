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
    private JSONArray subData;
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
            this.subData = this.jsonData.getJSONObject("GetAssetWarrantyResponse")
                        .getJSONObject("GetAssetWarrantyResult").getJSONObject("Response")
                        .getJSONArray("DellAsset");
            for (int i = 0;i < this.subData.length();i++){
                String serviceTag = this.subData.getJSONObject(i).getString("ServiceTag");
                String machineDescription = this.subData.getJSONObject(i)
                         .getString("MachineDescription");
                String customerNumber = this.subData.getJSONObject(i).getString("CustomerNumber");
                String itemClassCode = this.subData.getJSONObject(i).getString("ItemClassCode");
                try {
                    String parentServiceTag = this.subData.getJSONObject(i)
                            .getString("ParentServiceTag");
                } catch (JSONException e1) {
                    String parentServiceTag = "None";
                }
                try {
                    JSONArray jsonWarranties = this.subData.getJSONObject(i).getJSONObject("Warranties")
                            .getJSONArray("Warranty");
                    for (int j = 0;j  < jsonWarranties.length();j++) {
                        String endDate = jsonWarranties.getJSONObject(j).getString("EndDate");
                        String entitlementType = jsonWarranties.getJSONObject(j)
                                .getString("EntitlementType");
                        String serviceLevelDescription = jsonWarranties.getJSONObject(j)
                                .getString("ServiceLevelDescription");
                        String serviceProvider = jsonWarranties.getJSONObject(j)
                                .getString("ServiceProvider");
                        String startDate = jsonWarranties.getJSONObject(j).getString("StartDate");
                        WarrantyInfoContainer tempContainer2 = new WarrantyInfoContainer(endDate,
                                startDate,
                                entitlementType,
                                serviceLevelDescription);
                        if (serviceTag.equals(this.serviceTag)) {
                            this.warrantyInfoContainers.add(tempContainer2);
                        }
                    }
                } catch (JSONException e3) {
                    JSONObject jsonWarranties = this.subData.getJSONObject(i)
                            .getJSONObject("Warranties").getJSONObject("Warranty");
                    String endDate = jsonWarranties.getString("EndDate");
                    String entitlementType = jsonWarranties.getString("EntitlementType");
                    String serviceLevelDescription = jsonWarranties
                            .getString("ServiceLevelDescription");
                    String serviceProvider = jsonWarranties
                            .getString("ServiceProvider");
                    String startDate = jsonWarranties.getString("StartDate");
                    if (serviceTag.equals(this.serviceTag)) {
                        this.warrantyInfoContainers.add(
                                new WarrantyInfoContainer(endDate,
                                        startDate,
                                        entitlementType,
                                        serviceLevelDescription));
                    }
                }

            }
            if (this.warrantyInfoContainers.isEmpty()) {
                WarrantyInfoContainer tempContainer2 = new WarrantyInfoContainer();
                this.warrantyInfoContainers.add(tempContainer2);
            }
        } catch (JSONException e) {
            String tempString = "<Fault xmlns=\"http://api.dell.com/faults\">\t" +
                    "<Code>eAPI-40102</Code>\t<Url>http://developer.dell.com/faults#eAPI" +
                    "-40102</Url>\t<Message>Failed to Authenticate User</Message>\t" +
                    "<Reason>User Identification failed in Key Management Service</Reason>" +
                    "\t<Source>KMS</Source>\t<StackTrace></StackTrace></Fault>";
            if (this.inputString.equals(tempString)) {
                WarrantyInfoContainer tempContainer2 =
                        new WarrantyInfoContainer("Error Authenticating: Please try again.");
                this.warrantyInfoContainers.add(tempContainer2);
            } else {
                WarrantyInfoContainer tempContainer2 = new WarrantyInfoContainer();
                this.warrantyInfoContainers.add(tempContainer2);
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
