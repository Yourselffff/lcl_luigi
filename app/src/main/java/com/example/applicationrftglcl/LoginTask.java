package com.example.applicationrftglcl;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Tâche asynchrone gérant l'appel REST POST vers /customers/verify.
 * Hérite de AsyncTask pour exécuter l'appel réseau hors du thread principal
 * et retourner le résultat JSON à LoginActivity via onPostExecute.
 */
public class LoginTask extends AsyncTask<URL, Integer, String> {

    /** Référence à l'Activity appelante pour le callback de résultat. */
    private LoginActivity activityDAppel;

    /** Corps JSON de la requête (email + mot de passe hashé). */
    private String jsonBody;

    /**
     * Constructeur de la tâche.
     *
     * @param activityDAppel Activity qui recevra le résultat.
     * @param jsonBody       corps JSON à envoyer dans la requête POST.
     */
    public LoginTask(LoginActivity activityDAppel, String jsonBody) {
        this.activityDAppel = activityDAppel;
        this.jsonBody = jsonBody;
    }

    /**
     * Exécuté sur le thread de fond — effectue l'appel HTTP POST.
     * Configure la connexion, envoie le JSON, lit et retourne la réponse.
     *
     * @param urls URL cible (index 0 = /customers/verify).
     * @return JSON retourné par l'API, ou "{\"success\":false}" en cas d'erreur.
     */
    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        String resultatAppelRest = "";
        HttpURLConnection urlConnection = null;

        try {
            // Configuration de la connexion HTTP POST
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            // Envoi du corps JSON
            OutputStream os = urlConnection.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>LoginTask - responseCode=" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Lecture de la réponse ligne par ligne
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                resultatAppelRest = response.toString();
                Log.d("mydebug", ">>>LoginTask - resultat=" + resultatAppelRest);
            } else {
                Log.e("mydebug", ">>>Erreur HTTP : " + responseCode);
                resultatAppelRest = "{\"success\":false}";
            }

        } catch (Exception e) {
            Log.e("mydebug", ">>>LoginTask - Exception: " + e.toString());
            resultatAppelRest = "{\"success\":false}";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return resultatAppelRest;
    }

    /**
     * Exécuté sur le thread principal après doInBackground.
     * Transmet le résultat JSON à LoginActivity pour traitement.
     *
     * @param result JSON retourné par l'API.
     */
    @Override
    protected void onPostExecute(String result) {
        activityDAppel.mettreAJourActivityApresAppelRest(result);
    }
}
