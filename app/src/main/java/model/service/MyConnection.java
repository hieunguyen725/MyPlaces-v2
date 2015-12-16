package model.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Author: Hieu Nguyen
 *
 * This is a helper class to create connection and make
 * http request to the web service.
 */
public class MyConnection {

    /**
     * Given an URL, use that URL to create a new connection
     * to make a new request, then return a String content based
     * on the http reponse.
     * @param URL the url for making the http request.
     * @return a string content retrieved from the http response.
     * @throws IOException
     */
    public String retrieveData(String URL) throws IOException {
        BufferedReader reader = null;
        try {
            URL url = new URL(URL);
            // Open the new connection.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.connect();
            StringBuilder stringBuilder = new StringBuilder();
            // Create a new reader for content InputStream.
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            // Append each line of the content while it is not null.
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
