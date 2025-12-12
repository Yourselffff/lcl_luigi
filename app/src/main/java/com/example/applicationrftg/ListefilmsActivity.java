package com.example.applicationrftg;

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
 
public class ListefilmsActivity extends AppCompatActivity implements Panier.PanierChangeListener {

    private String listeFilmsResultat = "";
    private ArrayList<Film> filmArrayComplet = new ArrayList<>(); // Liste complète des films
    private ArrayAdapter<Film> adapter; // Adapter pour la recherche
    private ProgressBar progressBar; // Indicateur de chargement
    private TextView tvPanierBadge; // Badge du panier
    private SessionManager sessionManager; // Gestionnaire de session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listefilms);

        // Initialiser le SessionManager
        sessionManager = new SessionManager(this);

        // Vérifier si l'utilisateur est connecté
        if (!sessionManager.isLoggedIn()) {
            // Rediriger vers la page de connexion
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialiser le ProgressBar et le badge du panier
        progressBar = findViewById(R.id.progressBar);
        tvPanierBadge = findViewById(R.id.tvPanierBadge);

        // Enregistrer le listener pour les changements du panier
        Panier.getInstance().setListener(this);

        // Configurer la barre de recherche
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrer la liste
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Afficher le loader avant de charger les films
        progressBar.setVisibility(View.VISIBLE);

        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL("http://10.0.2.2:8180/films");
            new ListefilmsTask(this).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug",">>>Pour ListefilmsTask - MalformedURLException mue="+mue.toString());
            // Masquer le loader en cas d'erreur
            progressBar.setVisibility(View.GONE);
        } finally {
            urlAAppeler = null;
        }
    }

    public void mettreAJourActivityApresAppelRest(String resultatAppelRest) {
        // Masquer le loader une fois les données reçues
        progressBar.setVisibility(View.GONE);

        listeFilmsResultat = resultatAppelRest;
        Log.d("mydebug",">>>Pour ListefilmsActivity - mettreAJourActivityApresAppelRest="+listeFilmsResultat);

        // Vérifier que le résultat n'est pas vide avant d'afficher
        if (resultatAppelRest != null && !resultatAppelRest.trim().isEmpty()) {
            afficherListeFilms(listeFilmsResultat);
        } else {
            Log.e("mydebug", ">>>Erreur : Le résultat de l'appel REST est vide ou null");
            // TODO: Afficher un message d'erreur à l'utilisateur
        }
    }

    // Conversion JSON en ArrayList (modèle du cours)
    public ArrayList<Film> convertitListeFilmsEnArrayList(String filmJson) {
        Gson gson = new Gson();

        Type filmListType = new TypeToken<ArrayList<Film>>(){}.getType();
        ArrayList<Film> filmArray = gson.fromJson(filmJson, filmListType);

        // Vérifier que le JSON a été correctement parsé
        if (filmArray == null) {
            Log.e("mydebug", ">>>Erreur : Impossible de parser le JSON en ArrayList<Film>");
            return new ArrayList<>(); // Retourner une liste vide au lieu de null
        }

        // Contrôle
        System.out.println(">>>>Les films >>>>>>>>>>>>>>>DEBUT");
        for(Film film : filmArray) {
            System.out.println("film_id="+film.getFilm_id()+"/title="+film.getTitle());
        }
        System.out.println(">>>>Les films >>>>>>>>>>>>>>>FIN");
        return filmArray;
    }

    // Affichage dans la ListView (modèle du cours)
    public void afficherListeFilms(String filmJson) {
        final ArrayList<Film> filmArray = convertitListeFilmsEnArrayList(filmJson);
        filmArrayComplet = filmArray; // Stocker pour la recherche

        // Création de l'ArrayAdapter avec classe anonyme
        adapter = new ArrayAdapter<Film>(this, R.layout.ligne_liste_films, filmArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Film film = getItem(position);

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.ligne_liste_films, parent, false);
                }

                // Récupération des vues du modèle ligne_liste_films.xml
                TextView textNomFilm = convertView.findViewById(R.id.textNomFilm);
                TextView textTypeFilm = convertView.findViewById(R.id.textTypeFilm);
                Button btnDetail = convertView.findViewById(R.id.btnDetail);
                Button btnAjouter = convertView.findViewById(R.id.btnAjouter);

                // Remplissage avec les données du film
                textNomFilm.setText(film.getTitle());
                textTypeFilm.setText("DVD"); // Ou utiliser un champ du film si disponible

                // Vérifier la disponibilité du film
                verifierDisponibilite(film.getFilm_id(), btnAjouter);

                // Bouton Détail
                btnDetail.setOnClickListener(v -> {
                    Log.d("mydebug","clic sur détail du film: " + film.getTitle());
                    Intent intent = new Intent(ListefilmsActivity.this, DetailfilmActivity.class);
                    intent.putExtra("FILM_ID", film.getFilm_id());
                    intent.putExtra("FILM_TITLE", film.getTitle());
                    startActivity(intent);
                });

                // Bouton Ajouter au panier
                btnAjouter.setOnClickListener(v -> {
                    Log.d("mydebug","Ajout au panier: " + film.getTitle());
                    // Ajouter au panier (principe du cours : Singleton)
                    Panier.getInstance().ajouterFilm(film);
                    Toast.makeText(ListefilmsActivity.this, "Film ajouté au panier", Toast.LENGTH_SHORT).show();
                });

                return convertView;
            }
        };

        // Remplissage de la ListView
        ListView listviewFilms = (ListView) findViewById(R.id.listeFilms);
        listviewFilms.setAdapter(adapter);

        // Listener pour clic sur un item de la liste (principe du cours)
        listviewFilms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Film filmClique = adapter.getItem(position);
                if (filmClique != null) {
                    Log.d("mydebug","clic sur film: " + filmClique.getTitle());
                    // Redirection vers la page détails avec l'ID du film
                    Intent intent = new Intent(ListefilmsActivity.this, DetailfilmActivity.class);
                    intent.putExtra("FILM_ID", filmClique.getFilm_id());
                    intent.putExtra("FILM_TITLE", filmClique.getTitle());
                    startActivity(intent);
                }
            }
        });
    }

    // Méthode pour afficher le popup avec le nom du film
    /*
    private void afficherPopup(View view, Film film) {
        // Inflate le layout du popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Créer le PopupWindow
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // Pour fermer en cliquant en dehors
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Définir un fond semi-transparent
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Récupérer le TextView et afficher le message avec le nom du film
        TextView messagePopup = popupView.findViewById(R.id.messagePopup);
        messagePopup.setText("J'ai choisi : " + film.getTitle());

        // Gérer le bouton de fermeture
        Button closeBtn = popupView.findViewById(R.id.closePopupBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // Afficher le popup au centre de l'écran
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
    */

    @Override
    protected void onResume() {
        super.onResume();
        // Mettre à jour le badge du panier quand on revient sur la page
        mettreAJourBadgePanier();
    }

    // Méthode pour mettre à jour le badge du panier
    private void mettreAJourBadgePanier() {
        int nombreArticles = Panier.getInstance().getNombreItems();

        if (nombreArticles > 0) {
            tvPanierBadge.setText(String.valueOf(nombreArticles));
            tvPanierBadge.setVisibility(View.VISIBLE);
        } else {
            tvPanierBadge.setVisibility(View.GONE);
        }
    }

    // Implémentation de PanierChangeListener
    @Override
    public void onPanierChanged() {
        // Mettre à jour le badge en temps réel
        mettreAJourBadgePanier();
    }

    public void onPanierClicked(View view) {
        Log.d("ListefilmsActivity", "Ouverture du panier");
        startActivity(new Intent(this, PanierActivity.class));
    }

    public void onDeconnexionClicked(View view) {
        Log.d("ListefilmsActivity", "Déconnexion de l'utilisateur");

        // Déconnecter l'utilisateur
        sessionManager.logout();

        // Vider le panier
        Panier.getInstance().viderPanier();

        // Rediriger vers la page de connexion
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Vérifier la disponibilité d'un film
    private void verifierDisponibilite(String filmId, Button btnAjouter) {
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL("http://10.0.2.2:8180/inventories/available/film/" + filmId);
            new CheckAvailabilityTask(btnAjouter, filmId).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>Pour CheckAvailabilityTask - MalformedURLException mue=" + mue.toString());
            // En cas d'erreur, désactiver le bouton par sécurité
            btnAjouter.setEnabled(false);
            btnAjouter.setText("Erreur");
        } finally {
            urlAAppeler = null;
        }
    }
}
