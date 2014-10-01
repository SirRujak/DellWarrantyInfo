package ironhammerindustries.dellwarrantyinfo;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rujak on 10/1/2014.
 */
public class WarrantyInfoContainer {
    private String endDate;
    private String startDate;
    private String entitlementType;
    private String serviceLevelDescription;

    private boolean isActive;

    public WarrantyInfoContainer(String endDate, String startDate, String entitlementType,
                                 String serviceLevelDescription) {
        this.endDate = endDate;
        this.startDate = startDate;
        this.entitlementType = entitlementType;
        this.serviceLevelDescription = serviceLevelDescription;
        this.checkIfWarrantyIsActive();
    }

    private void checkIfWarrantyIsActive() {
        Timestamp currentTime = this.getCurrentDate();
        Timestamp endDateAsDate = this.changeEndDateToTimeStamp();
        if (currentTime.after(endDateAsDate)) {
            this.isActive = false;
        } else {
            this.isActive = true;
        }
    }

    private Timestamp changeEndDateToTimeStamp() {
        String[] timeSplitEndDate = this.endDate.split("T");
        String repairedTimeString = timeSplitEndDate[0] + " " + timeSplitEndDate[1];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date endDateAsDate = null;
        Timestamp endDateAsTimeStamp = null;
        try {
            endDateAsDate = dateFormat.parse(repairedTimeString);
            endDateAsTimeStamp = new Timestamp(endDateAsDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endDateAsTimeStamp;
    }

    private Timestamp getCurrentDate(){
        Date currentDate = new Date();
        Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
        return currentTimeStamp;
    }

    public String getEntitlementType() {
        return this.entitlementType;
    }
    public String getEndDate() {
        return this.endDate;
    }

    public boolean getIsActive() {
        return this.isActive;
    }
}
