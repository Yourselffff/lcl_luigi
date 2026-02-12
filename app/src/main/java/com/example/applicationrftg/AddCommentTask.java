package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddCommentTask extends AsyncTask<URL,Integer,String> {

    private volatile DetailfilmActivity screen;
    private String filmId;
    private String customerId;
    private String commentText;

    public AddCommentTask(DetailfilmActivity s, String filmId, String customerId, String commentText) {
        this.screen = s;
        this.filmId = filmId;
        this.customerId = customerId;
        this.commentText = commentText;
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
        System.out.println(">>>onPostExecute AddCommentTask / resultat="+resultat);
        this.screen.commentaireAjouteAvecSucces(resultat);
    }

    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";
        try {
            // Appel POST avec les données du commentaire
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization", screen.getString(R.string.api_token));
            urlConnection.setDoOutput(true);

        
            String escapedCommentText = commentText
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");

            String jsonInputString = "{\"filmId\": " + filmId +
                    ", \"customerId\": " + customerId +
                    ", \"commentText\": \"" + escapedCommentText + "\"}";

            Log.d("mydebug", ">>>AddCommentTask - JSON envoyé : " + jsonInputString);

           
            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>AddCommentTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            int codeCaractere = -1;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>AddCommentTask - Résultat obtenu : " + sResultatAppel);
        } catch (IOException ioe) {
            Log.d("mydebug", ">>>AddCommentTask - IOException ioe =" + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug",">>>AddCommentTask - Exception="+e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
