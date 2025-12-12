package com.example.applicationrftg;

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

public class CheckAvailabilityTask extends AsyncTask<URL, Integer, Boolean> {

    private Button btnAjouter;
    private String filmId;

    public CheckAvailabilityTask(Button btnAjouter, String filmId) {
        this.btnAjouter = btnAjouter;
        this.filmId = filmId;
    }

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
            urlConnection.setRequestProperty("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.e30.jg2m4pLbAlZv1h5uPQ6fU38X23g65eXMX8q-SXuIPDg");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResult = response.toString();

                // Parser le JSON pour obtenir la liste des inventaires
                Gson gson = new Gson();
                Type inventoryListType = new TypeToken<ArrayList<Inventory>>(){}.getType();
                ArrayList<Inventory> inventories = gson.fromJson(jsonResult, inventoryListType);

                // Si la liste n'est pas vide, le film est disponible
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

    @Override
    protected void onPostExecute(Boolean isAvailable) {
        if (btnAjouter != null) {
            btnAjouter.setEnabled(isAvailable);

            // Changer l'apparence du bouton selon la disponibilité
            if (isAvailable) {
                btnAjouter.setText("Ajouter");
                btnAjouter.setAlpha(1.0f);
            } else {
                btnAjouter.setText("Indisponible");
                btnAjouter.setAlpha(0.5f);
            }
        }
    }
}
