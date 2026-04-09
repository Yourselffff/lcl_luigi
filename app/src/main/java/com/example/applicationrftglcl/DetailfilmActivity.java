package com.example.applicationrftglcl;

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

/**
 * Activity affichant le détail d'un film sélectionné depuis ListefilmsActivity.
 * Charge les informations du film (titre, description, acteurs, réalisateurs, catégories)
 * via DetailfilmTask, affiche les commentaires via GetCommentsTask,
 * et permet d'ajouter le film au panier ou de poster un commentaire.
 */
public class DetailfilmActivity extends AppCompatActivity {

    /** JSON brut du détail du film retourné par l'API. */
    private String detailFilmResultat = "";

    /** Identifiant du film transmis par ListefilmsActivity via l'Intent. */
    private String filmId = "";

    /** Titre du film transmis par ListefilmsActivity (pour les logs). */
    private String filmTitle = "";

    /** Objet Film chargé depuis l'API, utilisé lors de l'ajout au panier. */
    private Film filmActuel = null;

    /** Indicateur de chargement affiché pendant l'appel API initial. */
    private ProgressBar progressBarDetail;

    /** ScrollView contenant le contenu — masqué pendant le chargement. */
    private ScrollView scrollViewContent;

    /** Gestionnaire de session pour récupérer le customerId et l'URL serveur. */
    private SessionManager sessionManager;

    /** Conteneur dynamique dans lequel les commentaires sont ajoutés programmatiquement. */
    private LinearLayout layoutCommentaires;

    /** Champ de saisie du nouveau commentaire. */
    private EditText etNouveauCommentaire;

    /**
     * Initialise l'Activity, récupère l'ID du film depuis l'Intent,
     * déclenche le chargement du détail et des commentaires.
     *
     * @param savedInstanceState état sauvegardé de l'instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailfilm);

        sessionManager = new SessionManager(this);

        // Liaison des vues avec les éléments du layout
        progressBarDetail = findViewById(R.id.progressBarDetail);
        scrollViewContent = findViewById(R.id.scrollViewContent);
        layoutCommentaires = findViewById(R.id.layoutCommentaires);
        etNouveauCommentaire = findViewById(R.id.etNouveauCommentaire);

        // Affiche le loader et masque le contenu pendant le chargement
        progressBarDetail.setVisibility(View.VISIBLE);
        scrollViewContent.setVisibility(View.GONE);

        // Récupération de l'ID et du titre du film transmis par ListefilmsActivity
        Intent intent = getIntent();
        filmId = intent.getStringExtra("FILM_ID");
        filmTitle = intent.getStringExtra("FILM_TITLE");
        Log.d("DetailfilmActivity", "Film ID: " + filmId);

        // Listener du bouton d'envoi de commentaire
        Button btnEnvoyerCommentaire = findViewById(R.id.btnEnvoyerCommentaire);
        btnEnvoyerCommentaire.setOnClickListener(v -> onEnvoyerCommentaireClicked());

        // Chargement du détail du film via l'API
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/films/" + filmId);
            new DetailfilmTask(this).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>DetailfilmTask - MalformedURLException: " + mue.toString());
            progressBarDetail.setVisibility(View.GONE);
        } finally {
            urlAAppeler = null;
        }

        // Chargement des commentaires du film (appel indépendant)
        chargerCommentaires();
    }

    /**
     * Callback appelé par DetailfilmTask après réception de la réponse API.
     * Masque le loader, affiche le contenu et déclenche l'affichage du film.
     *
     * @param resultatAppelRest JSON du film retourné par l'API.
     */
    public void mettreAJourActivityApresAppelRest(String resultatAppelRest) {
        progressBarDetail.setVisibility(View.GONE);
        scrollViewContent.setVisibility(View.VISIBLE);

        detailFilmResultat = resultatAppelRest;
        Log.d("mydebug", ">>>DetailfilmActivity - résultat reçu");

        if (resultatAppelRest != null && !resultatAppelRest.trim().isEmpty()) {
            afficherDetailFilm(resultatAppelRest);
        } else {
            Log.e("mydebug", ">>>Erreur : résultat vide ou null");
        }
    }

    /**
     * Désérialise le JSON et remplit les TextViews avec les informations du film.
     * Construit les listes de catégories, réalisateurs et acteurs par concaténation.
     * Configure également le bouton "Commander ce film".
     *
     * @param filmJson JSON brut du film retourné par l'API.
     */
    public void afficherDetailFilm(String filmJson) {
        Gson gson = new Gson();
        Film film = gson.fromJson(filmJson, Film.class);

        if (film == null) {
            Log.e("mydebug", ">>>Impossible de parser le JSON en Film");
            return;
        }

        // Sauvegarde du film pour l'ajout au panier
        this.filmActuel = film;
        System.out.println(">>>>Détail du film : " + film.getTitle());

        // Liaison des TextViews du layout
        TextView tvTitle = findViewById(R.id.tvFilmTitle);
        TextView tvDescription = findViewById(R.id.tvFilmDescription);
        TextView tvYearAndCategory = findViewById(R.id.tvFilmYearAndCategory);
        TextView tvActor = findViewById(R.id.tvFilmActor);
        TextView tvDirectors = findViewById(R.id.tvFilmDirectors);
        TextView tvActors = findViewById(R.id.tvFilmActors);
        TextView tvCategories = findViewById(R.id.tvFilmCategories);

        tvTitle.setText(film.getTitle());
        tvDescription.setText(film.getDescription());

        // Format "Année • Catégorie principale"
        String yearAndCategory = film.getRelease_year();
        if (film.getCategories() != null && !film.getCategories().isEmpty()) {
            yearAndCategory += " • " + film.getCategories().get(0).toString();
        }
        tvYearAndCategory.setText(yearAndCategory);

        // Affiche le premier acteur principal en vedette
        if (film.getActors() != null && !film.getActors().isEmpty()) {
            tvActor.setText(film.getActors().get(0).toString());
        } else {
            tvActor.setText("");
        }

        // Construction de la liste des catégories séparées par des virgules
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

        // Construction de la liste des réalisateurs séparés par des virgules
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

        // Construction de la liste de tous les acteurs séparés par des virgules
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

        // Listener du bouton Commander
        Button btnCommander = findViewById(R.id.btnCommander);
        btnCommander.setOnClickListener(v -> onCommanderClicked());
    }

    /**
     * Lance GetCommentsTask pour charger les commentaires du film depuis l'API.
     * Utilise POST /films/commentaire avec le filmId dans le corps JSON.
     */
    private void chargerCommentaires() {
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/films/commentaire");
            new GetCommentsTask(this, filmId).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>GetCommentsTask - MalformedURLException: " + mue.toString());
        } finally {
            urlAAppeler = null;
        }
    }

    /**
     * Callback appelé par GetCommentsTask après réception des commentaires.
     * Parse la liste JSON et délègue l'affichage ou affiche un message d'absence.
     *
     * @param resultatAppelRest JSON de la liste des commentaires.
     */
    public void mettreAJourCommentairesApresAppelRest(String resultatAppelRest) {
        Log.d("mydebug", ">>>Commentaires reçus: " + resultatAppelRest);

        if (resultatAppelRest == null || resultatAppelRest.trim().isEmpty() || resultatAppelRest.equals("[]")) {
            afficherMessageAucunCommentaire();
            return;
        }

        try {
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

    /**
     * Construit dynamiquement les vues des commentaires et les ajoute au layoutCommentaires.
     * Chaque commentaire affiche l'auteur, la date et le texte.
     *
     * @param commentaires liste des commentaires à afficher.
     */
    private void afficherCommentaires(ArrayList<FilmComment> commentaires) {
        layoutCommentaires.removeAllViews();

        for (FilmComment comment : commentaires) {
            // Conteneur vertical pour un commentaire
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

            // En-tête : nom de l'auteur (gauche) + date (droite)
            LinearLayout headerLayout = new LinearLayout(this);
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Nom de l'auteur en gras
            TextView tvAuthor = new TextView(this);
            tvAuthor.setText(comment.getCustomerName() != null ? comment.getCustomerName() : "Utilisateur");
            tvAuthor.setTextSize(14);
            tvAuthor.setTextColor(0xFF000000);
            tvAuthor.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams authorParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
            );
            tvAuthor.setLayoutParams(authorParams);

            // Date formatée en dd/MM/yyyy
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

    /**
     * Affiche un message d'invitation quand il n'y a aucun commentaire pour ce film.
     */
    private void afficherMessageAucunCommentaire() {
        layoutCommentaires.removeAllViews();
        TextView tvMessage = new TextView(this);
        tvMessage.setText("Aucun commentaire pour le moment. Soyez le premier à commenter !");
        tvMessage.setTextSize(14);
        tvMessage.setTextColor(0xFF666666);
        tvMessage.setPadding(0, 16, 0, 16);
        layoutCommentaires.addView(tvMessage);
    }

    /**
     * Convertit une date ISO 8601 (yyyy-MM-dd'T'HH:mm:ss) en format lisible (dd/MM/yyyy).
     * Retourne les 10 premiers caractères en cas d'échec du parsing.
     *
     * @param dateString date au format ISO 8601.
     * @return date formatée en dd/MM/yyyy, ou chaîne vide si null/vide.
     */
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
            // Fallback : retourne les 10 premiers caractères (partie date)
            return dateString.substring(0, Math.min(10, dateString.length()));
        }
    }

    /**
     * Valide le champ de saisie et lance AddCommentTask pour envoyer le commentaire.
     * Vérifie que le texte n'est pas vide et que l'utilisateur est connecté.
     */
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
            Log.d("mydebug", ">>>AddCommentTask - MalformedURLException: " + mue.toString());
            Toast.makeText(this, "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
        } finally {
            urlAAppeler = null;
        }
    }

    /**
     * Callback appelé par AddCommentTask après ajout du commentaire.
     * Vide le champ de saisie, affiche un toast et recharge les commentaires.
     *
     * @param resultat réponse de l'API.
     */
    public void commentaireAjouteAvecSucces(String resultat) {
        Log.d("mydebug", ">>>Commentaire ajouté: " + resultat);
        etNouveauCommentaire.setText("");
        Toast.makeText(this, "Commentaire ajouté avec succès", Toast.LENGTH_SHORT).show();
        // Rechargement pour afficher le nouveau commentaire immédiatement
        chargerCommentaires();
    }

    /**
     * Déclenché par le bouton "Commander ce film".
     * Vérifie que le film est chargé et l'utilisateur connecté avant l'appel API.
     */
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

        // Ajout du film au panier via l'API
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/cart/add");
            new AddToCartTask(this, filmId, String.valueOf(customerId)).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>AddToCartTask - MalformedURLException: " + mue.toString());
            Toast.makeText(this, "Erreur lors de l'ajout au panier", Toast.LENGTH_SHORT).show();
        } finally {
            urlAAppeler = null;
        }
    }

    /**
     * Callback appelé par AddToCartTask après ajout au panier depuis le détail.
     * En cas de succès, ouvre directement PanierActivity.
     *
     * @param resultat réponse de l'API ou "ERROR".
     */
    public void filmAjouteAuPanierAvecSucces(String resultat) {
        Log.d("mydebug", ">>>Film ajouté au panier: " + resultat);

        if (resultat != null && !resultat.equals("ERROR")) {
            Toast.makeText(this, "Film ajouté au panier", Toast.LENGTH_SHORT).show();
            Log.d("DetailfilmActivity", "Film ajouté: " + filmActuel.getTitle());
            // Redirection automatique vers le panier après ajout
            startActivity(new Intent(this, PanierActivity.class));
        } else {
            Toast.makeText(this, "Erreur: Film non disponible ou déjà dans le panier", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Callback du bouton Retour (défini via android:onClick dans le layout).
     * Ferme l'Activity et retourne à ListefilmsActivity.
     *
     * @param view vue ayant déclenché l'événement.
     */
    public void onRetourClicked(android.view.View view) {
        Log.d("DetailfilmActivity", "Retour à la liste des films");
        finish();
    }
}
