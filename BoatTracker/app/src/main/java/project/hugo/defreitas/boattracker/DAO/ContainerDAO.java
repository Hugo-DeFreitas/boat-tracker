package project.hugo.defreitas.boattracker.DAO;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * DAO d'un container sur Firebase
 */
public class ContainerDAO extends DAO {
    public static final String COLLECTION_REFERENCE = "containers";

    /** L'ID du document de type 'boat' du container : il s'agit donc du bateau sur lequel il est actuellement */
    private String boat_id;
    /** Données de mesures, utiles pour calculer le volume qu'occupe un container */
    private Long height;
    private Long width;
    private Long length;

    public ContainerDAO(String ID) {
        super(ID);
    }

    /**
     * Remplit la liste de Container passée en paramètres, avec tous les containers de la collection Firebase 'containers'
     * @param db, la Firebase du projet.
     * @param listOfContainers, la liste que l'on souhaite remplir.
     */
    public static void get_all(FirebaseFirestore db, final ArrayList<ContainerDAO> listOfContainers){
        db.collection(ContainerDAO.COLLECTION_REFERENCE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> taskForContainers) {
                if (taskForContainers.isSuccessful()) {
                    for (QueryDocumentSnapshot document : taskForContainers.getResult()) {

                        final ContainerDAO containerDAO = new ContainerDAO(document.getId());

                        //On itère sur la HashMap reçue depuis la Firebase.
                        Iterator it = document.getData().entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            switch (String.valueOf(pair.getKey())) {
                                case "boat":
                                    DocumentReference boatRef = (DocumentReference) pair.getValue();
                                    containerDAO.setBoat_id(boatRef.getId());
                                    break;
                                case "length":
                                    if(pair.getValue() instanceof Double){
                                        Double length = (Double) pair.getValue();
                                        containerDAO.setLength(length.longValue());
                                    }
                                    else{
                                        Long length = (Long) pair.getValue();
                                        containerDAO.setLength(length);
                                    }
                                    break;
                                case "height":
                                    if(pair.getValue() instanceof Double){
                                        Double height = (Double) pair.getValue();
                                        containerDAO.setHeight(height.longValue());
                                    }
                                    else{
                                        Long height = (Long) pair.getValue();
                                        containerDAO.setHeight(height);
                                    }
                                    break;
                                case "width":
                                    if(pair.getValue() instanceof Double){
                                        Double width = (Double) pair.getValue();
                                        containerDAO.setWidth(width.longValue());
                                    }
                                    else{
                                        Long width = (Long) pair.getValue();
                                        containerDAO.setWidth(width);
                                    }
                                    break;
                            }
                        }
                        listOfContainers.add(containerDAO);
                    }
                }
            }
        });
    }

    /** Getters et Setters */
    public String getBoat_id() {return boat_id;}
    public void setBoat_id(String boat_id) {this.boat_id = boat_id;}
    public Long getVolume(){ return getHeight()*getLength()*getWidth(); }
    public Long getHeight() {return height; }
    public void setHeight(Long height) {this.height = height;}
    public Long getWidth() {return width;}
    public void setWidth(Long width) {this.width = width;}
    public Long getLength() {return length;}
    public void setLength(Long length) {this.length = length;}
}
