/**
 * Created by Dorina on 21.11.2016.
 */
public class UrlCreator {

    // todo url generieren hierher auslagern...
    public String getURL(String[] destinations){
        String url = "";

        return url;
    }

    // todo postleitzahlen akzeptieren?
    public String convert(String dest) {
        String result = "";
        dest.trim();
        boolean adressNrFound = false;
        for (int i = 0; i < dest.length(); i++) {
            if (!adressNrFound && (i + 1 < dest.length()) && Character.isDigit(dest.charAt(i + 1))) {
                result += "+";
                adressNrFound = true;
            } else if (Character.isWhitespace(dest.charAt(i))) {
                result += ",";
            } else if (Character.isLetter(dest.charAt(i)) || Character.isDigit(dest.charAt(i))) {
                result += dest.charAt(i);
            }
        }
        return result;
    }
}
