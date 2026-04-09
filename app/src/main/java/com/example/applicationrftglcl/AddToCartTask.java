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
 * Tâche asynchrone ajoutant un film au panier via POST /cart/add.
 * Compatible avec DetailfilmActivity et ListefilmsActivity grâce à un typage Object.
 * Délègue le callback à l'Activity appelante selon son type réel.
 */
public class AddToCartTask extends AsyncTask<URL, Integer, String> {

    /**
     * Référence volatile à l'Activity appelante.
     * Déclaré Object pour accepter à la fois DetailfilmActivity et ListefilmsActivity.
     */
    private volatile Object screen;

    /** Identifiant du film à ajouter au panier. */
    private String filmId;

    /** Identifiant du client connecté. */
    private String customerId;

    /**
     * Constructeur de la tâche.
     *
     * @param s          Activity appelante (DetailfilmActivity ou ListefilmsActivity).
     * @param filmId     identifiant du film à ajouter.
     * @param customerId identifiant du client connecté.
     */
    public AddToCartTask(Object s, String filmId, String customerId) {
        this.screen = s;
        this.filmId = filmId;
        this.customerId = customerId;
    }

    /** Pré-traitement avant l'exécution (non utilisé ici). */
    @Override
    protected void onPreExecute() {
        // Réservé pour d'éventuelles initialisations avant l'appel réseau
    }

    /**
     * Exécuté sur le thread de fond — effectue le POST vers /cart/add.
     *
     * @param urls URL cible (index 0 = /cart/add).
     * @return JSON retourné par l'API, ou "ERROR" en cas d'échec.
     */
    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        return appelerServiceRestHttp(urlAAppeler);
    }

    /**
     * Exécuté sur le thread principal après doInBackground.
     * Détermine le type de l'Activity appelante et appelle le callback approprié.
     *
     * @param resultat JSON retourné par l'API ou "ERROR".
     */
    @Override
    protected void onPostExecute(String resultat) {
        System.out.println(">>>onPostExecute AddToCartTask / resultat=" + resultat);

        // Dispatch vers la bonne Activity selon son type
        if (screen instanceof DetailfilmActivity) {
            ((DetailfilmActivity) screen).filmAjouteAuPanierAvecSucces(resultat);
        } else if (screen instanceof ListefilmsActivity) {
            ((ListefilmsActivity) screen).filmAjouteAuPanierAvecSucces(resultat);
        }
    }

    /**
     * Effectue l'appel HTTP POST avec le JSON {customerId, filmId} dans le corps.
     * Le token d'autorisation est lu depuis strings.xml.
     *
     * @param urlAAppeler URL du service REST à appeler.
     * @return corps de la réponse, ou "ERROR" en cas d'exception.
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
            urlConnection.setRequestProperty("Authorization", ((android.app.Activity) screen).getString(R.string.api_token));
            urlConnection.setDoOutput(true);

            // Construcción du corps JSON avec customerId et filmId
            String jsonInputString = "{\"customerId\": " + customerId +
                    ", \"filmId\": " + filmId + "}";
            Log.d("mydebug", ">>>AddToCartTask - JSON envoyé : " + jsonInputString);

            // Envoi du corps JSON
            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>AddToCartTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            int codeCaractere;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>AddToCartTask - Résultat obtenu : " + sResultatAppel);

        } catch (IOException ioe) {
            Log.d("mydebug", ">>>AddToCartTask - IOException: " + ioe.toString());
            sResultatAppel = "ERROR";
        } catch (Exception e) {
            Log.d("mydebug", ">>>AddToCartTask - Exception: " + e.toString());
            sResultatAppel = "ERROR";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
