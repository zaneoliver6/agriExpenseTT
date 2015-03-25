package uwi.dcit.AgriExpenseTT.helpers;

import org.apache.commons.lang3.text.WordUtils;

public class TextHelper {

    public static String formatUserText(String input){
        input = input.trim(); // Remove leading and trailing whitespaces
        return WordUtils.capitalizeFully(input);
    }
}
