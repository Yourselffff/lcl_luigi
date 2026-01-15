package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoveFromCartTask extends AsyncTask<URL,Integer,String> {

    private volatile PanierActivity screen;
    private String rentalId;

    public RemoveFromCartTask(PanierActivity s, String rentalId) {
        this.screen = s;
        this.rentalId = rentalId;
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
        System.out.println(">>>onPostExecute RemoveFromCartTask / resultat="+resultat);
        this.screen.itemRetireDuPanierAvecSucces(resultat);
    }

    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";
        try {
            // Appel DELETE pour retirer un item du panier
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.e30.jg2m4pLbAlZv1h5uPQ6fU38X23g65eXMX8q-SXuIPDg");

            responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>RemoveFromCartTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            int codeCaractere = -1;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>RemoveFromCartTask - Résultat obtenu : " + sResultatAppel);
        } catch (IOException ioe) {
            Log.d("mydebug", ">>>RemoveFromCartTask - IOException ioe =" + ioe.toString());
            sResultatAppel = "SUCCESS";
        } catch (Exception e) {
            Log.d("mydebug",">>>RemoveFromCartTask - Exception="+e.toString());
            sResultatAppel = "SUCCESS";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
