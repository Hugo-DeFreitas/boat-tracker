package project.hugo.defreitas.boattracker.DAO;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

import project.hugo.defreitas.boattracker.classes.BoatListAdapter;

/**
 * Classe mère pour former un ORM Maison pour Firebase.
 */
public class DAO implements Serializable {
    /**
     * Cette variable sur toutes les classes filles, permettra de récupérer la référence de la collection sur Firebase
     */
    public static final String COLLECTION_REFERENCE = "";

    private String ID;

    /**
     * @param ID, l'ID du document sur Firebase.
     */
    public DAO(String ID) {
        this.ID = ID;
    }

    /**
     * Met à jour un champs d'un document en BDD.
     * @param collectionReference, la collection qui contient le document.
     * @param documentID, le document que l'on souhaite éditer.
     * @param fieldToUpdate, le champs à mettre à jour.
     * @param newValue, la nouvelle valeur à assigner.
     */
    public static void update(String collectionReference, String documentID,String fieldToUpdate, String newValue){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionReference)
                .document(documentID)
                .update(fieldToUpdate, newValue);
    }
    /** Version avec DocumentReference */
    public static void update(String collectionReference, String documentID,String fieldToUpdate, DocumentReference newValue){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionReference)
                .document(documentID)
                .update(fieldToUpdate, newValue);
    }

    /**
     * Récupère depuis la BDD Firebase les données pour les stocker dans une ListView.
     * @param db, la Firebase du projet.
     * @param saveContext, le contexte de l'application.
     * @param boatList, la ListView qui contient les bateaux dans l'activité Welcome.
     * @param progress
     */
    public static void loadFirebaseData(FirebaseFirestore db, Context saveContext, ListView boatList, ProgressDialog progress){
        //Récupération de tous les types de bateaux.
        final ArrayList<BoatTypeDAO> listOfBoatTypes = new ArrayList<BoatTypeDAO>();
        BoatTypeDAO.get_all(listOfBoatTypes);

        //Récupération de tous les containers.
        final ArrayList<ContainerDAO> listOfContainers = new ArrayList<ContainerDAO>();
        ContainerDAO.get_all(db,listOfContainers);

        // Récupération de tous les bateaux depuis la Firebase.
        //Pour chaque bateau reçu depuis la BDD, on l'ajoute dans notre liste.
        ArrayList<BoatDAO> _all_boats_from_db = new ArrayList<BoatDAO>();
        //On met un adapter pour pouvoir ranger les bateaux dans la ListView
        final BoatListAdapter boatListAdapter = new BoatListAdapter(saveContext, _all_boats_from_db, listOfContainers);
        boatList.setAdapter(boatListAdapter);
        boatListAdapter.set_all_boat_types(listOfBoatTypes);

        BoatDAO.get_all(db,_all_boats_from_db,boatListAdapter,progress);
    }

    public String getID() {return ID;}
    public void setID(String ID) {this.ID = ID;}
}
