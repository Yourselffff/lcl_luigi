package com.example.applicationrftglcl;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gestionnaire de session utilisateur basé sur SharedPreferences.
 * Persiste l'état de connexion (customerId, isLoggedIn) et l'URL du serveur
 * entre les redémarrages de l'application.
 */
public class SessionManager {

    /** Nom du fichier SharedPreferences utilisé pour la session. */
    private static final String PREF_NAME = "UserSession";

    /** Clé pour l'identifiant du client connecté. */
    private static final String KEY_CUSTOMER_ID = "customerId";

    /** Clé pour l'état de connexion (booléen). */
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    /** Clé pour l'URL de base de l'API choisie par l'utilisateur. */
    private static final String KEY_BASE_URL = "baseUrl";

    /** URL par défaut pointant vers l'émulateur Android (10.0.2.2 = localhost hôte). */
    private static final String DEFAULT_BASE_URL = "http://10.0.2.2:8180";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    /**
     * Constructeur : initialise les SharedPreferences en mode privé.
     *
     * @param context contexte Android (Activity ou Application).
     */
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Enregistre la session après une connexion réussie.
     * Stocke le customerId et positionne le flag isLoggedIn à true.
     *
     * @param customerId identifiant retourné par l'API après authentification.
     */
    public void createLoginSession(int customerId) {
        editor.putInt(KEY_CUSTOMER_ID, customerId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    /**
     * Vérifie si une session active existe.
     *
     * @return true si l'utilisateur est connecté, false sinon.
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Récupère l'identifiant du client connecté.
     *
     * @return le customerId, ou -1 si aucune session active.
     */
    public int getCustomerId() {
        return sharedPreferences.getInt(KEY_CUSTOMER_ID, -1);
    }

    /**
     * Sauvegarde l'URL de base de l'API sélectionnée par l'utilisateur.
     * Cette URL est conservée même après déconnexion.
     *
     * @param baseUrl URL du serveur (ex. : "http://192.168.1.10:8180").
     */
    public void saveBaseUrl(String baseUrl) {
        editor.putString(KEY_BASE_URL, baseUrl);
        editor.commit();
    }

    /**
     * Récupère l'URL de base de l'API.
     *
     * @return l'URL enregistrée, ou l'URL par défaut (émulateur) si absente.
     */
    public String getBaseUrl() {
        return sharedPreferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }

    /**
     * Déconnecte l'utilisateur en effaçant la session.
     * L'URL du serveur est conservée pour faciliter la prochaine connexion.
     */
    public void logout() {
        // Conserver l'URL choisie avant d'effacer les préférences
        String baseUrl = getBaseUrl();
        editor.clear();
        editor.putString(KEY_BASE_URL, baseUrl);
        editor.commit();
    }
}
