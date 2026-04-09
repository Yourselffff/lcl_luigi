package com.example.applicationrftglcl;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Activity principale affichant la liste de tous les films disponibles.
 * Implémente PanierChangeListener pour mettre à jour le badge du panier en temps réel.
 * Gère la recherche par titre, l'ajout au panier, et la navigation vers les détails.
 */
public class ListefilmsActivity extends AppCompatActivity implements Panier.PanierChangeListener {

    /** JSON brut de la liste des films, conservé pour la restauration après rotation. */
    private String listeFilmsResultat = "";

    /** Liste complète des films chargés depuis l'API (utilisée pour la recherche). */
    private ArrayList<Film> filmArrayComplet = new ArrayList<>();

    /** Adapter de la ListView avec filtre de recherche intégré. */
    private ArrayAdapter<Film> adapter;

    /** Indicateur de chargement affiché pendant l'appel API. */
    private ProgressBar progressBar;

    /** Conteneur du loader (texte + roue de chargement). */
    private View layoutChargement;

    /** Badge numérique affiché sur l'icône du panier. */
    private TextView tvPanierBadge;

    /** Gestionnaire de session pour récupérer le customerId et l'URL serveur. */
    private SessionManager sessionManager;

    /**
     * Initialise l'Activity : vérifie la session, configure la barre de recherche
     * et charge la liste des films (depuis le cache ou l'API).
     *
     * @param savedInstanceState état sauvegardé (contient le JSON des films après rotation).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listefilms);

        sessionManager = new SessionManager(this);

        // Redirection vers la connexion si aucune session active
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Liaison des vues avec les éléments du layout
        progressBar = findViewById(R.id.progressBar);
        layoutChargement = findViewById(R.id.layoutChargement);
        tvPanierBadge = findViewById(R.id.tvPanierBadge);

        // Enregistrement du listener pour les mises à jour du badge panier
        Panier.getInstance().setListener(this);

        // Configuration du filtre de recherche en temps réel
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Applique le filtre sur la liste à chaque modification du texte
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Restauration après rotation : réutilise le JSON déjà chargé
        if (savedInstanceState != null && savedInstanceState.containsKey("listeFilmsJson")) {
            listeFilmsResultat = savedInstanceState.getString("listeFilmsJson");
            layoutChargement.setVisibility(View.GONE);
            afficherListeFilms(listeFilmsResultat);
        } else {
            // Premier chargement : appel API
            layoutChargement.setVisibility(View.VISIBLE);
            URL urlAAppeler = null;
            try {
                urlAAppeler = new URL(sessionManager.getBaseUrl() + "/films");
                new ListefilmsTask(this).execute(urlAAppeler);
            } catch (MalformedURLException mue) {
                Log.d("mydebug", ">>>ListefilmsTask - MalformedURLException: " + mue.toString());
                layoutChargement.setVisibility(View.GONE);
            } finally {
                urlAAppeler = null;
            }
        }
    }

    /**
     * Sauvegarde le JSON des films avant rotation pour éviter un rechargement réseau.
     *
     * @param outState bundle dans lequel le JSON est stocké.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (listeFilmsResultat != null && !listeFilmsResultat.isEmpty()) {
            outState.putString("listeFilmsJson", listeFilmsResultat);
        }
    }

    /**
     * Callback appelé par ListefilmsTask après réception de la réponse API.
     * Masque le loader et déclenche l'affichage de la liste.
     *
     * @param resultatAppelRest JSON de la liste des films.
     */
    public void mettreAJourActivityApresAppelRest(String resultatAppelRest) {
        layoutChargement.setVisibility(View.GONE);
        listeFilmsResultat = resultatAppelRest;
        Log.d("mydebug", ">>>ListefilmsActivity - résultat reçu");

        if (resultatAppelRest != null && !resultatAppelRest.trim().isEmpty()) {
            afficherListeFilms(listeFilmsResultat);
        } else {
            Log.e("mydebug", ">>>Erreur : résultat vide ou null");
            Toast.makeText(this, "Impossible de charger les films. Vérifiez votre connexion.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Désérialise le JSON en ArrayList<Film>.
     * Retourne une liste vide si le parsing échoue.
     *
     * @param filmJson JSON brut retourné par l'API.
     * @return liste de films parsée, ou liste vide en cas d'erreur.
     */
    public ArrayList<Film> convertitListeFilmsEnArrayList(String filmJson) {
        Gson gson = new Gson();
        Type filmListType = new TypeToken<ArrayList<Film>>(){}.getType();
        ArrayList<Film> filmArray = gson.fromJson(filmJson, filmListType);

        if (filmArray == null) {
            Log.e("mydebug", ">>>Impossible de parser le JSON en ArrayList<Film>");
            return new ArrayList<>();
        }

        // Log de contrôle
        System.out.println(">>>>Les films >>>>>>>>>>>>>>>DEBUT");
        for (Film film : filmArray) {
            System.out.println("film_id=" + film.getFilm_id() + "/title=" + film.getTitle());
        }
        System.out.println(">>>>Les films >>>>>>>>>>>>>>>FIN");
        return filmArray;
    }

    /**
     * Crée et configure l'ArrayAdapter puis l'associe à la ListView.
     * Chaque ligne affiche le titre, le type, et des boutons Détail et Ajouter.
     * Le filtre de recherche s'appuie sur Film.toString() qui retourne le titre.
     *
     * @param filmJson JSON brut à afficher.
     */
    public void afficherListeFilms(String filmJson) {
        final ArrayList<Film> filmArray = convertitListeFilmsEnArrayList(filmJson);
        filmArrayComplet = filmArray;

        // Création de l'ArrayAdapter avec surcharge de getView pour le layout personnalisé
        adapter = new ArrayAdapter<Film>(this, R.layout.ligne_liste_films, filmArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Film film = getItem(position);

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.ligne_liste_films, parent, false);
                }

                // Liaison des vues de la ligne avec les données du film
                TextView textNomFilm = convertView.findViewById(R.id.textNomFilm);
                TextView textTypeFilm = convertView.findViewById(R.id.textTypeFilm);
                Button btnDetail = convertView.findViewById(R.id.btnDetail);
                Button btnAjouter = convertView.findViewById(R.id.btnAjouter);

                textNomFilm.setText(film.getTitle());
                textTypeFilm.setText("DVD");

                // Vérification de la disponibilité pour activer/désactiver le bouton Ajouter
                verifierDisponibilite(film.getFilm_id(), btnAjouter);

                // Navigation vers le détail du film
                btnDetail.setOnClickListener(v -> {
                    Log.d("mydebug", "Clic sur détail du film: " + film.getTitle());
                    Intent intent = new Intent(ListefilmsActivity.this, DetailfilmActivity.class);
                    intent.putExtra("FILM_ID", film.getFilm_id());
                    intent.putExtra("FILM_TITLE", film.getTitle());
                    startActivity(intent);
                });

                // Ajout du film au panier via l'API
                btnAjouter.setOnClickListener(v -> {
                    Log.d("mydebug", "Ajout au panier: " + film.getTitle());
                    int customerId = sessionManager.getCustomerId();
                    if (customerId == -1) {
                        Toast.makeText(ListefilmsActivity.this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    URL urlAAppeler = null;
                    try {
                        urlAAppeler = new URL(sessionManager.getBaseUrl() + "/cart/add");
                        new AddToCartTask(ListefilmsActivity.this, film.getFilm_id(), String.valueOf(customerId)).execute(urlAAppeler);
                    } catch (MalformedURLException mue) {
                        Log.d("mydebug", ">>>AddToCartTask - MalformedURLException: " + mue.toString());
                        Toast.makeText(ListefilmsActivity.this, "Erreur lors de l'ajout au panier", Toast.LENGTH_SHORT).show();
                    } finally {
                        urlAAppeler = null;
                    }
                });

                return convertView;
            }
        };

        // Association de l'adapter à la ListView
        ListView listviewFilms = (ListView) findViewById(R.id.listeFilms);
        listviewFilms.setAdapter(adapter);

        // Clic sur un item de la liste → navigation vers le détail
        listviewFilms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Film filmClique = adapter.getItem(position);
                if (filmClique != null) {
                    Log.d("mydebug", "Clic sur film: " + filmClique.getTitle());
                    Intent intent = new Intent(ListefilmsActivity.this, DetailfilmActivity.class);
                    intent.putExtra("FILM_ID", filmClique.getFilm_id());
                    intent.putExtra("FILM_TITLE", filmClique.getTitle());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Met à jour le badge numérique du panier lors du retour sur cet écran.
     * Déclenché à chaque reprise de l'Activity (ex. : retour depuis DetailfilmActivity).
     */
    @Override
    protected void onResume() {
        super.onResume();
        mettreAJourBadgePanier();
    }

    /**
     * Affiche ou masque le badge du panier selon le nombre d'articles.
     * Badge visible uniquement si le panier contient au moins un article.
     */
    private void mettreAJourBadgePanier() {
        int nombreArticles = Panier.getInstance().getNombreItems();
        if (nombreArticles > 0) {
            tvPanierBadge.setText(String.valueOf(nombreArticles));
            tvPanierBadge.setVisibility(View.VISIBLE);
        } else {
            tvPanierBadge.setVisibility(View.GONE);
        }
    }

    /**
     * Implémentation de PanierChangeListener — met à jour le badge en temps réel
     * dès qu'un article est ajouté ou supprimé du panier.
     */
    @Override
    public void onPanierChanged() {
        mettreAJourBadgePanier();
    }

    /**
     * Callback du bouton Panier (défini via android:onClick dans le layout).
     * Ouvre PanierActivity.
     *
     * @param view vue ayant déclenché l'événement.
     */
    public void onPanierClicked(View view) {
        Log.d("ListefilmsActivity", "Ouverture du panier");
        startActivity(new Intent(this, PanierActivity.class));
    }

    /**
     * Callback du bouton Quitter (défini via android:onClick dans le layout).
     * Ferme l'application et la retire de la pile des tâches récentes.
     *
     * @param view vue ayant déclenché l'événement.
     */
    public void onQuitterApp(View view) {
        finishAndRemoveTask();
    }

    /**
     * Callback du bouton Déconnexion (défini via android:onClick dans le layout).
     * Efface la session, vide le panier local et redirige vers LoginActivity.
     *
     * @param view vue ayant déclenché l'événement.
     */
    public void onDeconnexionClicked(View view) {
        Log.d("ListefilmsActivity", "Déconnexion de l'utilisateur");
        sessionManager.logout();
        Panier.getInstance().viderPanier();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Lance CheckAvailabilityTask pour vérifier si un film est disponible en stock.
     * Active ou désactive le bouton Ajouter selon le résultat.
     *
     * @param filmId     identifiant du film à vérifier.
     * @param btnAjouter bouton à mettre à jour selon la disponibilité.
     */
    private void verifierDisponibilite(String filmId, Button btnAjouter) {
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/inventories/available/film/" + filmId);
            new CheckAvailabilityTask(btnAjouter, filmId).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>CheckAvailabilityTask - MalformedURLException: " + mue.toString());
            // Sécurité : désactive le bouton si l'URL est invalide
            btnAjouter.setEnabled(false);
            btnAjouter.setText("Erreur");
        } finally {
            urlAAppeler = null;
        }
    }

    /**
     * Callback appelé par AddToCartTask après ajout d'un film au panier depuis la liste.
     * En cas de succès, recharge le panier depuis l'API pour synchroniser le badge.
     *
     * @param resultat réponse de l'API ou "ERROR".
     */
    public void filmAjouteAuPanierAvecSucces(String resultat) {
        Log.d("mydebug", ">>>Film ajouté au panier depuis la liste: " + resultat);
        if (resultat != null && !resultat.equals("ERROR")) {
            Toast.makeText(this, "Film ajouté au panier", Toast.LENGTH_SHORT).show();
            int customerId = sessionManager.getCustomerId();
            if (customerId != -1) {
                chargerPanierDepuisAPI(customerId);
            }
        } else {
            Toast.makeText(this, "Erreur: Film non disponible ou déjà dans le panier", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Lance GetCartTask pour synchroniser le panier local avec l'état du serveur.
     * Utilisé après un ajout au panier pour mettre à jour le badge.
     *
     * @param customerId identifiant du client connecté.
     */
    private void chargerPanierDepuisAPI(int customerId) {
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/cart/" + customerId);
            new GetCartTask(this, String.valueOf(customerId)).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>GetCartTask - MalformedURLException: " + mue.toString());
        } finally {
            urlAAppeler = null;
        }
    }

    /**
     * Callback appelé par GetCartTask après récupération du panier depuis l'API.
     * Synchronise le panier local (Singleton) avec les données serveur
     * et met à jour le badge.
     *
     * @param resultatAppelRest JSON de la liste des CartItem.
     */
    public void mettreAJourPanierApresAppelRest(String resultatAppelRest) {
        Log.d("mydebug", ">>>Panier reçu dans ListefilmsActivity: " + resultatAppelRest);

        if (resultatAppelRest == null || resultatAppelRest.trim().isEmpty() || resultatAppelRest.equals("[]")) {
            Panier.getInstance().viderPanier();
            mettreAJourBadgePanier();
            return;
        }

        try {
            Gson gson = new Gson();
            Type cartListType = new TypeToken<ArrayList<CartItem>>(){}.getType();
            ArrayList<CartItem> cartItems = gson.fromJson(resultatAppelRest, cartListType);

            if (cartItems != null && !cartItems.isEmpty()) {
                Panier.getInstance().viderPanier();

                for (CartItem item : cartItems) {
                    Film film = item.getFilm();
                    if (film == null) {
                        // Crée un Film minimal si l'objet imbriqué est absent
                        film = new Film();
                        film.setFilm_id(item.getFilmId());
                        film.setTitle(item.getFilmTitle());
                    }
                    Panier.getInstance().ajouterFilm(film);

                    // Associe le rentalId à l'ItemPanier pour permettre la suppression via l'API
                    ItemPanier itemPanier = Panier.getInstance().trouverItem(film.getFilm_id());
                    if (itemPanier != null) {
                        itemPanier.setRentalId(item.getRentalId());
                    }
                }
            } else {
                Panier.getInstance().viderPanier();
            }
            mettreAJourBadgePanier();
        } catch (Exception e) {
            Log.e("mydebug", ">>>Erreur parsing panier dans ListefilmsActivity: " + e.toString());
            mettreAJourBadgePanier();
        }
    }
}
