package project.hugo.defreitas.boattracker.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

import project.hugo.defreitas.boattracker.DAO.BoatDAO;
import project.hugo.defreitas.boattracker.R;

/**
 * Affichage du bateau sur la carte et de son port d'origine.
 */
public class ShowBoatOnMapActivity extends FragmentActivity implements OnMapReadyCallback {
    /**
     * Données liées à la vue
     */
    private GoogleMap mMap;
    /** Bateau sur lequel on a clické  */
    private BoatDAO _current_boat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_boat_on_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this._current_boat = (BoatDAO) getIntent().getSerializableExtra("current_boat");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Coordonnée actuelle du bateau
        LatLng boatPos = new LatLng(_current_boat.getLatitude(),_current_boat.getLongitude());
        //Coordonnée du port d'origine
        LatLng harborPos = new LatLng(_current_boat.getOrigin_harbor().getLatitude(),_current_boat.getOrigin_harbor().getLongitude());

        //On ajoute les deux markers
        mMap.addMarker(new MarkerOptions().position(boatPos).title(this._current_boat.getName())); //BoatDAO
        mMap.addMarker(new MarkerOptions().position(harborPos).title(this._current_boat.getOrigin_harbor().getCity())
                .icon(BitmapDescriptorFactory //HarborDAO
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //On trace un trait entre les deux markers
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions
                .add(boatPos)
                .add(harborPos);
        mMap.addPolyline(polylineOptions);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(boatPos));

        Toast.makeText(ShowBoatOnMapActivity.this,
                "Cliquez sur la carte pour déplacer le bateau.",
                Toast.LENGTH_LONG)
                .show();

        final ArrayList<BoatDAO> all_boats = new ArrayList<BoatDAO>();
        BoatDAO.get_all(all_boats);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Pour chaque bateau de la liste des bateaux de la Base de données, on vient mettre on onClickListener pour
                // les tranferts de container.
                for (BoatDAO boatDAO : all_boats){
                    //On ne rajoute pas de clickListeners sur le marker du bateau courant.
                    if(!boatDAO.getID().equals(_current_boat.getID())){
                        LatLng boatDAOPos = new LatLng(boatDAO.getLatitude(),boatDAO.getLongitude());

                        MarkerOptions boatDaoMarkerOptions = new MarkerOptions().position(boatDAOPos).title(boatDAO.getName())
                                .snippet("Volume restant du bateau : "+_current_boat.getLeftingStorage()+" mètres-cubes.")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        Marker boatDAOMarker = mMap.addMarker(boatDaoMarkerOptions);
                        boatDAOMarker.setTag(boatDAO);
                    }
                }
            }
        }, 1000);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent newPositions = new Intent();
                newPositions.putExtra("longitude",latLng.longitude);
                newPositions.putExtra("latitude", latLng.latitude);
                setResult(BoatDetailsActivity.CODE_FOR_BOAT_LOCATION_EDITION,newPositions);
                finish();
            }
        });
    }
}
