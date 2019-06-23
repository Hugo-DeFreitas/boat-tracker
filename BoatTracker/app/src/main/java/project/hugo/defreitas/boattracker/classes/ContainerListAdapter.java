package project.hugo.defreitas.boattracker.classes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.ArrayList;

import project.hugo.defreitas.boattracker.DAO.BoatDAO;
import project.hugo.defreitas.boattracker.DAO.BoatTypeDAO;
import project.hugo.defreitas.boattracker.DAO.ContainerDAO;
import project.hugo.defreitas.boattracker.R;
import project.hugo.defreitas.boattracker.activity.BoatDetailsActivity;
import project.hugo.defreitas.boattracker.activity.ShowBoatOnMapActivity;
import project.hugo.defreitas.boattracker.activity.TransferContainerOnMapActivity;

/**
 * Classe Adapter pour pouvoir afficher une liste de bateaux
 */
public class ContainerListAdapter extends BaseAdapter {

    /**
     * Données membres
     */
    private BoatDAO _boat_concerned;
    private ArrayList<ContainerDAO> _all_containers_from_boat;
    private ArrayList<BoatDAO> _all_boats;
    private Context             _context;
    private LayoutInflater      _layout_inflater;

    private static int CODE_FOR_CONTAINER_TRANSFER = 102;

    /**
     * Constructeur
     */
    public ContainerListAdapter(Context context, BoatDAO boatConcerned) {
        _context = context;
        _all_containers_from_boat = new ArrayList<ContainerDAO>();
        if(boatConcerned != null){
            _boat_concerned = boatConcerned;
            _all_containers_from_boat = boatConcerned.getContainers();
        }
        _layout_inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return this._all_containers_from_boat.size();
    }
    @Override
    public ContainerDAO getItem(int position) {return this._all_containers_from_boat.get(position);}
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = _layout_inflater.inflate(R.layout.container_list_element,null);
            holder = new ViewHolder();
            holder.container_id     = (MaterialFancyButton) convertView.findViewById(R.id.container_id);
            holder.container_heigth = (MaterialFancyButton) convertView.findViewById(R.id.container_height);
            holder.container_lenght = (MaterialFancyButton) convertView.findViewById(R.id.container_length);
            holder.container_width  = (MaterialFancyButton) convertView.findViewById(R.id.container_width);
            holder.edit_boat_of_container  = (MaterialFancyButton) convertView.findViewById(R.id.transfer_container_to_another_boat);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //On initialise les labels avec les valeurs du bateau courant.
        final ContainerDAO aContainer = this._all_containers_from_boat.get(position);
        holder.container_id.setText(aContainer.getID());
        holder.container_lenght.setText(String.valueOf(aContainer.getLength()));
        holder.container_width.setText(String.valueOf(aContainer.getWidth()));
        holder.container_heigth.setText(String.valueOf(aContainer.getHeight()));
        holder.edit_boat_of_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), TransferContainerOnMapActivity.class);
                i.putExtra("current_container",aContainer);
                i.putExtra("current_boat",_boat_concerned);
                _context.startActivity(i);

            }
        });

        return convertView;
    }

    public class ViewHolder {
        MaterialFancyButton container_id;
        MaterialFancyButton container_width;
        MaterialFancyButton container_lenght;
        MaterialFancyButton container_heigth;
        MaterialFancyButton edit_boat_of_container;
    }
}
