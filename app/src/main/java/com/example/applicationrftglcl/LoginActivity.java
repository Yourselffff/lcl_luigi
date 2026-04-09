package com.example.applicationrftglcl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Map;

/**
 * Activity d'authentification de l'utilisateur.
 * Collecte l'email et le mot de passe, hashe le mot de passe en MD5,
 * puis appelle l'API REST (/customers/verify) via LoginTask.
 * En cas de succès, persiste la session et redirige vers ListefilmsActivity.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBarLogin;
    private TextView tvErreurLogin;
    private Spinner spinnerServeur;
    private EditText etUrlPersonnalisee;

    /** Gestionnaire de session pour persister le customerId et l'URL serveur. */
    private SessionManager sessionManager;

    /**
     * Initialise l'Activity, vérifie si une session active existe et configure les vues.
     * Si l'utilisateur est déjà connecté, redirige directement vers la liste des films.
     *
     * @param savedInstanceState état sauvegardé de l'instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        // Redirection automatique si session déjà active
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, ListefilmsActivity.class));
            finish();
            return;
        }

        // Liaison des vues avec les éléments du layout
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        tvErreurLogin = findViewById(R.id.tvErreurLogin);
        spinnerServeur = findViewById(R.id.spinnerServeur);
        etUrlPersonnalisee = findViewById(R.id.etUrlPersonnalisee);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connecter();
            }
        });
    }

    /**
     * Orchestre le processus de connexion :
     * validation des champs, hashage MD5 du mot de passe,
     * détermination de l'URL serveur, puis lancement de LoginTask.
     */
    private void connecter() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation basique des champs avant appel réseau
        if (email.isEmpty()) {
            tvErreurLogin.setText("Veuillez entrer votre email");
            tvErreurLogin.setVisibility(View.VISIBLE);
            return;
        }
        if (password.isEmpty()) {
            tvErreurLogin.setText("Veuillez entrer votre mot de passe");
            tvErreurLogin.setVisibility(View.VISIBLE);
            return;
        }

        // Masquer le message d'erreur et afficher l'indicateur de chargement
        tvErreurLogin.setVisibility(View.GONE);
        progressBarLogin.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Hashage MD5 du mot de passe avant envoi à l'API
        String passwordEncrypte = encrypterChaineMD5(password);
        Log.d("mydebug", ">>>Mot de passe hashé en MD5: " + passwordEncrypte);

        // Sérialisation de la requête en JSON
        LoginRequest loginRequest = new LoginRequest(email, passwordEncrypte);
        Gson gson = new Gson();
        String jsonBody = gson.toJson(loginRequest);

        // Priorité au champ URL personnalisée, sinon valeur du Spinner
        String urlPersonnalisee = etUrlPersonnalisee.getText().toString().trim();
        String baseUrl;
        if (!urlPersonnalisee.isEmpty()) {
            baseUrl = urlPersonnalisee;
            Log.d("mydebug", ">>>URL personnalisée utilisée: " + baseUrl);
        } else {
            baseUrl = spinnerServeur.getSelectedItem().toString();
            Log.d("mydebug", ">>>URL du Spinner utilisée: " + baseUrl);
        }
        sessionManager.saveBaseUrl(baseUrl);

        // Lancement de la tâche asynchrone d'authentification
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(baseUrl + "/customers/verify");
            new LoginTask(this, jsonBody).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>LoginTask - MalformedURLException: " + mue.toString());
            progressBarLogin.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            tvErreurLogin.setText("Erreur de connexion");
            tvErreurLogin.setVisibility(View.VISIBLE);
        } finally {
            urlAAppeler = null;
        }
    }

    /**
     * Callback appelé par LoginTask après réception de la réponse API.
     * Parse la réponse JSON, vérifie le customerId et redirige si connexion réussie.
     *
     * @param resultatAppelRest JSON retourné par l'API (contient "customerId").
     */
    public void mettreAJourActivityApresAppelRest(String resultatAppelRest) {
        progressBarLogin.setVisibility(View.GONE);
        btnLogin.setEnabled(true);

        Log.d("mydebug", ">>>LoginActivity - resultat=" + resultatAppelRest);

        try {
            Gson gson = new Gson();
            Map<String, Object> response = gson.fromJson(resultatAppelRest, Map.class);

            // Gson désérialise les nombres en Double par défaut
            Double customerIdDouble = (Double) response.get("customerId");

            if (customerIdDouble != null) {
                int customerId = customerIdDouble.intValue();

                if (customerId > 0) {
                    // Connexion réussie : persistance de la session et redirection
                    Log.d("mydebug", ">>>Connexion réussie - customerId=" + customerId);
                    sessionManager.createLoginSession(customerId);
                    startActivity(new Intent(LoginActivity.this, ListefilmsActivity.class));
                    finish();
                } else {
                    // customerId = -1 : identifiants incorrects
                    Log.d("mydebug", ">>>Échec de la connexion - customerId=" + customerId);
                    tvErreurLogin.setText("Email ou mot de passe incorrect");
                    tvErreurLogin.setVisibility(View.VISIBLE);
                }
            } else {
                // Réponse inattendue de l'API
                Log.e("mydebug", ">>>customerId est null dans la réponse");
                tvErreurLogin.setText("Erreur de connexion");
                tvErreurLogin.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e("mydebug", ">>>Erreur parsing réponse: " + e.toString());
            tvErreurLogin.setText("Erreur de connexion");
            tvErreurLogin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hashe une chaîne de caractères en MD5 pour sécuriser le mot de passe avant envoi.
     * Chaque octet est converti en sa représentation hexadécimale sur 2 caractères.
     *
     * @param chaine chaîne à hasher (mot de passe en clair).
     * @return représentation hexadécimale du hash MD5.
     */
    private String encrypterChaineMD5(String chaine) {
        byte[] chaineBytes = chaine.getBytes();
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(chaineBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer hashString = new StringBuffer();
        for (int i = 0; i < hash.length; ++i) {
            String hex = Integer.toHexString(hash[i]);
            if (hex.length() == 1) {
                // Compléter à 2 caractères si l'octet est < 0x10
                hashString.append('0');
                hashString.append(hex.charAt(hex.length() - 1));
            } else {
                hashString.append(hex.substring(hex.length() - 2));
            }
        }
        return hashString.toString();
    }
}
