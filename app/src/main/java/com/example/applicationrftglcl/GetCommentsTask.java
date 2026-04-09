package com.example.applicationrftglcl;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Tâche asynchrone récupérant les commentaires d'un film via POST /films/commentaire.
 * Envoie l'identifiant du film dans le corps JSON et retourne la liste des commentaires.
 * Notifie DetailfilmActivity via le callback mettreAJourCommentairesApresAppelRest.
 */
public class GetCommentsTask extends AsyncTask<URL, Integer, String> {

    /** Référence volatile à DetailfilmActivity pour le callback de résultat. */
    private volatile DetailfilmActivity screen;

    /** Identifiant du film dont on récupère les commentaires. */
    private String filmId;

    /**
     * Constructeur de la tâche.
     *
     * @param s      DetailfilmActivity qui recevra les commentaires.
     * @param filmId identifiant du film concerné.
     */
    public GetCommentsTask(DetailfilmActivity s, String filmId) {
        this.screen = s;
        this.filmId = filmId;
    }

    /** Pré-traitement avant l'exécution (non utilisé ici). */
    @Override
    protected void onPreExecute() {
        // Réservé pour d'éventuelles initialisations avant l'appel réseau
    }

    /**
     * Exécuté sur le thread de fond — effectue le POST vers /films/commentaire.
     *
     * @param urls URL cible (index 0 = /films/commentaire).
     * @return JSON contenant la liste des commentaires, ou chaîne vide en cas d'erreur.
     */
    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        return appelerServiceRestHttp(urlAAppeler);
    }

    /**
     * Exécuté sur le thread principal — transmet les commentaires à DetailfilmActivity.
     *
     * @param resultat JSON des commentaires retourné par l'API.
     */
    @Override
    protected void onPostExecute(String resultat) {
        System.out.println(">>>onPostExecute GetCommentsTask / resultat=" + resultat);
        this.screen.mettreAJourCommentairesApresAppelRest(resultat);
    }

    /**
     * Effectue l'appel HTTP POST avec {filmId} dans le corps JSON.
     * L'API retourne la liste des commentaires associés à ce film.
     *
     * @param urlAAppeler URL du service REST à appeler.
     * @return corps de la réponse HTTP, ou chaîne vide en cas d'erreur.
     */
    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        String sResultatAppel = "";
        try {
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization", screen.getString(R.string.api_token));
            urlConnection.setDoOutput(true);

            // Corps JSON contenant l'identifiant du film
            String jsonInputString = "{\"filmId\": " + filmId + "}";
            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>GetCommentsTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            int codeCaractere;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>GetCommentsTask - Résultat obtenu : " + sResultatAppel.substring(0, Math.min(100, sResultatAppel.length())));

        } catch (IOException ioe) {
            Log.d("mydebug", ">>>GetCommentsTask - IOException: " + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug", ">>>GetCommentsTask - Exception: " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
