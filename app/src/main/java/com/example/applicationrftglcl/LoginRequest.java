package com.example.applicationrftglcl;

/**
 * Modèle de données pour la requête d'authentification.
 * Sérialisé en JSON par Gson avant envoi à l'API (/customers/verify).
 * Le mot de passe doit être hashé en MD5 avant d'être affecté à ce modèle.
 */
public class LoginRequest {

    /** Adresse e-mail de l'utilisateur. */
    private String email;

    /** Mot de passe hashé en MD5. */
    private String password;

    /**
     * Constructeur utilisé pour créer la requête de connexion.
     *
     * @param email    adresse e-mail saisie par l'utilisateur.
     * @param password mot de passe hashé en MD5.
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ── Getters et Setters ────────────────────────────────────────────────────

    /** @return l'adresse e-mail. */
    public String getEmail() { return email; }

    /** @param email adresse e-mail à définir. */
    public void setEmail(String email) { this.email = email; }

    /** @return le mot de passe hashé. */
    public String getPassword() { return password; }

    /** @param password mot de passe hashé à définir. */
    public void setPassword(String password) { this.password = password; }
}
