package project.hugo.defreitas.boattracker.DAO;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Class HarborDAO, les ports associés aux Bateaux pour Firebase.
 */
public class HarborDAO extends DAO{
    public static final String COLLECTION_REFERENCE = "originHarbor";

    /** Nom de la localité du Port (Ville, Village, etc..) */
    private String city;
    /** Coordonnées GPS du port */
    private Long latitude;
    private Long longitude;

    public HarborDAO(String ID) {
        super(ID);
    }

    public static void get_all(final ArrayList<HarborDAO> listOfHarbors){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(HarborDAO.COLLECTION_REFERENCE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> taskForContainers) {
                if (taskForContainers.isSuccessful()) {
                    for (QueryDocumentSnapshot document : taskForContainers.getResult()) {

                        final HarborDAO harborDAO = new HarborDAO(document.getId());

                        //On itère sur la HashMap reçue depuis la Firebase.
                        Iterator it = document.getData().entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            switch (String.valueOf(pair.getKey())) {
                                case "city":
                                    harborDAO.setCity((String) pair.getValue());
                                    break;
                                case "latitude":
                                    Double lat = (Double) pair.getValue();
                                    harborDAO.setLatitude(lat.longValue());
                                    break;
                                case "longitude":
                                    Double longC = (Double) pair.getValue();
                                    harborDAO.setLongitude(longC.longValue());
                                default:
                                    break;
                            }
                        }
                        listOfHarbors.add(harborDAO);
                    }
                }
            }
        });
    }

    public static void get_all(final ArrayList<HarborDAO> listOfHarbors, final ProgressDialog progressDialog){
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(HarborDAO.COLLECTION_REFERENCE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> taskForContainers) {
                if (taskForContainers.isSuccessful()) {
                    for (QueryDocumentSnapshot document : taskForContainers.getResult()) {

                        final HarborDAO harborDAO = new HarborDAO(document.getId());

                        //On itère sur la HashMap reçue depuis la Firebase.
                        Iterator it = document.getData().entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            switch (String.valueOf(pair.getKey())) {
                                case "city":
                                    harborDAO.setCity((String) pair.getValue());
                                    break;
                                case "latitude":
                                    Double lat = (Double) pair.getValue();
                                    harborDAO.setLatitude(lat.longValue());
                                    break;
                                case "longitude":
                                    Double longC = (Double) pair.getValue();
                                    harborDAO.setLongitude(longC.longValue());
                                default:
                                    break;
                            }
                        }
                        listOfHarbors.add(harborDAO);
                    }
                    progressDialog.dismiss();
                }
            }
        });
    }

    /** Getters et Setters */
    public void setPosition(Long latitude,Long longitude) { this.latitude = latitude;this.longitude = longitude;}
    public Long getLatitude() {return latitude;}
    public void setLatitude(Long latitude) {this.latitude = latitude;}
    public Long getLongitude() {return longitude;}
    public void setLongitude(Long longitude) {this.longitude = longitude;}
    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}
}
