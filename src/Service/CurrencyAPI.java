package Service;

import java.io.*;
import java.net.*;

public class CurrencyAPI {
	private static final String API_URL = "https://api.freecurrencyapi.com/v1/latest";
    private static final String API_KEY = "fca_live_1uULucMux77aE9BEPfRKg8tPRIaekWrZD7D5Hbcw";

    public static String getLatestExchangeRates() throws Exception {
        // build the complete API URL with the API key
        String apiUrlWithKey = API_URL + "?apikey=" + API_KEY;

        // create a URL object
        @SuppressWarnings("deprecation")
		URL url = new URL(apiUrlWithKey);

        // open a connection to the API
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // set connection timeout (5 seconds)
        connection.setReadTimeout(5000); // set read timeout (5 seconds)

        // check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            throw new Exception("Error fetching exchange rates. HTTP response code: " + responseCode);
        }
    }
}
