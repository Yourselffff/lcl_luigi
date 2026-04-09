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
 * Tâche asynchrone validant le panier via POST /cart/checkout.
 * Change le statut des locations du client de 2 (panier) à 3 (réservé).
 * Notifie PanierActivity du résultat via le callback panierValideAvecSucces.
 */
public class CheckoutCartTask extends AsyncTask<URL, Integer, String> {

    /** Référence volatile à PanierActivity pour le callback de résultat. */
    private volatile PanierActivity screen;

    /** Identifiant du client dont on valide le panier. */
    private String customerId;

    /**
     * Constructeur de la tâche.
     *
     * @param s          PanierActivity qui recevra le résultat.
     * @param customerId identifiant du client connecté.
     */
    public CheckoutCartTask(PanierActivity s, String customerId) {
        this.screen = s;
        this.customerId = customerId;
    }

    /** Pré-traitement avant l'exécution (non utilisé ici). */
    @Override
    protected void onPreExecute() {
        // Réservé pour d'éventuelles initialisations avant l'appel réseau
    }

    /**
     * Exécuté sur le thread de fond — effectue le POST vers /cart/checkout.
     *
     * @param urls URL cible (index 0 = /cart/checkout).
     * @return réponse de l'API, ou "ERROR" en cas d'exception.
     */
    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        return appelerServiceRestHttp(urlAAppeler);
    }

    /**
     * Exécuté sur le thread principal — notifie PanierActivity du résultat.
     *
     * @param resultat réponse de l'API ou "ERROR".
     */
    @Override
    protected void onPostExecute(String resultat) {
        System.out.println(">>>onPostExecute CheckoutCartTask / resultat=" + resultat);
        this.screen.panierValideAvecSucces(resultat);
    }

    /**
     * Effectue l'appel HTTP POST avec le JSON {customerId} dans le corps.
     * Passe le statut des locations du client de 2 à 3.
     *
     * @param urlAAppeler URL du service REST à appeler.
     * @return corps de la réponse HTTP, ou "ERROR" en cas d'exception.
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

            // Corps JSON contenant uniquement l'identifiant du client
            String jsonInputString = "{\"customerId\": " + customerId + "}";
            Log.d("mydebug", ">>>CheckoutCartTask - JSON envoyé : " + jsonInputString);

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>CheckoutCartTask - Code de réponse HTTP : " + responseCode);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            int codeCaractere;
            while ((codeCaractere = in.read()) != -1) {
                sResultatAppel = sResultatAppel + (char) codeCaractere;
            }
            in.close();
            Log.d("mydebug", ">>>CheckoutCartTask - Résultat obtenu : " + sResultatAppel);

        } catch (IOException ioe) {
            Log.d("mydebug", ">>>CheckoutCartTask - IOException: " + ioe.toString());
            sResultatAppel = "ERROR";
        } catch (Exception e) {
            Log.d("mydebug", ">>>CheckoutCartTask - Exception: " + e.toString());
            sResultatAppel = "ERROR";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }
}
