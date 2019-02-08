package com.app.trigger;

import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.HttpsURLConnection;

public class EventTrigger {

    public void triggerSequence(String docName) throws Exception {
        URL url = null;

        String jsonString = "{\"bucket\" : \"<BUCKET_NAME>\", \"key\" : \"" +  docName + "\"}";

        System.out.println(jsonString);
        String apikey = "<APUKEY>";
        byte[] bytesEncoded = apikey.getBytes();
        String encoded = Base64.encodeBase64String(bytesEncoded);


        try {
            url = new URL("<FUNCTIONS_ACTION_URL>");
        } catch (MalformedURLException me) {
            System.err.println("URL not present or malformed");
            System.exit(1);
        }
        @SuppressWarnings("restriction")
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setConnectTimeout(0);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Basic" + encoded + "");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.connect();
        OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
        osw.write(jsonString);
        osw.flush();
        osw.close();

        System.out.println(conn.getResponseMessage());
        System.out.println("Closing Trigger Connection");
        conn.disconnect();
    }
}
