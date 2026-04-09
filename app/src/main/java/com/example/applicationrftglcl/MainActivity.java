package com.example.applicationrftglcl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity principale — point d'entrée historique de l'application.
 * Redirige vers ListefilmsActivity via le bouton de connexion.
 * Note : la gestion de l'authentification a été déplacée dans LoginActivity.
 */
public class MainActivity extends AppCompatActivity {

    /** Champs de saisie (identifiant et mot de passe) non utilisés dans cette version. */
    private EditText etIdentifiant, etMdp;

    /**
     * Initialise l'Activity et charge le layout principal.
     *
     * @param savedInstanceState état sauvegardé de l'instance (rotation, etc.).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Callback du bouton "Connexion" défini dans le layout via android:onClick.
     * Redirige vers la liste des films.
     *
     * @param view vue ayant déclenché l'événement.
     */
    public void onConnexionClicked(View view) {
        startActivity(new Intent(this, ListefilmsActivity.class));
    }
}
