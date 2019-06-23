package project.hugo.defreitas.boattracker.DAO;

import android.app.ProgressDialog;
import android.location.Location;
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

import project.hugo.defreitas.boattracker.classes.BoatListAdapter;


/**
 * Classe DAO BoatDAO pour Firebase
 */
public class BoatDAO extends DAO{
    public static final String COLLECTION_REFERENCE = "boats";
    /** Le type du bateau */
    private BoatTypeDAO boat_type;
    /** Le port d'origine du bateau */
    private HarborDAO origin_harbor;
    /** Le capitaine du bateau */
    private CaptainDAO captain;
    /** Une description du bateau */
    private String description;
    private String name;
    /** Coordonnées actuelles du bateau */
    private Double latitude;
    private Double longitude;
    /** La liste des containers à bord */
    private ArrayList<ContainerDAO> containers;

    public BoatDAO(String ID) {
        super(ID);
        containers = new ArrayList<ContainerDAO>();
    }

    /**
     * Récupère toutes les Documents de type 'boat' sur Firebase.
     * @param db, la Firebase du projet.
     * @param listOfBoats, une liste de bateaux que l'on vient incrémenter par référence.
     * @param boatListAdapter, l'adapter pour pouvoir venir peupler la ListView.
     */
    public static void get_all(FirebaseFirestore db, final ArrayList<BoatDAO> listOfBoats, final BoatListAdapter boatListAdapter, final ProgressDialog progress){
        db.collection(BoatDAO.COLLECTION_REFERENCE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> taskForBoat) {
                if (taskForBoat.isSuccessful()) {
                    /*
                     * On va ici itérer afin de récupérer petit à petit toutes les informations depuis la BDD pour les stocker dans les classes Java que l'on a créé.
                     */
                    for (QueryDocumentSnapshot document : taskForBoat.getResult()) {

                        final BoatDAO boatDAO = new BoatDAO(document.getId());

                        //On itère sur la HashMap reçue depuis la Firebase.
                        Iterator it = document.getData().entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();

                            switch (String.valueOf(pair.getKey())) {

                                /*
                                Il s'agit d'une référence vers un document de type 'boat_type' en BDD. On doit donc faire une nouvelle requête.
                                 */
                                case "boat_type":
                                    DocumentReference boatTypeReference = (DocumentReference) pair.getValue();
                                    boatTypeReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> taskForBoatType) {

                                            if (taskForBoatType.isSuccessful()) {
                                                DocumentSnapshot result = taskForBoatType.getResult();
                                                boatDAO.setBoat_type(new BoatTypeDAO(result.getId()));

                                                //On itère sur la HashMap reçue depuis la Firebase.
                                                Iterator it = result.getData().entrySet().iterator();
                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry) it.next();
                                                    switch (String.valueOf(pair.getKey())) {
                                                        case "name":
                                                            boatDAO.getBoat_type().setName((String) pair.getValue());
                                                            break;
                                                        case "length":
                                                            if(pair.getValue() instanceof Double){
                                                                Double length = (Double) pair.getValue();
                                                                boatDAO.getBoat_type().setLength(length.longValue());
                                                            }
                                                            else{
                                                                Long length = (Long) pair.getValue();
                                                                boatDAO.getBoat_type().setLength(length);
                                                            }
                                                            break;
                                                        case "height":
                                                            if(pair.getValue() instanceof Double){
                                                                Double height = (Double) pair.getValue();
                                                                boatDAO.getBoat_type().setHeight(height.longValue());
                                                            }
                                                            else{
                                                                Long height = (Long) pair.getValue();
                                                                boatDAO.getBoat_type().setHeight(height);
                                                            }
                                                            break;
                                                        case "width":
                                                            if(pair.getValue() instanceof Double){
                                                                Double width = (Double) pair.getValue();
                                                                boatDAO.getBoat_type().setWidth(width.longValue());
                                                            }
                                                            else{
                                                                Long width = (Long) pair.getValue();
                                                                boatDAO.getBoat_type().setWidth(width);
                                                            }
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                                boatListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    break;
                                //Fin de la récupération du BoatType.

                                /*
                                Il s'agit d'une référence vers un document de type 'originHarbor' en BDD. On doit donc faire une nouvelle requête.
                                 */
                                case "origin_harbor":
                                    DocumentReference originHarborReference = (DocumentReference) pair.getValue();
                                    originHarborReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> taskForOriginHarbor) {

                                            if (taskForOriginHarbor.isSuccessful()) {

                                                boatDAO.setOrigin_harbor(new HarborDAO(taskForOriginHarbor.getResult().getId()));

                                                //On itère sur la HashMap reçue depuis la Firebase.
                                                Iterator it = taskForOriginHarbor.getResult().getData().entrySet().iterator();

                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry) it.next();
                                                    switch (String.valueOf(pair.getKey())) {
                                                        case "city":
                                                            boatDAO.getOrigin_harbor().setCity((String) pair.getValue());
                                                            break;
                                                        case "latitude":
                                                            Double lat = (Double) pair.getValue();
                                                            boatDAO.getOrigin_harbor().setLatitude(lat.longValue());
                                                            break;
                                                        case "longitude":
                                                            Double longC = (Double) pair.getValue();
                                                            boatDAO.getOrigin_harbor().setLongitude(longC.longValue());
                                                            break;
                                                        default:
                                                            break;

                                                    }
                                                }
                                                boatListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    break;
                                //Fin de la récupération du port d'origine du bateau concerné.
                                case "captain":
                                    DocumentReference captainReference = (DocumentReference) pair.getValue();
                                    captainReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> taskForBoatType) {
                                            if (taskForBoatType.isSuccessful()) {
                                                DocumentSnapshot result = taskForBoatType.getResult();
                                                boatDAO.setCaptain(new CaptainDAO(result.getId()));
                                                //On itère sur la HashMap reçue depuis la Firebase.
                                                Iterator it = result.getData().entrySet().iterator();
                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry) it.next();
                                                    switch (String.valueOf(pair.getKey())) {
                                                        case "name":
                                                            boatDAO.getCaptain().setName((String) pair.getValue());
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                                boatListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    break;//Fin de la récupération du Captain.
                                /*
                                A partir d'ici, les données ne sont plus des références à d'autres entités de la BDD. On peut donc les stocker directement.
                                 */
                                case "description":
                                    boatDAO.setDescription((String) pair.getValue());
                                    break;
                                case "name":
                                    boatDAO.setName((String) pair.getValue());
                                    break;
                                case "latitude":
                                    if (pair.getValue() instanceof Long) {
                                        Double latitude = Double.valueOf((Long) pair.getValue());
                                        boatDAO.setLatitude(latitude);
                                    } else {
                                        Double latitude = (Double) pair.getValue();
                                        boatDAO.setLatitude(latitude);
                                    }
                                    break;
                                case "longitude":
                                    if (pair.getValue() instanceof Long) {
                                        Double longitude = Double.valueOf((Long) pair.getValue());
                                        boatDAO.setLongitude(longitude);
                                    } else {
                                        Double longitude = (Double) pair.getValue();
                                        boatDAO.setLongitude(longitude);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        boatListAdapter.addBoat(boatDAO);
                        boatListAdapter.notifyDataSetChanged();
                    }
                    progress.dismiss();
                }
            }
        });
    }

    public static void get_all(final ArrayList<BoatDAO> listOfBoats)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(BoatDAO.COLLECTION_REFERENCE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> taskForBoat) {
                if (taskForBoat.isSuccessful()) {
                    /*
                     * On va ici itérer afin de récupérer petit à petit toutes les informations depuis la BDD pour les stocker dans les classes Java que l'on a créé.
                     */
                    for (QueryDocumentSnapshot document : taskForBoat.getResult()) {

                        final BoatDAO boatDAO = new BoatDAO(document.getId());

                        //On itère sur la HashMap reçue depuis la Firebase.
                        Iterator it = document.getData().entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();

                            switch (String.valueOf(pair.getKey())) {

                                /*
                                Il s'agit d'une référence vers un document de type 'boat_type' en BDD. On doit donc faire une nouvelle requête.
                                 */
                                case "boat_type":
                                    DocumentReference boatTypeReference = (DocumentReference) pair.getValue();
                                    boatTypeReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> taskForBoatType) {

                                            if (taskForBoatType.isSuccessful()) {
                                                DocumentSnapshot result = taskForBoatType.getResult();
                                                boatDAO.setBoat_type(new BoatTypeDAO(result.getId()));

                                                //On itère sur la HashMap reçue depuis la Firebase.
                                                Iterator it = result.getData().entrySet().iterator();
                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry) it.next();
                                                    switch (String.valueOf(pair.getKey())) {
                                                        case "name":
                                                            boatDAO.getBoat_type().setName((String) pair.getValue());
                                                            break;
                                                        case "length":
                                                            if(pair.getValue() instanceof Double){
                                                                Double length = (Double) pair.getValue();
                                                                boatDAO.getBoat_type().setLength(length.longValue());
                                                            }
                                                            else{
                                                                Long length = (Long) pair.getValue();
                                                                boatDAO.getBoat_type().setLength(length);
                                                            }
                                                            break;
                                                        case "height":
                                                            if(pair.getValue() instanceof Double){
                                                                Double height = (Double) pair.getValue();
                                                                boatDAO.getBoat_type().setHeight(height.longValue());
                                                            }
                                                            else{
                                                                Long height = (Long) pair.getValue();
                                                                boatDAO.getBoat_type().setHeight(height);
                                                            }
                                                            break;
                                                        case "width":
                                                            if(pair.getValue() instanceof Double){
                                                                Double width = (Double) pair.getValue();
                                                                boatDAO.getBoat_type().setWidth(width.longValue());
                                                            }
                                                            else{
                                                                Long width = (Long) pair.getValue();
                                                                boatDAO.getBoat_type().setWidth(width);
                                                            }
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    break;
                                //Fin de la récupération du BoatType.

                                /*
                                Il s'agit d'une référence vers un document de type 'originHarbor' en BDD. On doit donc faire une nouvelle requête.
                                 */
                                case "origin_harbor":
                                    DocumentReference originHarborReference = (DocumentReference) pair.getValue();
                                    originHarborReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> taskForOriginHarbor) {

                                            if (taskForOriginHarbor.isSuccessful()) {

                                                boatDAO.setOrigin_harbor(new HarborDAO(taskForOriginHarbor.getResult().getId()));

                                                //On itère sur la HashMap reçue depuis la Firebase.
                                                Iterator it = taskForOriginHarbor.getResult().getData().entrySet().iterator();

                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry) it.next();
                                                    switch (String.valueOf(pair.getKey())) {
                                                        case "city":
                                                            boatDAO.getOrigin_harbor().setCity((String) pair.getValue());
                                                            break;
                                                        case "latitude":
                                                            Double lat = (Double) pair.getValue();
                                                            boatDAO.getOrigin_harbor().setLatitude(lat.longValue());
                                                            break;
                                                        case "longitude":
                                                            Double longC = (Double) pair.getValue();
                                                            boatDAO.getOrigin_harbor().setLongitude(longC.longValue());
                                                        default:
                                                            break;

                                                    }
                                                }
                                            }
                                        }
                                    });
                                    break;
                                //Fin de la récupération du port d'origine du bateau concerné.
                                case "captain":
                                    DocumentReference captainReference = (DocumentReference) pair.getValue();
                                    captainReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> taskForBoatType) {
                                            if (taskForBoatType.isSuccessful()) {
                                                DocumentSnapshot result = taskForBoatType.getResult();
                                                boatDAO.setCaptain(new CaptainDAO(result.getId()));
                                                //On itère sur la HashMap reçue depuis la Firebase.
                                                Iterator it = result.getData().entrySet().iterator();
                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry) it.next();
                                                    switch (String.valueOf(pair.getKey())) {
                                                        case "name":
                                                            boatDAO.getCaptain().setName((String) pair.getValue());
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    break;//Fin de la récupération du Captain.
                                /*
                                A partir d'ici, les données ne sont plus des références à d'autres entités de la BDD. On peut donc les stocker directement.
                                 */
                                case "description":
                                    boatDAO.setDescription((String) pair.getValue());
                                    break;
                                case "name":
                                    boatDAO.setName((String) pair.getValue());
                                    break;
                                case "latitude":
                                    if (pair.getValue() instanceof Long) {
                                        Double latitude = Double.valueOf((Long) pair.getValue());
                                        boatDAO.setLatitude(latitude);
                                    } else {
                                        Double latitude = (Double) pair.getValue();
                                        boatDAO.setLatitude(latitude);
                                    }
                                    break;
                                case "longitude":
                                    if (pair.getValue() instanceof Long) {
                                        Double longitude = Double.valueOf((Long) pair.getValue());
                                        boatDAO.setLongitude(longitude);
                                    } else {
                                        Double longitude = (Double) pair.getValue();
                                        boatDAO.setLongitude(longitude);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        listOfBoats.add(boatDAO);
                    }
                }
            }
        });
    }


    /**
     *
     * @return Float, la distance en mètres du bateau à son port d'origine.
     */
    public Float get_distance_from_origin_harbor(){
        float[] result = new float[1];
        Location.distanceBetween(this.getLatitude(),this.getLongitude(),this.origin_harbor.getLatitude(),this.origin_harbor.getLongitude(),result);
        return result[0];
    }

    /**
     * @return le nombre de mètres cubes restant pour le chargement du bateau.
     */
    public long getLeftingStorage(){
        long totalFreeVolume = getBoat_type().getTotalVolume();
        for (ContainerDAO aContainer: containers) {
            totalFreeVolume -= aContainer.getVolume();
        }
        return totalFreeVolume;
    }

    /**
     * @param containerDAO, un container quelquoncque que l'on veut ajouter sur ce bateau.
     * @return la possibilité de l'ajouter (si il peut y rentrer).
     */
    public Boolean isPossibleToAddContainer(ContainerDAO containerDAO){
        return getLeftingStorage() >= containerDAO.getVolume();
    }

    /**
     * Ajoute un container sur le bateau, uniquement si c'est possible.
     * @param containerDAO, le container que l'on souhaite ajouter.
     */
    public void addContainer(ContainerDAO containerDAO){
        if(isContainerAlreadyOnBoard(containerDAO)){
            return;
        }
        if(isPossibleToAddContainer(containerDAO)){
            this.getContainers().add(containerDAO);
        }
    }

    /**
     * Regarde si un container est déjà présent sur le bateau.
     * @param containerDAO, le container recherché.
     * @return si le container est présent ou non.
     */
    public Boolean isContainerAlreadyOnBoard(ContainerDAO containerDAO){
        for(ContainerDAO aContainer : getContainers()){
            if (aContainer.getID().equals(containerDAO.getID())){
                return true;
            }
        }
        return false;
    }

    /** Getters et Setters */
    public ArrayList<ContainerDAO> getContainers() {return containers;}
    public void setContainers(ArrayList<ContainerDAO> containers) {this.containers = containers; }
    public CaptainDAO getCaptain() {return captain;}
    public void setCaptain(CaptainDAO captain) {this.captain = captain;}
    public BoatTypeDAO getBoat_type() { return boat_type;}
    public void setBoat_type(BoatTypeDAO boat_type) { this.boat_type = boat_type;}
    public HarborDAO getOrigin_harbor() {return origin_harbor;}
    public void setOrigin_harbor(HarborDAO origin_harbor) {this.origin_harbor = origin_harbor;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public void setPosition(double latitude,Double longitude) { this.latitude = latitude;this.longitude = longitude;}
    public Double getLatitude() {return latitude;}
    public void setLatitude(Double latitude) {this.latitude = latitude;}
    public Double getLongitude() {return longitude;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}
}
