package uwi.dcit.AgriExpenseTT.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatStandard {

    public static String getDateStr(Date d){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.UK);
        return formatter.format(d);
    }

    public static String getDateStr(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return getDateStr(cal.getTime());
    }


}
