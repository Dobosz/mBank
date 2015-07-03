package pl.dobosz.mbank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dobosz on 02.07.15.
 */
public class Utils {
    public static String getCookie(String cookie, Map<String, List<String>> headerFields) {
        Set<String> headerFieldsSet = headerFields.keySet();
        Iterator<String> hearerFieldsIter = headerFieldsSet.iterator();

        while (hearerFieldsIter.hasNext()) {
            String headerFieldKey = hearerFieldsIter.next();
            if ("Set-Cookie".equalsIgnoreCase(headerFieldKey)) {
                List<String> headerFieldValue = headerFields.get(headerFieldKey);
                for (String headerValue : headerFieldValue) {
                    String[] fields = headerValue.split("; ");

                    String[] inputCookie = fields[0].split("=");
                    if(inputCookie[0].equalsIgnoreCase(cookie))
                        return inputCookie[1];
                }
            }
        }
        return null;
    }

    public static String buildResponse(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));

        String buffor = "";
        try {
            while ((buffor = bufferedReader.readLine()) != null)
                builder.append(buffor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
