package com.example.applicationrftg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Adapter pour afficher les items du panier dans une ListView
 * Principe du cours : BaseAdapter avec ViewHolder pour optimiser les performances
 */
public class PanierAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemPanier> items;
    private PanierChangeListener listener;

    // Interface pour notifier les changements
    public interface PanierChangeListener {
        void onPanierChanged();
    }

    public PanierAdapter(Context context, ArrayList<ItemPanier> items, PanierChangeListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Pattern ViewHolder pour optimiser les performances (principe du cours)
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_panier, parent, false);
            holder = new ViewHolder();
            holder.tvTitre = convertView.findViewById(R.id.tvItemTitre);
            holder.tvType = convertView.findViewById(R.id.tvItemType);
            holder.btnSupprimer = convertView.findViewById(R.id.btnSupprimer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Récupérer l'item à la position donnée
        ItemPanier item = items.get(position);
        Film film = item.getFilm();

        // Remplir les vues avec les données
        holder.tvTitre.setText(film.getTitle());
        holder.tvType.setText("DVD"); // Ou autre type selon vos données

        // Bouton supprimer
        holder.btnSupprimer.setOnClickListener(v -> {
            // Récupérer le rentalId avant de supprimer
            int rentalId = item.getRentalId();

            if (rentalId > 0 && listener != null && listener instanceof PanierActivity) {
                // Appeler l'API pour supprimer du panier
                PanierActivity activity = (PanierActivity) listener;
                try {
                    SessionManager sessionManager = new SessionManager(activity);
                    java.net.URL urlAAppeler = new java.net.URL(sessionManager.getBaseUrl() + "/cart/" + rentalId);
                    new RemoveFromCartTask(activity, String.valueOf(rentalId)).execute(urlAAppeler);
                } catch (java.net.MalformedURLException mue) {
                    android.util.Log.d("mydebug", ">>>Pour RemoveFromCartTask - MalformedURLException mue=" + mue.toString());
                    // En cas d'erreur, supprimer quand même localement
                    Panier.getInstance().supprimerFilm(film.getFilm_id());
                    listener.onPanierChanged();
                }
            } else {
                // Pas de rentalId, supprimer seulement localement
                Panier.getInstance().supprimerFilm(film.getFilm_id());
                if (listener != null) {
                    listener.onPanierChanged();
                }
            }
        });

        return convertView;
    }

    // ViewHolder pour optimiser les performances (principe du cours)
    static class ViewHolder {
        TextView tvTitre;
        TextView tvType;
        Button btnSupprimer;
    }
}
