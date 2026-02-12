package com.example.applicationrftg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DetailfilmActivity extends AppCompatActivity {

    private String detailFilmResultat = "";
    private String filmId = "";
    private String filmTitle = "";
    private Film filmActuel = null;
    private ProgressBar progressBarDetail;
    private ScrollView scrollViewContent;
    private SessionManager sessionManager;
    private LinearLayout layoutCommentaires;
    private EditText etNouveauCommentaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailfilm);

        // Initialiser le SessionManager
        sessionManager = new SessionManager(this);

        // Initialiser les vues
        progressBarDetail = findViewById(R.id.progressBarDetail);
        scrollViewContent = findViewById(R.id.scrollViewContent);
        layoutCommentaires = findViewById(R.id.layoutCommentaires);
        etNouveauCommentaire = findViewById(R.id.etNouveauCommentaire);

        // Afficher le loader et cacher le contenu
        progressBarDetail.setVisibility(View.VISIBLE);
        scrollViewContent.setVisibility(View.GONE);

        // Récupérer l'ID du film passé depuis ListefilmsActivity
        Intent intent = getIntent();
        filmId = intent.getStringExtra("FILM_ID");
        filmTitle = intent.getStringExtra("FILM_TITLE");

        Log.d("DetailfilmActivity", "Page détail affichée pour film ID: " + filmId);

        // Configurer le bouton d'envoi de commentaire
        Button btnEnvoyerCommentaire = findViewById(R.id.btnEnvoyerCommentaire);
        btnEnvoyerCommentaire.setOnClickListener(v -> onEnvoyerCommentaireClicked());

        // Appeler le service REST pour récupérer les détails du film
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/films/" + filmId);
            new DetailfilmTask(this).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug",">>>Pour DetailfilmTask - MalformedURLException mue="+mue.toString());
            progressBarDetail.setVisibility(View.GONE);
        } finally {
            urlAAppeler = null;
        }

        // Charger les commentaires du film
        chargerCommentaires();
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

    // Affichage des détails du film
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

        // Construire "Année • Catégorie"
        String yearAndCategory = film.getRelease_year();
        if (film.getCategories() != null && !film.getCategories().isEmpty()) {
            yearAndCategory += " • " + film.getCategories().get(0).toString();
        }
        tvYearAndCategory.setText(yearAndCategory);

        // Afficher le premier acteur principal
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

    // Charger les commentaires du film
    private void chargerCommentaires() {
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/films/commentaire");
            new GetCommentsTask(this, filmId).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>Pour GetCommentsTask - MalformedURLException mue=" + mue.toString());
        } finally {
            urlAAppeler = null;
        }
    }

    // Mettre à jour l'affichage des commentaires après réception des données
    public void mettreAJourCommentairesApresAppelRest(String resultatAppelRest) {
        Log.d("mydebug", ">>>Commentaires reçus: " + resultatAppelRest);

        if (resultatAppelRest == null || resultatAppelRest.trim().isEmpty() || resultatAppelRest.equals("[]")) {
            // Aucun commentaire
            afficherMessageAucunCommentaire();
            return;
        }

        try {
            // Parser le JSON en liste de commentaires
            Gson gson = new Gson();
            Type commentListType = new TypeToken<ArrayList<FilmComment>>(){}.getType();
            ArrayList<FilmComment> commentaires = gson.fromJson(resultatAppelRest, commentListType);

            if (commentaires != null && !commentaires.isEmpty()) {
                afficherCommentaires(commentaires);
            } else {
                afficherMessageAucunCommentaire();
            }
        } catch (Exception e) {
            Log.e("mydebug", ">>>Erreur parsing commentaires: " + e.toString());
            afficherMessageAucunCommentaire();
        }
    }

    // Afficher la liste des commentaires
    private void afficherCommentaires(ArrayList<FilmComment> commentaires) {
        layoutCommentaires.removeAllViews();

        for (FilmComment comment : commentaires) {
            // Créer une vue pour chaque commentaire
            LinearLayout commentView = new LinearLayout(this);
            commentView.setOrientation(LinearLayout.VERTICAL);
            commentView.setBackgroundColor(0xFFFFFFFF);
            commentView.setPadding(32, 24, 32, 24);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 16);
            commentView.setLayoutParams(layoutParams);

            // Ligne avec nom et date
            LinearLayout headerLayout = new LinearLayout(this);
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Nom de l'auteur
            TextView tvAuthor = new TextView(this);
            tvAuthor.setText(comment.getCustomerName() != null ? comment.getCustomerName() : "Utilisateur");
            tvAuthor.setTextSize(14);
            tvAuthor.setTextColor(0xFF000000);
            tvAuthor.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams authorParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            tvAuthor.setLayoutParams(authorParams);

            // Date du commentaire
            TextView tvDate = new TextView(this);
            tvDate.setText(formatDate(comment.getCreatedDate()));
            tvDate.setTextSize(12);
            tvDate.setTextColor(0xFF999999);

            headerLayout.addView(tvAuthor);
            headerLayout.addView(tvDate);

            // Texte du commentaire
            TextView tvCommentText = new TextView(this);
            tvCommentText.setText(comment.getCommentText());
            tvCommentText.setTextSize(14);
            tvCommentText.setTextColor(0xFF333333);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textParams.setMargins(0, 12, 0, 0);
            tvCommentText.setLayoutParams(textParams);

            commentView.addView(headerLayout);
            commentView.addView(tvCommentText);

            layoutCommentaires.addView(commentView);
        }
    }

    // Afficher un message quand il n'y a pas de commentaires
    private void afficherMessageAucunCommentaire() {
        layoutCommentaires.removeAllViews();

        TextView tvMessage = new TextView(this);
        tvMessage.setText("Aucun commentaire pour le moment. Soyez le premier à commenter !");
        tvMessage.setTextSize(14);
        tvMessage.setTextColor(0xFF666666);
        tvMessage.setPadding(0, 16, 0, 16);

        layoutCommentaires.addView(tvMessage);
    }

    // Formater la date
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("mydebug", ">>>Erreur parsing date: " + e.toString());
            return dateString.substring(0, Math.min(10, dateString.length()));
        }
    }

    // Envoyer un nouveau commentaire
    private void onEnvoyerCommentaireClicked() {
        String commentText = etNouveauCommentaire.getText().toString().trim();

        if (commentText.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un commentaire", Toast.LENGTH_SHORT).show();
            return;
        }

        int customerId = sessionManager.getCustomerId();

        if (customerId == -1) {
            Toast.makeText(this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/films/commentaire/add");
            new AddCommentTask(this, filmId, String.valueOf(customerId), commentText).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>Pour AddCommentTask - MalformedURLException mue=" + mue.toString());
            Toast.makeText(this, "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
        } finally {
            urlAAppeler = null;
        }
    }

   
    public void commentaireAjouteAvecSucces(String resultat) {
        Log.d("mydebug", ">>>Commentaire ajouté: " + resultat);

        etNouveauCommentaire.setText("");

        Toast.makeText(this, "Commentaire ajouté avec succès", Toast.LENGTH_SHORT).show();

        chargerCommentaires();
    }

    // Méthode appelée quand on clique sur "Commander ce film"
    public void onCommanderClicked() {
        if (filmActuel == null) {
            Toast.makeText(this, "Erreur: Film non chargé", Toast.LENGTH_SHORT).show();
            return;
        }

        int customerId = sessionManager.getCustomerId();

        if (customerId == -1) {
            Toast.makeText(this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        // Appeler l'API pour ajouter le film au panier
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/cart/add");
            new AddToCartTask(this, filmId, String.valueOf(customerId)).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>Pour AddToCartTask - MalformedURLException mue=" + mue.toString());
            Toast.makeText(this, "Erreur lors de l'ajout au panier", Toast.LENGTH_SHORT).show();
        } finally {
            urlAAppeler = null;
        }
    }

    // Callback appelé après l'ajout au panier via l'API
    public void filmAjouteAuPanierAvecSucces(String resultat) {
        Log.d("mydebug", ">>>Film ajouté au panier: " + resultat);

        if (resultat != null && !resultat.equals("ERROR")) {
            Toast.makeText(this, "Film ajouté au panier", Toast.LENGTH_SHORT).show();
            Log.d("DetailfilmActivity", "Film ajouté au panier: " + filmActuel.getTitle());

            // Ouvrir l'écran du panier
            Intent intent = new Intent(this, PanierActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Erreur: Film non disponible ou déjà dans le panier", Toast.LENGTH_LONG).show();
        }
    }

    public void onRetourClicked(android.view.View view) {
        Log.d("DetailfilmActivity", "Retour à la liste des films");
        finish();
    }
}
