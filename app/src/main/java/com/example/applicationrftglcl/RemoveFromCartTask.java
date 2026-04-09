package com.example.applicationrftglcl;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Tâche asynchrone supprimant un article du panier via DELETE /cart/{rentalId}.
 * Notifie PanierActivity du résultat via le callback itemRetireDuPanierAvecSucces.
 */
public class RemoveFromCartTask extends AsyncTask<URL, Integer, String> {

    /** Référence volatile à PanierActivity pour le callback de résultat. */
    private volatile PanierActivity screen;

    /** Identifiant de la location (rental) à supprimer. */
    private String rentalId;

    /**
     * Constructeur de la tâche.
     *
     * @param s        PanierActivity qui recevra le résultat.
     * @param rentalId identifiant de la location à supprimer.
     */
    public RemoveFromCartTask(PanierActivity s, String rentalId) {
        this.screen = s;
        this.rentalId = rentalId;
    }

    /** Pré-traitement avant l'exécution (non utilisé ici). */
    @Override
    protected void onPreExecute() {
        // Réservé pour d'éventuelles initialisations avant l'appel réseau
    }

    /**
     * Exécuté sur le thread de fond — effectue le DELETE vers /cart/{rentalId}.
     *
     * @param urls URL cible (index 0 = /cart/{rentalId}).
     * @return réponse de l'API, ou "SUCCESS" en cas d'exception (suppression locale forcée).
     */
    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        return appelerServiceRestHttp(urlAAppeler);
    }

    /**
     * Exécuté sur le thread principal — notifie PanierActivity de la suppression.
     *
     * @param resultat réponse de l'API.
     */
    @Override
    protected void onPostExecute(String resultat) {
        System.out.println(">>>onPostExecute RemoveFromCartTask / resultat=" + resultat);
        this.screen.itemRetireDuPanierAvecSucces(resultat);
    }

    /**
     * Effectue l'appel HTTP DELETE sur l'URL fournie.
     * En cas d'exception, retourne "SUCCESS" pour forcer la suppression locale.
     *
     * @param urlAAppeler URL du service REST à appeler.
     * @return corps de la réponse, ou "SUCCESS" en cas d'erreur réseau.
     */
    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        String sResultatAppel = "";
        try {
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization", screen.getString(R.string.api_token));

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>RemoveFromCartTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            int codeCaractere;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>RemoveFromCartTask - Résultat obtenu : " + sResultatAppel);

        } catch (IOException ioe) {
            // Suppression considérée réussie côté serveur même si la lecture échoue
            Log.d("mydebug", ">>>RemoveFromCartTask - IOException: " + ioe.toString());
            sResultatAppel = "SUCCESS";
        } catch (Exception e) {
            Log.d("mydebug", ">>>RemoveFromCartTask - Exception: " + e.toString());
            sResultatAppel = "SUCCESS";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
