package com.example.applicationrftg;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_CUSTOMER_ID = "customerId";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

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

    // Déconnecter l'utilisateur
    public void logout() {
        editor.clear();
        editor.commit();
    }
}
