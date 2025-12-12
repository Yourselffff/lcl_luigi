package com.example.applicationrftg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBarLogin;
    private TextView tvErreurLogin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser le SessionManager
        sessionManager = new SessionManager(this);

        // Vérifier si l'utilisateur est déjà connecté
        if (sessionManager.isLoggedIn()) {
            // Rediriger directement vers la liste des films
            Intent intent = new Intent(LoginActivity.this, ListefilmsActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialiser les vues
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        tvErreurLogin = findViewById(R.id.tvErreurLogin);

        // Listener sur le bouton de connexion
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connecter();
            }
        });
    }

    private void connecter() {
        // Récupérer les valeurs des champs
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation des champs
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

        // Masquer le message d'erreur et afficher le loader
        tvErreurLogin.setVisibility(View.GONE);
        progressBarLogin.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Encrypter le mot de passe en MD5
        String passwordEncrypte = encrypterChaineMD5(password);

        // Log pour voir le mot de passe encrypté
        Log.d("mydebug", ">>>Mot de passe encrypté en MD5: " + passwordEncrypte);

        // Créer l'objet LoginRequest avec le mot de passe encrypté
        LoginRequest loginRequest = new LoginRequest(email, passwordEncrypte);

        // Convertir en JSON
        Gson gson = new Gson();
        String jsonBody = gson.toJson(loginRequest);

        // Appel REST
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL("http://10.0.2.2:8180/customers/verify");
            new LoginTask(this, jsonBody).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>Pour LoginTask - MalformedURLException mue=" + mue.toString());
            progressBarLogin.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            tvErreurLogin.setText("Erreur de connexion");
            tvErreurLogin.setVisibility(View.VISIBLE);
        } finally {
            urlAAppeler = null;
        }
    }

    public void mettreAJourActivityApresAppelRest(String resultatAppelRest) {
        // Masquer le loader
        progressBarLogin.setVisibility(View.GONE);
        btnLogin.setEnabled(true);

        Log.d("mydebug", ">>>Pour LoginActivity - resultat=" + resultatAppelRest);

        // Parser la réponse JSON
        try {
            Gson gson = new Gson();
            Map<String, Object> response = gson.fromJson(resultatAppelRest, Map.class);

            // Récupérer le customerId (retourné comme Double par Gson)
            Double customerIdDouble = (Double) response.get("customerId");

            if (customerIdDouble != null) {
                int customerId = customerIdDouble.intValue();

                if (customerId > 0) {
                    // Connexion réussie (customerId positif)
                    Log.d("mydebug", ">>>Connexion réussie - customerId=" + customerId);

                    // Sauvegarder la session
                    sessionManager.createLoginSession(customerId);

                    // Rediriger vers la liste des films
                    Intent intent = new Intent(LoginActivity.this, ListefilmsActivity.class);
                    startActivity(intent);
                    finish(); // Fermer l'activité de connexion
                } else {
                    // Échec de la connexion (customerId = -1)
                    Log.d("mydebug", ">>>Échec de la connexion - customerId=" + customerId);
                    tvErreurLogin.setText("Email ou mot de passe incorrect");
                    tvErreurLogin.setVisibility(View.VISIBLE);
                }
            } else {
                // Réponse invalide
                Log.e("mydebug", ">>>customerId est null dans la réponse");
                tvErreurLogin.setText("Erreur de connexion");
                tvErreurLogin.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e("mydebug", ">>>Erreur lors du parsing de la réponse: " + e.toString());
            tvErreurLogin.setText("Erreur de connexion");
            tvErreurLogin.setVisibility(View.VISIBLE);
        }
    }

    // ENCRYPTAGE EN MD5
    private String encrypterChaineMD5(String chaine) {
        byte[] chaineBytes = chaine.getBytes();
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(chaineBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer hashString = new StringBuffer();
        for (int i=0; i<hash.length; ++i ) {
            String hex = Integer.toHexString(hash[i]);
            if (hex.length() == 1) {
                hashString.append('0');
                hashString.append(hex.charAt(hex.length()-1));
            }
            else {
                hashString.append(hex.substring(hex.length()-2));
            }
        }
        return hashString.toString();
    }
}
