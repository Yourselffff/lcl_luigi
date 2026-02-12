package com.example.applicationrftg;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_CUSTOMER_ID = "customerId";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_BASE_URL = "baseUrl";
    private static final String DEFAULT_BASE_URL = "http://10.0.2.2:8180";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Sauvegarder la session utilisateur
    public void createLoginSession(int customerId) {
        editor.putInt(KEY_CUSTOMER_ID, customerId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    // Vérifier si l'utilisateur est connecté
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Récupérer l'ID de l'utilisateur connecté
    public int getCustomerId() {
        return sharedPreferences.getInt(KEY_CUSTOMER_ID, -1);
    }

    // Sauvegarder l'URL de base de l'API choisie
    public void saveBaseUrl(String baseUrl) {
        editor.putString(KEY_BASE_URL, baseUrl);
        editor.commit();
    }

    // Récupérer l'URL de base de l'API
    public String getBaseUrl() {
        return sharedPreferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }

    // Déconnecter l'utilisateur (on garde l'URL choisie)
    public void logout() {
        String baseUrl = getBaseUrl();
        editor.clear();
        editor.putString(KEY_BASE_URL, baseUrl);
        editor.commit();
    }
}
