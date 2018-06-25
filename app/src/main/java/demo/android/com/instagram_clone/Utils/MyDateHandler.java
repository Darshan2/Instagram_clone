package demo.android.com.instagram_clone.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Darshan B.S on 12-06-2018.
 */

public class MyDateHandler {
    private static final String TAG = "MyDateHandler";

    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat
                ("dd-MM-yyyy 'at' hh:mm:ss a zzz", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return simpleDateFormat.format(new Date());
    }


    public static String getTimeDiffernce(String postedTime) {
        String timeDiff = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat
                ("dd-MM-yyyy 'at' hh:mm:ss a zzz", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

        String todayDateStr = simpleDateFormat.format(new Date());
        Log.d(TAG, "getTimeStampDifference: today" + todayDateStr);
        String postedDateStr = postedTime;//which is stored in above format within database

        try {
            Date todayDate = simpleDateFormat.parse(todayDateStr);
            Date postedDate = simpleDateFormat.parse(postedDateStr);
            //difference is in long milliseconds, convert it into difference in days format
            timeDiff = String.valueOf(
                    Math.round((todayDate.getTime() - postedDate.getTime()) / 1000 / 60 / 60 / 24));
        } catch (ParseException e) {
            Log.d(TAG, "getTimeStampDifference: ParseException: " + e.getMessage());
        }

        return timeDiff;
    }
}
