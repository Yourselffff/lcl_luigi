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
            holder.tvQuantite = convertView.findViewById(R.id.tvQuantite);
            holder.tvPrix = convertView.findViewById(R.id.tvItemPrix);
            holder.btnDiminuer = convertView.findViewById(R.id.btnDiminuer);
            holder.btnAugmenter = convertView.findViewById(R.id.btnAugmenter);
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
        holder.tvQuantite.setText(String.valueOf(item.getQuantite()));
        holder.tvPrix.setText(String.format(Locale.FRANCE, "%.2f €", item.getPrixTotal()));

        // Bouton diminuer quantité
        holder.btnDiminuer.setOnClickListener(v -> {
            int nouvelleQuantite = item.getQuantite() - 1;
            if (nouvelleQuantite > 0) {
                Panier.getInstance().modifierQuantite(film.getFilm_id(), nouvelleQuantite);
            } else {
                Panier.getInstance().supprimerFilm(film.getFilm_id());
            }
            if (listener != null) {
                listener.onPanierChanged();
            }
        });

        // Bouton augmenter quantité
        holder.btnAugmenter.setOnClickListener(v -> {
            int nouvelleQuantite = item.getQuantite() + 1;
            Panier.getInstance().modifierQuantite(film.getFilm_id(), nouvelleQuantite);
            if (listener != null) {
                listener.onPanierChanged();
            }
        });

        // Bouton supprimer
        holder.btnSupprimer.setOnClickListener(v -> {
            Panier.getInstance().supprimerFilm(film.getFilm_id());
            if (listener != null) {
                listener.onPanierChanged();
            }
        });

        return convertView;
    }

    // ViewHolder pour optimiser les performances (principe du cours)
    static class ViewHolder {
        TextView tvTitre;
        TextView tvType;
        TextView tvQuantite;
        TextView tvPrix;
        Button btnDiminuer;
        Button btnAugmenter;
        Button btnSupprimer;
    }
}
