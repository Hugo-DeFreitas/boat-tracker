package project.hugo.defreitas.boattracker.DAO;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Classe DAO pour les types de Bateaux sur Firebase
 */
public class BoatTypeDAO extends DAO{
    public static final String COLLECTION_REFERENCE = "boatTypes";

    /** Le nom du type de bateau */
    private String name;
    /** Données pour pouvoir mesurer le volume qu'occupe le container sur un bateau. */
    private Long height;
    private Long length;
    private Long width;

    public static void get_all(final ArrayList<BoatTypeDAO> listOfBoatTypes){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(BoatTypeDAO.COLLECTION_REFERENCE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> taskForBoat) {
                if (taskForBoat.isSuccessful()) {
                    for (QueryDocumentSnapshot document : taskForBoat.getResult()) {

                        final BoatTypeDAO boatTypeDAO = new BoatTypeDAO(document.getId());

                        //On itère sur la HashMap reçue depuis la Firebase.
                        Iterator it = document.getData().entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            switch (String.valueOf(pair.getKey())) {
                                case "name":
                                    boatTypeDAO.setName(pair.getValue().toString());
                                    break;
                                case "length":
                                    if(pair.getValue() instanceof Double){
                                        Double length = (Double) pair.getValue();
                                        boatTypeDAO.setLength(length.longValue());
                                    }
                                    else{
                                        Long length = (Long) pair.getValue();
                                        boatTypeDAO.setLength(length);
                                    }
                                    break;
                                case "height":
                                    if(pair.getValue() instanceof Double){
                                        Double height = (Double) pair.getValue();
                                        boatTypeDAO.setHeight(height.longValue());
                                    }
                                    else{
                                        Long height = (Long) pair.getValue();
                                        boatTypeDAO.setHeight(height);
                                    }
                                    break;
                                case "width":
                                    if(pair.getValue() instanceof Double){
                                        Double width = (Double) pair.getValue();
                                        boatTypeDAO.setWidth(width.longValue());
                                    }
                                    else{
                                        Long width = (Long) pair.getValue();
                                        boatTypeDAO.setWidth(width);
                                    }
                                    break;
                            }
                        }
                        listOfBoatTypes.add(boatTypeDAO);
                    }
                }
            }
        });
    }

    public static void get_all(final ArrayList<BoatTypeDAO> listOfBoatTypes, final ProgressDialog progressDialog){
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(BoatTypeDAO.COLLECTION_REFERENCE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> taskForBoat) {
                if (taskForBoat.isSuccessful()) {
                    for (QueryDocumentSnapshot document : taskForBoat.getResult()) {

                        final BoatTypeDAO boatTypeDAO = new BoatTypeDAO(document.getId());

                        //On itère sur la HashMap reçue depuis la Firebase.
                        Iterator it = document.getData().entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            switch (String.valueOf(pair.getKey())) {
                                case "name":
                                    boatTypeDAO.setName(pair.getValue().toString());
                                    break;
                                case "length":
                                    if(pair.getValue() instanceof Double){
                                        Double length = (Double) pair.getValue();
                                        boatTypeDAO.setLength(length.longValue());
                                    }
                                    else{
                                        Long length = (Long) pair.getValue();
                                        boatTypeDAO.setLength(length);
                                    }
                                    break;
                                case "height":
                                    if(pair.getValue() instanceof Double){
                                        Double height = (Double) pair.getValue();
                                        boatTypeDAO.setHeight(height.longValue());
                                    }
                                    else{
                                        Long height = (Long) pair.getValue();
                                        boatTypeDAO.setHeight(height);
                                    }
                                    break;
                                case "width":
                                    if(pair.getValue() instanceof Double){
                                        Double width = (Double) pair.getValue();
                                        boatTypeDAO.setWidth(width.longValue());
                                    }
                                    else{
                                        Long width = (Long) pair.getValue();
                                        boatTypeDAO.setWidth(width);
                                    }
                                    break;
                            }
                        }
                        listOfBoatTypes.add(boatTypeDAO);
                    }
                    progressDialog.dismiss();
                }
            }
        });
    }

    /** Getters et Setters */
    public BoatTypeDAO(String ID) {
        super(ID);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getHeight() {
        return height;
    }
    public void setHeight(Long height) {
        this.height = height;
    }
    public Long getLength() {
        return length;
    }
    public void setLength(Long length) {
        this.length = length;
    }
    public Long getWidth() {
        return width;
    }
    public void setWidth(Long width) {
        this.width = width;
    }
    public long getTotalVolume(){
        return getHeight() * getWidth() * getLength();
    }
}
