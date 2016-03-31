package fi.csc.shibboleth.mobileauth.impl.authn.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * 
 * @author kkorte
 *
 */
public class MobileAuthenticationUtils {

    private final static String NUM_PREFIX = "+358";
    private final static List<String> langs = Arrays.asList("fi", "sv", "en");

    /**
     * Validate phoneNumber against regular expression
     * 
     * @param number
     *            String phoneNumber that user has inputted to the form
     * @return true if phone number is valid
     */
    public static boolean validatePhoneNumber(String number) {
        String pattern = "^(\\+|0)(?:[0-9] ?){6,14}[0-9]$";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(number);

        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * Fixes users phoneNumber to the correct, international form
     * 
     * @param number
     *            String phoneNumber that user has inputted to the form
     * @return Fixed phoneNumber or null
     */
    public static String fixPhoneNumber(String number) {

        if (number.startsWith(NUM_PREFIX)) {
            return number.trim();

        } else if (number.startsWith("0")) {
            StringBuilder sb = new StringBuilder("");
            number = number.substring(1);
            sb.append(NUM_PREFIX);
            sb.append(number);
            return sb.toString().trim();
        }
        return null;
    }

    /**
     * Validates spam prevention code that user has inputted to the form
     * 
     * @param spam
     *            String spam prevention code that user has inputted to the form
     * @return true if spam code is valid
     */
    public static boolean validateSpamPreventionCode(String spam) {
        String pattern = "^[a-zA-Z]\\w{3,7}$";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(spam);

        if (m.find()) {
            return true;
        }

        return false;
    }

    /** 
     * Extracts language code from the AuthnRequest extension element
     * 
     * @param xmlElement AuthnRequest
     * @param ns XML Namespace where language code lives
     * @param tag XML element where language code lives
     * @return lang 
     */
    public static String unMarshallLanguage(Element xmlElement, String ns, String tag) {

        String lang = null;
        
        if (xmlElement.getElementsByTagNameNS(ns, tag).getLength() > 0) {

            lang = StringSupport.trimOrNull(xmlElement.getElementsByTagNameNS(ns, tag).item(0).getTextContent());
        }

        if (lang != null && langs.contains(lang)) {
            return lang;
        }
        return null;
    }
}
