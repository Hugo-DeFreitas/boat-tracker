package project.hugo.defreitas.boattracker.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.hugo.defreitas.boattracker.DAO.BoatDAO;
import project.hugo.defreitas.boattracker.DAO.BoatTypeDAO;
import project.hugo.defreitas.boattracker.DAO.CaptainDAO;
import project.hugo.defreitas.boattracker.DAO.DAO;
import project.hugo.defreitas.boattracker.DAO.HarborDAO;
import project.hugo.defreitas.boattracker.R;

/**
 * Activity de création d'un nouveau bateau (enregistrement en BDD).
 */
public class CreateBoatActivity extends AppCompatActivity {
    /** Données liées à la vue */
    EditText _new_boat_name;
    EditText _new_boat_description;
    EditText _new_boat_lat;
    EditText _new_boat_long;
    Spinner _new_boat_origin_harbor;
    Spinner _new_boat_captain;
    Spinner _new_boat_boat_type;
    MaterialFancyButton _save_new_boat;

     /** Données Firebase */
     ArrayList<HarborDAO>   _all_harbors;
     ArrayList<BoatTypeDAO> _all_boat_types;
     ArrayList<CaptainDAO>  _all_captains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity savedContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_boat);
        findViewsById();
        loadAllNecessaryData();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        _save_new_boat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String boatName         = String.valueOf(_new_boat_name.getText());
                String boatDescription  = String.valueOf(_new_boat_description.getText());

                String boatHarbor       = getIDFromSpinner(_new_boat_origin_harbor.getSelectedItem().toString());
                String boatBoatType     = getIDFromSpinner(_new_boat_boat_type.getSelectedItem().toString());
                String boatCaptain      = getIDFromSpinner(_new_boat_captain.getSelectedItem().toString());

                Double boatLat          = Double.valueOf(String.valueOf(_new_boat_lat.getText()));
                Double boatLong         = Double.valueOf(String.valueOf(_new_boat_long.getText()));

                //Le nom du bateau est obligatoire.
                if(boatName.equals("")){
                    Toast.makeText(getApplicationContext(),"Veuillez renseigner un nom pour le bateau.",Toast.LENGTH_SHORT);
                    return;
                }
                //On met commevaleur par défaut la coordonnée 0:0.
                if(String.valueOf(_new_boat_lat.getText()).equals("") || String.valueOf(_new_boat_long.getText()).equals("")){
                    boatLat = 0.0;
                    boatLong = 0.0;
                }
                Map<String, Object> boatData = new HashMap<>();
                boatData.put("name", boatName);
                boatData.put("description", boatDescription);
                boatData.put("latitude", boatLat);
                boatData.put("longitude",boatLong);

                DocumentReference boatTypeRef   = db.collection(BoatTypeDAO.COLLECTION_REFERENCE).document(boatBoatType);
                DocumentReference harborRef     = db.collection(HarborDAO.COLLECTION_REFERENCE).document(boatHarbor);
                DocumentReference captainRef    = db.collection(CaptainDAO.COLLECTION_REFERENCE).document(boatCaptain);

                boatData.put("boat_type",boatTypeRef);
                boatData.put("captain",captainRef);
                boatData.put("origin_harbor",harborRef);

                db.collection(BoatDAO.COLLECTION_REFERENCE)
                        .document(boatName)
                        .set(boatData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent i = new Intent(savedContext,Welcome.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(savedContext,"L'écriture sur Firebase est momentanément indisponible." +
                                        " Contactez un développeur pour plus d'informations.",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void findViewsById(){
        _new_boat_name = findViewById(R.id.new_boat_name);
        _new_boat_description = findViewById(R.id.new_boat_description);
        _new_boat_lat = findViewById(R.id.new_boat_lat);
        _new_boat_long = findViewById(R.id.new_boat_long);

        _new_boat_origin_harbor = findViewById(R.id.new_boat_origin_harbor);
        _new_boat_captain = findViewById(R.id.new_boat_captain);
        _new_boat_boat_type = findViewById(R.id.new_boat_boat_type);

        _save_new_boat = findViewById(R.id.save_new_boat);
    }

    private void loadAllNecessaryData() {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Récupération depuis Firebase");
        progress.setMessage("L'opération peut prendre quelques secondes...");
        progress.setCancelable(false);

        _all_harbors = new ArrayList<HarborDAO>();
        _all_boat_types = new ArrayList<BoatTypeDAO>();
        _all_captains = new ArrayList<CaptainDAO>();

        HarborDAO.get_all(_all_harbors,progress);
        BoatTypeDAO.get_all(_all_boat_types,progress);
        CaptainDAO.get_all(_all_captains,progress);

        final Context savedContext = this;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //On peuple le Spinner des types de bateau.
                final ArrayList<String> simpleListOfBoatTypes = new ArrayList<>();
                for (BoatTypeDAO boatTypeDAO : _all_boat_types) {
                    simpleListOfBoatTypes.add("Nom : " + boatTypeDAO.getName() + " / ID : " + boatTypeDAO.getID());
                }
                ArrayAdapter<String> boatTypeAdapter = new ArrayAdapter<String>(savedContext, android.R.layout.simple_spinner_item, simpleListOfBoatTypes);
                _new_boat_boat_type.setAdapter(boatTypeAdapter);


                //On peuple le Spinner des ports.
                final ArrayList<String> simpleListOfHarbors = new ArrayList<>();
                for (HarborDAO harborDAO : _all_harbors) {
                    simpleListOfHarbors.add("Ville : "+harborDAO.getCity()+" / ID : "+harborDAO.getID());
                }
                ArrayAdapter<String> harborAdapter = new ArrayAdapter<String>(savedContext, android.R.layout.simple_spinner_item, simpleListOfHarbors);
                _new_boat_origin_harbor.setAdapter(harborAdapter);

                //On peuple le Spinner des capitaines.
                final ArrayList<String> simpleListOfCaptains = new ArrayList<>();
                for (CaptainDAO captainDAO : _all_captains) {
                    simpleListOfCaptains.add("Nom : "+captainDAO.getName()+" / ID : "+captainDAO.getID());
                }
                ArrayAdapter<String> captainAdapter = new ArrayAdapter<String>(savedContext, android.R.layout.simple_spinner_item, simpleListOfCaptains);
                _new_boat_captain.setAdapter(captainAdapter);
            }
        }, 1000);
    }

    /**
     *     Petite technique pour récupérer directement l'ID de la Firebase
     *     pour le document qui concerne le Spinner.
     */
    private static String getIDFromSpinner(String str){
        String[] splitArray     = str.split("/");
        String newID    = splitArray[1];
        splitArray = newID.split(":");
        newID = splitArray[1];
        newID = newID.substring(1); //On enlève un espace qui traine.
        return newID;
    }
}
