package project.hugo.defreitas.boattracker.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import project.hugo.defreitas.boattracker.DAO.BoatDAO;
import project.hugo.defreitas.boattracker.DAO.BoatTypeDAO;
import project.hugo.defreitas.boattracker.DAO.ContainerDAO;
import project.hugo.defreitas.boattracker.DAO.DAO;
import project.hugo.defreitas.boattracker.R;
import project.hugo.defreitas.boattracker.classes.BoatListAdapter;

/**
 * Affichage du bateau sur la carte et de son port d'origine.
 */
public class TransferContainerOnMapActivity extends FragmentActivity implements OnMapReadyCallback {
    /**
     * Données liées à la vue
     */
    private GoogleMap mMap;
    /** Bateau sur lequel on a clické  */
    private ContainerDAO    _current_container;
    private BoatDAO         _current_boat;
    private ArrayList<BoatDAO> _all_boats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_container);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_for_container_transfer);
        mapFragment.getMapAsync(this);
        _current_container = (ContainerDAO) getIntent().getSerializableExtra("current_container");
        _current_boat      = (BoatDAO) getIntent().getSerializableExtra("current_boat");
        _all_boats = new ArrayList<BoatDAO>();
        BoatDAO.get_all(_all_boats);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Coordonnée actuelle du bateau
        final LatLng boatPos = new LatLng(_current_boat.getLatitude(),_current_boat.getLongitude());

        //On ajoute les deux markers
        final MarkerOptions currentBoatMarkerOptions = new MarkerOptions().position(boatPos).title(this._current_container.getID())
                .snippet("Container actuel. Volume restant du bateau : "+_current_boat.getLeftingStorage() +" mètres-cubes.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        final Marker currentBoatMarker = mMap.addMarker(currentBoatMarkerOptions);
        currentBoatMarker.setTag(_current_boat);

        //On attend 1 seconde le temps que la tâche de récupération des DAO finisse.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Pour chaque bateau de la liste des bateaux de la Base de données, on vient mettre on onClickListener pour
                // les tranferts de container.
                for (BoatDAO boatDAO : _all_boats){
                    //On ne rajoute pas de clickListeners sur le marker du bateau courant.
                    if(!boatDAO.getID().equals(_current_boat.getID())){
                        LatLng boatDAOPos = new LatLng(boatDAO.getLatitude(),boatDAO.getLongitude());

                        MarkerOptions boatDaoMarkerOptions = new MarkerOptions().position(boatDAOPos).title(boatDAO.getName())
                                .snippet("Volume restant du bateau : "+_current_boat.getLeftingStorage()+" mètres-cubes.")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        Marker boatDAOMarker = mMap.addMarker(boatDaoMarkerOptions);
                        boatDAOMarker.setTag(boatDAO);
                    }
                }
            }
        }, 1000);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(boatPos));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                BoatDAO boatSelected = (BoatDAO) marker.getTag();

                Location locationA = new Location("Bateau actuel du container");
                locationA.setLatitude(_current_boat.getLatitude());
                locationA.setLongitude(_current_boat.getLongitude());

                Location locationB = new Location("Bateau cible du transfert de container");
                locationB.setLatitude(boatSelected.getLatitude());
                locationB.setLongitude(boatSelected.getLongitude());

                float distance = locationA.distanceTo(locationB);

                if(boatSelected.getID().equals(_current_boat.getID())){
                    Toast.makeText(getApplicationContext(),"Le container est déjà présent sur ce bateau.",Toast.LENGTH_LONG).show();
                    return false;
                }
                if(!(distance <= 300)){
                    Toast.makeText(getApplicationContext(),"Pour pouvoir déplacer le container, il faut que les portes-containers " +
                            "soit à moins de 300 mètres (Vous pouvez les déplacer dans en cliquant sur 'Voir le bateau sur la carte' du menu précédent). "+
                                    "Distance actuelle : " +
                            distance/1000+" km.",Toast.LENGTH_LONG).show();
                    return false;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference newBoatForContainerReference = db.collection(BoatDAO.COLLECTION_REFERENCE).document(boatSelected.getID());

                DAO.update(ContainerDAO.COLLECTION_REFERENCE,_current_container.getID(),"boat",newBoatForContainerReference);

                //On recharge la page depuis l'accueil
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });
    }
}
