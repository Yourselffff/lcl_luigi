package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckoutCartTask extends AsyncTask<URL,Integer,String> {

    private volatile PanierActivity screen;
    private String customerId;

    public CheckoutCartTask(PanierActivity s, String customerId) {
        this.screen = s;
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
        System.out.println(">>>onPostExecute CheckoutCartTask / resultat="+resultat);
        this.screen.panierValideAvecSucces(resultat);
    }

    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";
        try {
            // Appel POST pour valider le panier
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.e30.jg2m4pLbAlZv1h5uPQ6fU38X23g65eXMX8q-SXuIPDg");
            urlConnection.setDoOutput(true);

            String jsonInputString = "{\"customerId\": " + customerId + "}";

            Log.d("mydebug", ">>>CheckoutCartTask - JSON envoyé : " + jsonInputString);

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>CheckoutCartTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            int codeCaractere = -1;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>CheckoutCartTask - Résultat obtenu : " + sResultatAppel);
        } catch (IOException ioe) {
            Log.d("mydebug", ">>>CheckoutCartTask - IOException ioe =" + ioe.toString());
            sResultatAppel = "ERROR";
        } catch (Exception e) {
            Log.d("mydebug",">>>CheckoutCartTask - Exception="+e.toString());
            sResultatAppel = "ERROR";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
