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
 * DAO d'un capitaine sur Firebase.
 */
public class CaptainDAO extends DAO {
    public static final String COLLECTION_REFERENCE = "captains";
    /** Le nom du capitaine */
    private String name;

    public CaptainDAO(String ID) { super(ID); }

    public static void get_all(final ArrayList<CaptainDAO> listOfCaptains){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CaptainDAO.COLLECTION_REFERENCE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> taskForContainers) {
                if (taskForContainers.isSuccessful()) {
                    for (QueryDocumentSnapshot document : taskForContainers.getResult()) {

                        final CaptainDAO captainDAO = new CaptainDAO(document.getId());

                        //On itère sur la HashMap reçue depuis la Firebase.
                        Iterator it = document.getData().entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            switch (String.valueOf(pair.getKey())) {

                                case "name":
                                    captainDAO.setName((String) pair.getValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        listOfCaptains.add(captainDAO);
                    }
                }
            }
        });
    }

    public static void get_all(final ArrayList<CaptainDAO> listOfCaptains, final ProgressDialog progressDialog){
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CaptainDAO.COLLECTION_REFERENCE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> taskForContainers) {
                if (taskForContainers.isSuccessful()) {
                    for (QueryDocumentSnapshot document : taskForContainers.getResult()) {

                        final CaptainDAO captainDAO = new CaptainDAO(document.getId());

                        //On itère sur la HashMap reçue depuis la Firebase.
                        Iterator it = document.getData().entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            switch (String.valueOf(pair.getKey())) {

                                case "name":
                                    captainDAO.setName((String) pair.getValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        listOfCaptains.add(captainDAO);
                    }
                    progressDialog.dismiss();
                }
            }
        });
    }

    /** Getters et Setters */
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}