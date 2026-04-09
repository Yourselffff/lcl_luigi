package com.example.applicationrftglcl;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Tâche asynchrone récupérant le détail d'un film via GET /films/{id}.
 * Hérite de AsyncTask pour exécuter l'appel réseau hors du thread principal.
 * Une fois la réponse reçue, délègue l'affichage à DetailfilmActivity.
 */
public class DetailfilmTask extends AsyncTask<URL, Integer, String> {

    /** Référence volatile à l'Activity pour éviter les fuites mémoire lors des rotations. */
    private volatile DetailfilmActivity screen;

    /**
     * Constructeur de la tâche.
     *
     * @param s Activity appelante qui recevra le résultat JSON.
     */
    public DetailfilmTask(DetailfilmActivity s) {
        this.screen = s;
    }

    /** Pré-traitement avant l'exécution (non utilisé ici). */
    @Override
    protected void onPreExecute() {
        // Réservé pour d'éventuelles initialisations avant l'appel réseau
    }

    /**
     * Exécuté sur le thread de fond — lance l'appel HTTP GET.
     *
     * @param urls URL cible (index 0 = /films/{id}).
     * @return JSON du film, ou chaîne vide en cas d'erreur.
     */
    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        return appelerServiceRestHttp(urlAAppeler);
    }

    /**
     * Exécuté sur le thread principal après doInBackground.
     * Transmet le JSON à DetailfilmActivity pour affichage.
     *
     * @param resultat JSON du film retourné par l'API.
     */
    @Override
    protected void onPostExecute(String resultat) {
        System.out.println(">>>onPostExecute / resultat=" + resultat);
        this.screen.mettreAJourActivityApresAppelRest(resultat);
    }

    /**
     * Effectue l'appel HTTP GET et retourne la réponse sous forme de chaîne.
     * L'en-tête Authorization utilise le token défini dans strings.xml.
     *
     * @param urlAAppeler URL du service REST à appeler.
     * @return corps de la réponse HTTP, ou chaîne vide en cas d'erreur.
     */
    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";
        try {
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization", screen.getString(R.string.api_token));

            responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            // Lecture du flux octet par octet et reconstruction de la chaîne
            int codeCaractere;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>Résultat obtenu : " + sResultatAppel.substring(0, Math.min(100, sResultatAppel.length())));

        } catch (IOException ioe) {
            Log.d("mydebug", ">>>appelerServiceRestHttp - IOException: " + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug", ">>>appelerServiceRestHttp - Exception: " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
