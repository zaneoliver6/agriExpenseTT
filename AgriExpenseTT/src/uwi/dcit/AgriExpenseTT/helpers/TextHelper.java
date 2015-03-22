package uwi.dcit.AgriExpenseTT.helpers;

import org.apache.commons.lang3.text.WordUtils;

public class TextHelper {
    public static String formatUserText(String input){
        return WordUtils.capitalizeFully(input);
    }
}
