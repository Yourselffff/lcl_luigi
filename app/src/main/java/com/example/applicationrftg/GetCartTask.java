package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetCartTask extends AsyncTask<URL,Integer,String> {

    private volatile Object screen;
    private String customerId;

    public GetCartTask(Object s, String customerId) {
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
        System.out.println(">>>onPostExecute GetCartTask / resultat="+resultat);

        // Appeler la bonne méthode selon le type d'Activity
        if (screen instanceof PanierActivity) {
            ((PanierActivity) screen).mettreAJourPanierApresAppelRest(resultat);
        } else if (screen instanceof ListefilmsActivity) {
            ((ListefilmsActivity) screen).mettreAJourPanierApresAppelRest(resultat);
        }
    }

    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";
        try {
            // Appel GET pour récupérer le panier
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization", ((android.app.Activity) screen).getString(R.string.api_token));

            responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>GetCartTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            int codeCaractere = -1;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>GetCartTask - Résultat obtenu : " + sResultatAppel);
        } catch (IOException ioe) {
            Log.d("mydebug", ">>>GetCartTask - IOException ioe =" + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug",">>>GetCartTask - Exception="+e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
