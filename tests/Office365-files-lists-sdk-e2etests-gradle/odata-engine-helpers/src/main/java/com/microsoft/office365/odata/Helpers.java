package com.microsoft.office365.odata;

import com.microsoft.office365.odata.interfaces.DependencyResolver;
import com.microsoft.office365.odata.interfaces.ODataURL;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * The type Helpers.
 */
public class Helpers {

    private static final String ENCODE_EXCEPTIONS = "!$&'()*+,;=:@";

    /**
     * Add custom parameters to o data uRL.
     *
     * @param url the url
     * @param parameters the parameters
     * @param resolver the resolver
     */
    public static void addCustomParametersToODataURL(ODataURL url, Map<String, Object> parameters, DependencyResolver resolver) {
        Set<String> keys = parameters.keySet();

        for (String name : keys) {
            //TODO: Handle parameter value serialization for URL
            String val = parameters.get(name).toString();
            url.addQueryStringParameter(name, val);
        }
    }

    /**
     * Url encode.
     *
     * @param s the s
     * @return the string
     */
    public static String urlEncode(String s) {
        return percentEncode(s, ENCODE_EXCEPTIONS);
        /*
        try {
            return URLEncoder.encode(s, Constants.UTF8_NAME);
        } catch (UnsupportedEncodingException ignore) {
            return s;
        }
        */
    }

    private static String percentEncode(String s, String reserved) {
        if (s == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(s.length());

        int escapeStart = -1;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || "-._~".indexOf(c) != -1 || reserved.indexOf(c) != -1) {
                if (escapeStart != -1) {
                    appendHex(builder, s.substring(escapeStart, i));
                    escapeStart = -1;
                }

                builder.append(c);
            } else if (escapeStart == -1) {
                escapeStart = i;
            }
        }

        if (escapeStart != -1) {
            appendHex(builder, s.substring(escapeStart, s.length()));
        }

        return builder.toString();
    }

    private static void appendHex(StringBuilder builder, String s) {
        try {
            for (byte b : s.getBytes(Constants.UTF8_NAME)) {
                appendHex(builder, b);
            }
        } catch (UnsupportedEncodingException e) {
            // UTF-8 should support any string
        }
    }

    private static void appendHex(StringBuilder sb, byte b) {
        sb.append('%');
        sb.append(String.format("%02X", b));
    }

    /**
     * Serialize to json byte array.
     *
     * @param entity the entity
     * @param resolver the resolver
     * @return the byte [ ]
     */
    public static byte[] serializeToJsonByteArray(Object entity, DependencyResolver resolver) {
        String payload = resolver.getJsonSerializer().serialize(entity);
        return payload.getBytes(Constants.UTF8);
    }
}