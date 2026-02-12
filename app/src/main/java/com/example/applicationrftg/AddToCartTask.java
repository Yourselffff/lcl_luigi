package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddToCartTask extends AsyncTask<URL,Integer,String> {

    private volatile Object screen;
    private String filmId;
    private String customerId;

    public AddToCartTask(Object s, String filmId, String customerId) {
        this.screen = s;
        this.filmId = filmId;
        this.customerId = customerId;
    }

    @Override
    protected void onPreExecute() {
        // Prétraitement
    }

    @Override
    protected String doInBackground(URL... urls) {
        String sResultatAppel = null;
        URL urlAAppeler = urls[0];
        sResultatAppel = appelerServiceRestHttp(urlAAppeler);
        return sResultatAppel;
    }

    @Override
    protected void onPostExecute(String resultat) {
        System.out.println(">>>onPostExecute AddToCartTask / resultat="+resultat);

        // Appeler la bonne méthode selon le type d'Activity
        if (screen instanceof DetailfilmActivity) {
            ((DetailfilmActivity) screen).filmAjouteAuPanierAvecSucces(resultat);
        } else if (screen instanceof ListefilmsActivity) {
            ((ListefilmsActivity) screen).filmAjouteAuPanierAvecSucces(resultat);
        }
    }

    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";
        try {
            // Appel POST avec les données du panier
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization", ((android.app.Activity) screen).getString(R.string.api_token));
            urlConnection.setDoOutput(true);

            String jsonInputString = "{\"customerId\": " + customerId +
                    ", \"filmId\": " + filmId + "}";

            Log.d("mydebug", ">>>AddToCartTask - JSON envoyé : " + jsonInputString);

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>AddToCartTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            int codeCaractere = -1;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>AddToCartTask - Résultat obtenu : " + sResultatAppel);
        } catch (IOException ioe) {
            Log.d("mydebug", ">>>AddToCartTask - IOException ioe =" + ioe.toString());
            sResultatAppel = "ERROR";
        } catch (Exception e) {
            Log.d("mydebug",">>>AddToCartTask - Exception="+e.toString());
            sResultatAppel = "ERROR";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
