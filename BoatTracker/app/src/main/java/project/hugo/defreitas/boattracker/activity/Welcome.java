package project.hugo.defreitas.boattracker.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.ArrayList;

import project.hugo.defreitas.boattracker.DAO.BoatDAO;
import project.hugo.defreitas.boattracker.DAO.ContainerDAO;
import project.hugo.defreitas.boattracker.DAO.DAO;
import project.hugo.defreitas.boattracker.R;
import project.hugo.defreitas.boattracker.classes.BoatListAdapter;


/**
 * Classe de l'accueil de l'application
 */
public class Welcome extends AppCompatActivity {
    /**
     * Données liées à la Vue
     */
    private ListView _boat_list;
    private SignInButton _sign_in_button; // Boutton d'authentification Google.
    private Button _sign_out_button;
    private MaterialFancyButton _message_for_not_connected_user;
    private MaterialFancyButton _create_new_boat;

    /**
     * Données liées à l'authentification via Google
     */
    public static int CODE_USER_SIGNED_IN_WITH_GOOGLE = 100;
    private GoogleSignInClient _google_sign_in_client;
    private GoogleSignInAccount _user_connected_with_google;

    /** Données utiles */
    ArrayList<BoatDAO> _all_boats_from_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context saveContext = getApplicationContext();
        setContentView(R.layout.activity_welcome);

        configureGoogleAuth();

        //Récupération du boutton Google
        _sign_in_button = findViewById(R.id.sign_in_button);
        _sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = _google_sign_in_client.getSignInIntent();
                startActivityForResult(signInIntent, CODE_USER_SIGNED_IN_WITH_GOOGLE);
            }

        });

        _create_new_boat = findViewById(R.id.create_new_boat);
        _create_new_boat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),CreateBoatActivity.class);
                startActivity(i);
            }
        });

        //Récupération de la ListView
        this._boat_list = findViewById(R.id.boats_list_view);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        //On change l'interface selon si on est authentifié via Google ou non.
        updateUI(isSignedIn());

        // Récupération des données depuis la FireBase.
        _all_boats_from_db = new ArrayList<BoatDAO>();
        ArrayList<ContainerDAO> allContainers = new ArrayList<ContainerDAO>();
        final BoatListAdapter boatListAdapter = new BoatListAdapter(saveContext, _all_boats_from_db, allContainers);
        _boat_list.setAdapter(boatListAdapter);
        //On vient charger toutes les données nécessaires depuis la Firebase.
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Récupération des bateaux depuis Firebase");
        progress.setMessage("L'opération peut prendre quelques secondes...");
        progress.setCancelable(false);
        progress.show();
        DAO.loadFirebaseData(db,saveContext,_boat_list,progress);
    }

    /**
     * Résultat de l'activité, notamment pour la connexion via Google
     * @param requestCode, Code de la requête pour laquelle l'activité fille a été appelée.
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_USER_SIGNED_IN_WITH_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * On regarde si l'authentification a bien eu lieu, pour récupérer des infos sur l'utilisateur et les afficher sur la vue.
     * En cas d'échec, on met aussi à jour la vue.
     * @param completedTask, le retour d'une tâche d'authentification.
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Boolean _is_user_connected = false;
        try {
            //L'authentification est un succès.
            _user_connected_with_google = completedTask.getResult(ApiException.class);
            System.out.println("L'utilisateur "+_user_connected_with_google.getGivenName()+"s'est bien authentifié.");
            _is_user_connected = true;

        } catch (ApiException e) {
            System.out.println("L'utilisateur n'a pas pu s'authentifier.");
            System.err.println(e.getMessage());
        }
        updateUI(_is_user_connected);
    }

    /**
     * MAJ de l'interface en fonction de la connexion (ou non) de l'utilisateur.
     * @param isUserConnected, est-ce que l'utilisateur est connecté ?
     */
    private void updateUI(Boolean isUserConnected){
        //On bind les vues.
        _message_for_not_connected_user = findViewById(R.id.message_for_not_connected_user);
        _sign_out_button = findViewById(R.id.sign_out_button);
        if(isUserConnected){
            //On change le message d'arrivée
            TextView welcomeMessage = findViewById(R.id.welcome_message);
            welcomeMessage.setText(String.format("Bienvenue %s!", _user_connected_with_google.getGivenName()));
            //On affiche les bateaux.
            _boat_list.setVisibility(View.VISIBLE);
            // On cache le boutton de sign in avec Google
            _sign_in_button.setVisibility(View.GONE);
            //On cache l'invitation à la connexion
            _message_for_not_connected_user.setVisibility(View.GONE);
            // On affiche la possibilité de se déconnecter.
            _sign_out_button.setVisibility(View.VISIBLE);
            _sign_out_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _google_sign_in_client.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(false);
                        }
                    });
                }
            });
        }
        else{
            //On change le message d'arrivée
            TextView welcomeMessage = findViewById(R.id.welcome_message);
            welcomeMessage.setText("Bienvenue sur BoatTracker!");
            //On indique à l'utilisateur qu'il doit se connecter.
            _message_for_not_connected_user.setVisibility(View.VISIBLE);
            // On affiche le boutton de sign in avec Google
            _sign_in_button.setVisibility(View.VISIBLE);
            //On cache les bateaux.
            _boat_list.setVisibility(View.GONE);
            // On cache la possibilité de se déconnecter.
            _sign_out_button = findViewById(R.id.sign_out_button);
            _sign_out_button.setVisibility(View.GONE);
        }

    }

    /**
     * Fonction utile, qui permet de vérifier la connexion de l'utilisateur, et de récupérer notamment
     * les infos de l'utilisateur si il a déjà été connecté (utile pour les retours sur la cette vue lors du rechargement
     * des données Firebase, sans à avoir à se reconnecter).
     * @return isSignedIn, qui permet de savoir si l'utilisateur est connecté ou non.
     */
    private Boolean isSignedIn() {
        Boolean isSignedIn = GoogleSignIn.getLastSignedInAccount(getApplicationContext()) != null;
        if(isSignedIn){
            _user_connected_with_google = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        }
        return isSignedIn;
    }

    /**
     * Configuration de l'authentification via Google
     */
    private void configureGoogleAuth(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        _google_sign_in_client = GoogleSignIn.getClient(this, gso);
    }
}
