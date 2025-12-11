package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginTask extends AsyncTask<URL, Integer, String> {

    private LoginActivity activityDAppel;
    private String jsonBody;

    public LoginTask(LoginActivity activityDAppel, String jsonBody) {
        this.activityDAppel = activityDAppel;
        this.jsonBody = jsonBody;
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        String resultatAppelRest = "";
        HttpURLConnection urlConnection = null;

        try {
            // Configurer la connexion
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            // Envoyer le JSON dans le corps de la requête
            OutputStream os = urlConnection.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            // Lire la réponse
            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>Pour LoginTask - responseCode=" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                resultatAppelRest = response.toString();
                Log.d("mydebug", ">>>Pour LoginTask - resultat=" + resultatAppelRest);
            } else {
                Log.e("mydebug", ">>>Erreur HTTP : " + responseCode);
                resultatAppelRest = "{\"success\":false}";
            }

        } catch (Exception e) {
            Log.e("mydebug", ">>>Pour LoginTask - Exception e=" + e.toString());
            resultatAppelRest = "{\"success\":false}";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return resultatAppelRest;
    }

    @Override
    protected void onPostExecute(String result) {
        activityDAppel.mettreAJourActivityApresAppelRest(result);
    }
}
