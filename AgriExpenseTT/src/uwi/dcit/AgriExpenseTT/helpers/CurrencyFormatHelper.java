package uwi.dcit.AgriExpenseTT.helpers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by kyledef on 2/25/15.
 */
public class CurrencyFormatHelper {

    public static String getCurrency(double val){
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "UK"));
        df.applyPattern("###,###.##");
        return df.format(val);
    }
}
