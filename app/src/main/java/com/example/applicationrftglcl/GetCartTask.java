package com.example.applicationrftglcl;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Tâche asynchrone récupérant le contenu du panier via GET /cart/{customerId}.
 * Compatible avec PanierActivity et ListefilmsActivity grâce à un typage Object.
 * Délègue le callback à l'Activity appelante selon son type réel.
 */
public class GetCartTask extends AsyncTask<URL, Integer, String> {

    /**
     * Référence volatile à l'Activity appelante.
     * Déclaré Object pour accepter PanierActivity et ListefilmsActivity.
     */
    private volatile Object screen;

    /** Identifiant du client dont on récupère le panier. */
    private String customerId;

    /**
     * Constructeur de la tâche.
     *
     * @param s          Activity appelante (PanierActivity ou ListefilmsActivity).
     * @param customerId identifiant du client connecté.
     */
    public GetCartTask(Object s, String customerId) {
        this.screen = s;
        this.customerId = customerId;
    }

    /** Pré-traitement avant l'exécution (non utilisé ici). */
    @Override
    protected void onPreExecute() {
        // Réservé pour d'éventuelles initialisations avant l'appel réseau
    }

    /**
     * Exécuté sur le thread de fond — effectue le GET vers /cart/{customerId}.
     *
     * @param urls URL cible (index 0 = /cart/{customerId}).
     * @return JSON de la liste des CartItem, ou chaîne vide en cas d'erreur.
     */
    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        return appelerServiceRestHttp(urlAAppeler);
    }

    /**
     * Exécuté sur le thread principal — dispatch le résultat vers la bonne Activity.
     *
     * @param resultat JSON du panier retourné par l'API.
     */
    @Override
    protected void onPostExecute(String resultat) {
        System.out.println(">>>onPostExecute GetCartTask / resultat=" + resultat);

        // Dispatch vers la bonne Activity selon son type
        if (screen instanceof PanierActivity) {
            ((PanierActivity) screen).mettreAJourPanierApresAppelRest(resultat);
        } else if (screen instanceof ListefilmsActivity) {
            ((ListefilmsActivity) screen).mettreAJourPanierApresAppelRest(resultat);
        }
    }

    /**
     * Effectue l'appel HTTP GET et retourne la réponse sous forme de chaîne.
     *
     * @param urlAAppeler URL du service REST à appeler.
     * @return corps de la réponse HTTP, ou chaîne vide en cas d'erreur.
     */
    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        String sResultatAppel = "";
        try {
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization", ((android.app.Activity) screen).getString(R.string.api_token));

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>GetCartTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            int codeCaractere;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>GetCartTask - Résultat obtenu : " + sResultatAppel);

        } catch (IOException ioe) {
            Log.d("mydebug", ">>>GetCartTask - IOException: " + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug", ">>>GetCartTask - Exception: " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
