package project.hugo.defreitas.boattracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.ArrayList;

import project.hugo.defreitas.boattracker.DAO.BoatDAO;
import project.hugo.defreitas.boattracker.DAO.BoatTypeDAO;
import project.hugo.defreitas.boattracker.DAO.DAO;
import project.hugo.defreitas.boattracker.DAO.HarborDAO;
import project.hugo.defreitas.boattracker.R;

/**
 * Activité qui renseigne sur les détails d'un bateau.
 */
public class BoatDetailsActivity extends AppCompatActivity {
    /**
     * Données liées à la vue
     */
    private MaterialFancyButton _boat_name;             //Boutton qui contient le nom du bateau
    private MaterialFancyButton _boat_type;             //Boutton qui contient le type du bateau
    private MaterialFancyButton _show_volume_of_boat;   //Boutton qui contient le volume disponible du bateau
    private MaterialFancyButton _boat_captain_name;     //Boutton qui contient le nom du capitaine du bateau
    private MaterialFancyButton _boat_description;      //Boutton qui contient la description du bateau
    /** Actions */
    private MaterialFancyButton _calculate_distance;    //Boutton qui permet de calculer la distance du bateau depuis son port d'origine
    private MaterialFancyButton _show_boat_on_map;      //Boutton permet d'afficher le bateau sur une carte du monde, et de modifier la position du bateau
    private MaterialFancyButton _show_containers_on_boat; //Boutton qui permet d'afficher les containers présents sur le bateau et de faire diverses actions.
    /** Editions en Base de données */
    private MaterialFancyButton _edit_boat_description; //Boutton qui permet l'édition de la description du bateau
    private MaterialFancyButton _edit_boat_name;        //Boutton d'édition du nom du bateau
    private MaterialFancyButton _edit_boat_type;        //Boutton qui contient le nom du bateau

    /**
     * Données récupérées depuis l'activité précédente.
     */
    private BoatDAO _current_boat; // Le BoatDAO sur lequel on a clické

    /** Autres */
    public static int CODE_FOR_BOAT_LOCATION_EDITION = 101; //Code de requête pour changer la localisation du bateau.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity savedContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_details);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Récupération du bateau en question, et de la liste des types de bateau disponibles en BDD (Utile pour peupler le Spinner de choix du type de bateau)
        _current_boat = (BoatDAO) getIntent().getSerializableExtra("current_boat");
        ArrayList<BoatTypeDAO> _all_boat_types = (ArrayList<BoatTypeDAO>) getIntent().getSerializableExtra("all_boat_types");

        //Récupération des éléments de la vue
        _boat_name                  = (MaterialFancyButton) findViewById(R.id.boat_name);
        _edit_boat_name            = (MaterialFancyButton) findViewById(R.id.edit_boat_name);
        _edit_boat_type             = (MaterialFancyButton) findViewById(R.id.edit_boat_type);
        _show_containers_on_boat    = (MaterialFancyButton) findViewById(R.id.label_container_list);
        _boat_type                  = (MaterialFancyButton) findViewById(R.id.boat_type);
        _calculate_distance         = (MaterialFancyButton) findViewById(R.id.get_distance_from_harbor);
        _show_boat_on_map           = (MaterialFancyButton) findViewById(R.id.show_boat_on_map);
        _show_volume_of_boat        = (MaterialFancyButton) findViewById(R.id.boat_lefting_volume);
        _boat_captain_name          = (MaterialFancyButton) findViewById(R.id.boat_captain_name);
        _boat_description           = (MaterialFancyButton) findViewById(R.id.boat_description);
        _edit_boat_description      = (MaterialFancyButton) findViewById(R.id.edit_boat_description);

        //On change les textes
        _show_volume_of_boat.setText(String.valueOf(_current_boat.getLeftingStorage())+" m3");
        _boat_captain_name.setText(_current_boat.getCaptain().getName());
        _boat_description.setText(_current_boat.getDescription());
        _boat_name.setText(_current_boat.getName());
        _boat_type.setText(_current_boat.getBoat_type().getName());

        _edit_boat_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomInputDialog(savedContext, new CustomInputDialog.InputSenderDialogListener() {
                    @Override
                    public void onOK(final String newValue) {
                        System.out.println("Valeur entrée par l'utilisateur : " + newValue);
                        //On enregistre en BDD
                        DAO.update(BoatDAO.COLLECTION_REFERENCE,
                                _current_boat.getID(),
                                "name",
                                newValue);
                        //On recharge la page depuis l'accueil
                        Intent intent = new Intent(getApplicationContext(), Welcome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancel(String newValue) {
                        Toast.makeText(getApplicationContext(),"Le nouveau nom '"+newValue+"' n'a pas été appliqué.",Toast.LENGTH_LONG).show();
                    }
                },"Modification du nom de bateau","Nom du bateau").setCurrentValueOfInput(_current_boat.getName()).show();
            }
        });

        _edit_boat_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomInputDialog(savedContext, new CustomInputDialog.InputSenderDialogListener() {
                    @Override
                    public void onOK(final String newValue) {
                        System.out.println("Valeur entrée par l'utilisateur : " + newValue);
                        //On enregistre en BDD
                        DAO.update(BoatDAO.COLLECTION_REFERENCE,
                                _current_boat.getID(),
                                "description",
                                newValue);
                        //On recharge la page depuis l'accueil
                        Intent intent = new Intent(getApplicationContext(), Welcome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancel(String newValue) {
                        Toast.makeText(getApplicationContext(),"Le nouveau nom '"+newValue+"' n'a pas été appliqué.",Toast.LENGTH_LONG).show();
                    }
                },"Modification de la description d'un bateau","Description du bateau").setCurrentValueOfInput(_current_boat.getDescription()).show();
            }
        });

        //Conversion de la liste de tous les types de bateaux, vers une ArrayList de string (pour que l'adapter puisse faire correctement son travail.
        final ArrayList<String> simpleListOfBoatTypes = new ArrayList<>();
        for (BoatTypeDAO boatTypeDAO : _all_boat_types) {
            simpleListOfBoatTypes.add("Nom : "+boatTypeDAO.getName()+" / ID : "+boatTypeDAO.getID());
        }

        _edit_boat_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomInputDialogWithSpinner(savedContext, new CustomInputDialogWithSpinner.InputSenderDialogListener() {
                    @Override
                    public void onOK(String newValue) {
                        System.out.println("Valeur entrée par l'utilisateur : " + newValue);
                        //Petite technique pour récupérer directement l'ID de la Firebase pour le document des types de bateaux, depuis le nouveau résultat de l'utilisateur.
                        String[] splitArray     = newValue.split("/");
                        String newBoatTypeID    = splitArray[1];
                        splitArray = newBoatTypeID.split(":");
                        newBoatTypeID = splitArray[1];
                        newBoatTypeID = newBoatTypeID.substring(1); //On enlève un espace qui traine.
                        System.out.println("Nouvel ID de type de Bateau sélectionné: " + newBoatTypeID);

                        //Récupérons la DocumentReference du type de bateau maintenant que nous disposons de l'ID.
                        DocumentReference boatTypeRef = db.collection(BoatTypeDAO.COLLECTION_REFERENCE).document(newBoatTypeID);

                        DAO.update(BoatDAO.COLLECTION_REFERENCE,
                                _current_boat.getID(),
                                "boat_type", boatTypeRef);
                        //On recharge la page depuis l'accueil
                        Intent intent = new Intent(getApplicationContext(), Welcome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancel(String newValue) {
                        System.out.println(newValue);
                    }
                },"Modification du type de bateau","Type du bateau",simpleListOfBoatTypes).show();
            }
        });


        _calculate_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int distance = Math.round(_current_boat.get_distance_from_origin_harbor() / 1000);
                Toast.makeText(getApplicationContext(),"Le bateau "+ _current_boat.getName() + " se trouve à " + distance + " km de son port d'origine.",Toast.LENGTH_LONG).show();
            }
        });

        _show_boat_on_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Création de l'activité de détails des bateaux avec les Intents.
                Intent i = new Intent(v.getContext(), ShowBoatOnMapActivity.class);
                i.putExtra("current_boat",_current_boat);
                //Lancement de l'activité
                startActivityForResult(i,CODE_FOR_BOAT_LOCATION_EDITION);
            }
        });

        _show_containers_on_boat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Création de l'activité de détails des bateaux avec les Intents.
                Intent i = new Intent(v.getContext(), ContainersOnBoatActivity.class);
                i.putExtra("current_boat",_current_boat);
                //Lancement de l'activité
                startActivity(i);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_FOR_BOAT_LOCATION_EDITION) {
            if(data != null && data.hasExtra("latitude") && data.hasExtra("longitude")){

                Double newLat    = (Double) data.getSerializableExtra("latitude");
                Double newLong   = (Double) data.getSerializableExtra("longitude");
                System.out.println("Nouvelle latitude pour le bateau "+_current_boat.getName()+": ");
                System.out.println("Nouvelle longitude pour le bateau "+_current_boat.getName()+": ");
                //Envoi vers la BDD
                db.collection(BoatDAO.COLLECTION_REFERENCE)
                        .document(_current_boat.getID())
                        .update("latitude",newLat);
                db.collection(BoatDAO.COLLECTION_REFERENCE)
                        .document(_current_boat.getID())
                        .update("longitude", newLong);

                //On recharge la page depuis l'accueil
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("is_connected",true);
                startActivity(intent);
            }
        }
    }
}
