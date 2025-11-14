package com.example.applicationrftg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;

public class DetailfilmActivity extends AppCompatActivity {

    private String detailFilmResultat = "";
    private String filmId = "";
    private String filmTitle = "";
    private Film filmActuel = null; // Stocker le film chargé
    private ProgressBar progressBarDetail;
    private ScrollView scrollViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailfilm);

        // Initialiser les vues
        progressBarDetail = findViewById(R.id.progressBarDetail);
        scrollViewContent = findViewById(R.id.scrollViewContent);

        // Afficher le loader et cacher le contenu
        progressBarDetail.setVisibility(View.VISIBLE);
        scrollViewContent.setVisibility(View.GONE);

        // Récupérer l'ID du film passé depuis ListefilmsActivity
        Intent intent = getIntent();
        filmId = intent.getStringExtra("FILM_ID");
        filmTitle = intent.getStringExtra("FILM_TITLE");

        Log.d("DetailfilmActivity", "Page détail affichée pour film ID: " + filmId);

        // Appeler le service REST pour récupérer les détails du film
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL("http://10.0.2.2:8180/films/" + filmId);
            new DetailfilmTask(this).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug",">>>Pour DetailfilmTask - MalformedURLException mue="+mue.toString());
            // Masquer le loader en cas d'erreur
            progressBarDetail.setVisibility(View.GONE);
        } finally {
            urlAAppeler = null;
        }
    }

    public void mettreAJourActivityApresAppelRest(String resultatAppelRest) {
        // Masquer le loader et afficher le contenu
        progressBarDetail.setVisibility(View.GONE);
        scrollViewContent.setVisibility(View.VISIBLE);

        detailFilmResultat = resultatAppelRest;
        Log.d("mydebug",">>>Pour DetailfilmActivity - mettreAJourActivityApresAppelRest="+detailFilmResultat);

        // Vérifier que le résultat n'est pas vide avant d'afficher
        if (resultatAppelRest != null && !resultatAppelRest.trim().isEmpty()) {
            afficherDetailFilm(resultatAppelRest);
        } else {
            Log.e("mydebug", ">>>Erreur : Le résultat de l'appel REST est vide ou null");
        }
    }

    // Affichage des détails du film (modèle du cours)
    public void afficherDetailFilm(String filmJson) {
        Gson gson = new Gson();
        Film film = gson.fromJson(filmJson, Film.class);

        // Vérifier que le JSON a été correctement parsé
        if (film == null) {
            Log.e("mydebug", ">>>Erreur : Impossible de parser le JSON en Film");
            return;
        }

        // Stocker le film actuel pour l'ajouter au panier
        this.filmActuel = film;

        // Contrôle
        System.out.println(">>>>Détail du film : " + film.getTitle());

        // Afficher les informations dans les TextViews
        TextView tvTitle = findViewById(R.id.tvFilmTitle);
        TextView tvDescription = findViewById(R.id.tvFilmDescription);
        TextView tvYearAndCategory = findViewById(R.id.tvFilmYearAndCategory);
        TextView tvActor = findViewById(R.id.tvFilmActor);
        TextView tvDirectors = findViewById(R.id.tvFilmDirectors);
        TextView tvActors = findViewById(R.id.tvFilmActors);
        TextView tvCategories = findViewById(R.id.tvFilmCategories);

        // Afficher le titre
        tvTitle.setText(film.getTitle());

        // Afficher la description
        tvDescription.setText(film.getDescription());

        // Construire "Année • Catégorie" (ex: "2023 • Action")
        String yearAndCategory = film.getRelease_year();
        if (film.getCategories() != null && !film.getCategories().isEmpty()) {
            yearAndCategory += " • " + film.getCategories().get(0).toString();
        }
        tvYearAndCategory.setText(yearAndCategory);

        // Afficher le premier acteur principal (en haut avec l'image)
        if (film.getActors() != null && !film.getActors().isEmpty()) {
            tvActor.setText(film.getActors().get(0).toString());
        } else {
            tvActor.setText("");
        }

        // Afficher toutes les catégories
        if (film.getCategories() != null && !film.getCategories().isEmpty()) {
            StringBuilder categoriesText = new StringBuilder();
            for (int i = 0; i < film.getCategories().size(); i++) {
                if (i > 0) categoriesText.append(", ");
                categoriesText.append(film.getCategories().get(i).toString());
            }
            tvCategories.setText(categoriesText.toString());
        } else {
            tvCategories.setText("Aucune catégorie");
        }

        // Afficher tous les réalisateurs
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            StringBuilder directorsText = new StringBuilder();
            for (int i = 0; i < film.getDirectors().size(); i++) {
                if (i > 0) directorsText.append(", ");
                directorsText.append(film.getDirectors().get(i).toString());
            }
            tvDirectors.setText(directorsText.toString());
        } else {
            tvDirectors.setText("Aucun réalisateur");
        }

        // Afficher tous les acteurs
        if (film.getActors() != null && !film.getActors().isEmpty()) {
            StringBuilder actorsText = new StringBuilder();
            for (int i = 0; i < film.getActors().size(); i++) {
                if (i > 0) actorsText.append(", ");
                actorsText.append(film.getActors().get(i).toString());
            }
            tvActors.setText(actorsText.toString());
        } else {
            tvActors.setText("Aucun acteur");
        }

        // Configurer le bouton Commander
        Button btnCommander = findViewById(R.id.btnCommander);
        btnCommander.setOnClickListener(v -> onCommanderClicked());
    }

    // Méthode appelée quand on clique sur "Commander ce film"
    public void onCommanderClicked() {
        if (filmActuel == null) {
            Toast.makeText(this, "Erreur: Film non chargé", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ajouter le film au panier (principe du cours : Singleton)
        Panier.getInstance().ajouterFilm(filmActuel);

        Toast.makeText(this, "Film ajouté au panier", Toast.LENGTH_SHORT).show();
        Log.d("DetailfilmActivity", "Film ajouté au panier: " + filmActuel.getTitle());

        // Ouvrir l'écran du panier
        Intent intent = new Intent(this, PanierActivity.class);
        startActivity(intent);
    }

    public void onRetourClicked(android.view.View view) {
        Log.d("DetailfilmActivity", "Retour à la liste des films");
        finish(); // Retour à l'activité précédente (ListefilmsActivity)
    }
}
