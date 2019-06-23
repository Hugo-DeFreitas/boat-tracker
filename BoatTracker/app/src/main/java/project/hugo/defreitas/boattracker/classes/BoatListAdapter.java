package project.hugo.defreitas.boattracker.classes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import project.hugo.defreitas.boattracker.DAO.BoatTypeDAO;
import project.hugo.defreitas.boattracker.DAO.ContainerDAO;
import project.hugo.defreitas.boattracker.activity.BoatDetailsActivity;
import project.hugo.defreitas.boattracker.DAO.BoatDAO;
import project.hugo.defreitas.boattracker.R;
import project.hugo.defreitas.boattracker.activity.Welcome;

import java.util.ArrayList;

/**
 * Classe Adapter pour pouvoir afficher une liste de bateaux
 */
public class BoatListAdapter extends BaseAdapter {

    /**
     * Données membres
     */
    private ArrayList<BoatDAO>  _boat_list;
    private ArrayList<BoatTypeDAO> _all_boat_types;
    private ArrayList<ContainerDAO> _all_containers;
    private Context             _context;
    private LayoutInflater      _layout_inflater;

    /**
     * Constructeur
     */
    public BoatListAdapter(Context context, ArrayList<BoatDAO> boatList, ArrayList<ContainerDAO> _all_containers) {
        this._context = context;
        if(boatList != null){
            this._boat_list = boatList;
        }
        if(_all_containers != null){
            this._all_containers = _all_containers;
        }
        this._layout_inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return this._boat_list.size();
    }

    @Override
    public BoatDAO getItem(int position) {
        return this._boat_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = _layout_inflater.inflate(R.layout.boat_list_element,null);
            holder = new ViewHolder();

            holder.boat_name = (MaterialFancyButton) convertView.findViewById(R.id.boat_name);
            holder.get_boat_info_button = (MaterialFancyButton) convertView.findViewById(R.id.btn_boat_infos);
            holder.delete_boat = (MaterialFancyButton) convertView.findViewById(R.id.btn_delete_boat);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //On initialise les labels avec les valeurs du bateau courant.
        final BoatDAO aBoat = this._boat_list.get(position);
        holder.boat_name.setText(aBoat.getName());
        holder.get_boat_info_button.setText(""); //On vide le texte, car la librairie met un texte par défaut.
        /**
         * Listener sur la liste des Bateaux. On doit :
         *  - afficher un Toast qui renseigne à propos du nom du bateau.
         *  - lancer une activité donnant des détails sur le bateau cliqué.
         *  - supprimer le bateau sélectionné
         */
        holder.get_boat_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Création de l'activité de détails des bateaux avec les Intents.
                Intent i = new Intent(_context, BoatDetailsActivity.class);
                i.putExtra("current_boat", aBoat);
                i.putExtra("all_boat_types", _all_boat_types);
                i.putExtra("all_containers_from_current_boat", getFilteredContainersList(aBoat));
                //Lancement de l'activité
                _context.startActivity(i);
            }
        });

        holder.delete_boat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(BoatDAO.COLLECTION_REFERENCE)
                        .document(aBoat.getID())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //On reset la page
                                Intent i = new Intent(_context, Welcome.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                _context.startActivity(i);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                e.printStackTrace();
                                Toast.makeText(_context,"Suppression impossible depuis la Firebase. " +
                                        "Veuillez contacter un développeur.",Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        return convertView;
    }

    public void addBoat(BoatDAO boatDAO){
        this._boat_list.add(boatDAO);
    }

    public class ViewHolder {
        MaterialFancyButton boat_name;
        MaterialFancyButton get_boat_info_button;
        MaterialFancyButton delete_boat;
    }


    public ArrayList<BoatTypeDAO> get_all_boat_types() {
        return _all_boat_types;
    }

    public void set_all_boat_types(ArrayList<BoatTypeDAO> _all_boat_types) {
        this._all_boat_types = _all_boat_types;
    }

    private ArrayList<ContainerDAO> getFilteredContainersList(BoatDAO boatDAO){
        ArrayList<ContainerDAO> listFiltered = new ArrayList<ContainerDAO>();

        for (ContainerDAO containerDAO : _all_containers ) {
            if(containerDAO.getBoat_id().equals(boatDAO.getID())){
                boatDAO.addContainer(containerDAO);
                listFiltered.add(containerDAO);
            }
        }

        return listFiltered;
    }
}
