package uwi.dcit.AgriExpenseTT.helpers;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class TextHelper {

    public static String formatUserText(String input){
        input = input.trim(); // Remove leading and trailing whitespaces
        return WordUtils.capitalizeFully(input);
    }

    public static String replaceNthWord(String word, int n, String insert){
        String [] wordArr = StringUtils.split(word);
        wordArr[n] = insert;
        return StringUtils.join(wordArr, " ");
    }
}
