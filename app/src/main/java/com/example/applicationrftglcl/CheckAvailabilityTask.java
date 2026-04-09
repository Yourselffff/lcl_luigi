package com.example.applicationrftglcl;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Tâche asynchrone vérifiant la disponibilité d'un film en stock via GET /inventories/available/film/{id}.
 * Active ou désactive le bouton "Ajouter" de la liste selon le résultat.
 * Retourne un booléen indiquant si au moins un exemplaire est disponible.
 */
public class CheckAvailabilityTask extends AsyncTask<URL, Integer, Boolean> {

    /** Bouton "Ajouter" à activer ou désactiver selon la disponibilité. */
    private Button btnAjouter;

    /** Identifiant du film dont on vérifie la disponibilité. */
    private String filmId;

    /**
     * Constructeur de la tâche.
     *
     * @param btnAjouter bouton à mettre à jour après vérification.
     * @param filmId     identifiant du film à vérifier.
     */
    public CheckAvailabilityTask(Button btnAjouter, String filmId) {
        this.btnAjouter = btnAjouter;
        this.filmId = filmId;
    }

    /**
     * Exécuté sur le thread de fond — appelle l'API et parse la liste d'inventaires.
     * Un film est considéré disponible si la liste retournée n'est pas vide.
     *
     * @param urls URL cible (index 0 = /inventories/available/film/{id}).
     * @return true si au moins un exemplaire est disponible, false sinon.
     */
    @Override
    protected Boolean doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        HttpURLConnection urlConnection = null;
        boolean isAvailable = false;

        try {
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization", btnAjouter.getContext().getString(R.string.api_token));
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Lecture de la réponse
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse la liste des inventaires disponibles
                Gson gson = new Gson();
                Type inventoryListType = new TypeToken<ArrayList<Inventory>>(){}.getType();
                ArrayList<Inventory> inventories = gson.fromJson(response.toString(), inventoryListType);

                // Disponible si au moins un exemplaire existe dans la liste
                isAvailable = inventories != null && !inventories.isEmpty();
            }

        } catch (Exception e) {
            Log.e("mydebug", ">>>CheckAvailabilityTask filmId=" + filmId + " - Exception: " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return isAvailable;
    }

    /**
     * Exécuté sur le thread principal — met à jour l'état du bouton "Ajouter".
     * Si disponible : bouton actif et opaque. Sinon : désactivé et semi-transparent.
     *
     * @param isAvailable true si le film est en stock.
     */
    @Override
    protected void onPostExecute(Boolean isAvailable) {
        if (btnAjouter != null) {
            btnAjouter.setEnabled(isAvailable);
            if (isAvailable) {
                btnAjouter.setText("Ajouter");
                btnAjouter.setAlpha(1.0f);       // Opacité pleine = disponible
            } else {
                btnAjouter.setText("Indisponible");
                btnAjouter.setAlpha(0.5f);        // Semi-transparent = indisponible
            }
        }
    }
}
