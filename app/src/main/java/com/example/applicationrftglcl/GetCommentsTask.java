package com.example.applicationrftglcl;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetCommentsTask extends AsyncTask<URL,Integer,String> {

    private volatile DetailfilmActivity screen;
    private String filmId;

    public GetCommentsTask(DetailfilmActivity s, String filmId) {
        this.screen = s;
        this.filmId = filmId;
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
        System.out.println(">>>onPostExecute GetCommentsTask / resultat="+resultat);
        this.screen.mettreAJourCommentairesApresAppelRest(resultat);
    }

    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";
        try {
            // Appel POST avec le filmId dans le body
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization", screen.getString(R.string.api_token));
            urlConnection.setDoOutput(true);

            // Créer le JSON body avec filmId
            String jsonInputString = "{\"filmId\": " + filmId + "}";

            // Écrire le body
            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>GetCommentsTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            // Lecture du résultat
            int codeCaractere = -1;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>GetCommentsTask - Résultat obtenu : " + sResultatAppel.substring(0, Math.min(100, sResultatAppel.length())));
        } catch (IOException ioe) {
            Log.d("mydebug", ">>>GetCommentsTask - IOException ioe =" + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug",">>>GetCommentsTask - Exception="+e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
