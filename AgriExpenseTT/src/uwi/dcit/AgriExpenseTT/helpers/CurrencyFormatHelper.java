package uwi.dcit.AgriExpenseTT.helpers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatHelper {

    public static String getCurrency(double val){
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "UK"));
        df.applyPattern("###,###.##");
        return df.format(val);
    }
}
