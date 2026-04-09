package com.example.applicationrftglcl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter personnalisé pour afficher les articles du panier dans une ListView.
 * Implémente le patron ViewHolder pour recycler les vues et optimiser les performances
 * (évite les appels répétés à findViewById lors du défilement).
 */
public class PanierAdapter extends BaseAdapter {

    private Context context;

    /** Liste des articles à afficher, partagée avec le Singleton Panier. */
    private ArrayList<ItemPanier> items;

    /** Listener notifié lors d'une modification du panier (suppression d'article). */
    private PanierChangeListener listener;

    /**
     * Interface de callback permettant à PanierActivity d'être notifiée
     * des changements déclenchés depuis l'Adapter (ex. : suppression d'un article).
     */
    public interface PanierChangeListener {
        /** Appelé dès qu'un article est modifié ou supprimé depuis l'Adapter. */
        void onPanierChanged();
    }

    /**
     * Constructeur de l'Adapter.
     *
     * @param context  contexte Android (Activity parente).
     * @param items    liste des articles du panier à afficher.
     * @param listener callback pour notifier les changements.
     */
    public PanierAdapter(Context context, ArrayList<ItemPanier> items, PanierChangeListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    /** @return le nombre d'articles dans la liste. */
    @Override
    public int getCount() {
        return items.size();
    }

    /** @return l'article à la position donnée. */
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    /**
     * Retourne l'identifiant de l'item à la position donnée.
     * Utilise la position comme identifiant (pas d'ID métier requis ici).
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Crée ou recycle une vue de ligne pour l'article à la position donnée.
     * Applique le patron ViewHolder : les références aux sous-vues sont mises en cache
     * via setTag/getTag pour éviter les appels répétés à findViewById.
     *
     * @param position    position de l'article dans la liste.
     * @param convertView vue recyclée (null si nouvelle création nécessaire).
     * @param parent      ViewGroup parent de la ListView.
     * @return vue configurée avec les données de l'article.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Première création : inflate le layout et met en cache les références
            convertView = LayoutInflater.from(context).inflate(R.layout.item_panier, parent, false);
            holder = new ViewHolder();
            holder.tvTitre = convertView.findViewById(R.id.tvItemTitre);
            holder.tvType = convertView.findViewById(R.id.tvItemType);
            holder.btnSupprimer = convertView.findViewById(R.id.btnSupprimer);
            convertView.setTag(holder); // Cache le holder pour les recyclages futurs
        } else {
            // Recyclage : récupère le holder mis en cache
            holder = (ViewHolder) convertView.getTag();
        }

        ItemPanier item = items.get(position);
        Film film = item.getFilm();

        // Remplissage des vues avec les données de l'article
        holder.tvTitre.setText(film.getTitle());
        holder.tvType.setText("DVD");

        // Listener du bouton Supprimer : tente d'abord la suppression via l'API
        holder.btnSupprimer.setOnClickListener(v -> {
            int rentalId = item.getRentalId();

            if (rentalId > 0 && listener instanceof PanierActivity) {
                // Suppression via l'API REST (rentalId connu)
                PanierActivity activity = (PanierActivity) listener;
                try {
                    SessionManager sessionManager = new SessionManager(activity);
                    java.net.URL urlAAppeler = new java.net.URL(sessionManager.getBaseUrl() + "/cart/" + rentalId);
                    new RemoveFromCartTask(activity, String.valueOf(rentalId)).execute(urlAAppeler);
                } catch (java.net.MalformedURLException mue) {
                    android.util.Log.d("mydebug", ">>>RemoveFromCartTask - MalformedURLException: " + mue.toString());
                    // Fallback : suppression locale si l'URL est invalide
                    Panier.getInstance().supprimerFilm(film.getFilm_id());
                    listener.onPanierChanged();
                }
            } else {
                // Pas de rentalId disponible : suppression locale uniquement
                Panier.getInstance().supprimerFilm(film.getFilm_id());
                if (listener != null) {
                    listener.onPanierChanged();
                }
            }
        });

        return convertView;
    }

    /**
     * Classe interne ViewHolder — met en cache les références aux sous-vues d'une ligne.
     * Évite les appels répétés à findViewById lors du défilement de la ListView.
     */
    static class ViewHolder {
        /** TextView affichant le titre du film. */
        TextView tvTitre;
        /** TextView affichant le type de support (ex. : "DVD"). */
        TextView tvType;
        /** Bouton de suppression de l'article du panier. */
        Button btnSupprimer;
    }
}
