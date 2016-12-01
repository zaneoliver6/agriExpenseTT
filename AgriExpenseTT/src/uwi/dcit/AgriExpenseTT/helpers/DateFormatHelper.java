package uwi.dcit.AgriExpenseTT.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatHelper {

    public static String getDateStr(Date d){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.UK);
        return formatter.format(d);
    }

    public static String getDateStr(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return getDateStr(cal.getTime());
    }

    public static long getDateUnix(Date d){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.getTimeInMillis();
    }

    public static String formatDisplayDate(Calendar calendar){
        String strDate;
        if ( calendar == null){
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        Date d = calendar.getTime();
        strDate = DateFormat.getDateInstance().format(d);
        return strDate;
    }

}
