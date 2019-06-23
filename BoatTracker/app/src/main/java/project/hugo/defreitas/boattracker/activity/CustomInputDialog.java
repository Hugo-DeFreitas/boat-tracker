package project.hugo.defreitas.boattracker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import project.hugo.defreitas.boattracker.R;

/**
 * Classe qui permet de récupérer un input simple depuis une activité parente.
 * C'est utile pour changer facilement les valeurs des bateaux sur Firebase.
 */
public class CustomInputDialog extends AlertDialog.Builder {

    public interface InputSenderDialogListener{
        public abstract void onOK(String number);
        public abstract void onCancel(String number);
    }

    //Input que l'utilisateur modifie.
    private EditText mInputEdit;

    //Données membres issues de la vue : comme ça on peut indiquer côté vue les titres à mettre, etc.
    private TextView mTitle;
    private TextView mSubtitle;

    //Le résultat entré par l'utilisateur
    private String resultFromActivity;

    public CustomInputDialog(Activity activity, final InputSenderDialogListener listener, String title, String subtitle) {
        super(new ContextThemeWrapper(activity, R.style.AppTheme));

        @SuppressLint("InflateParams")
        View dialogLayout = LayoutInflater.from(activity).inflate(R.layout.custom_dialog, null);
        setView(dialogLayout);

        //On fait les binds sur les vues
        mInputEdit = dialogLayout.findViewById(R.id.input_edit);

        mTitle = dialogLayout.findViewById(R.id.title_of_custom_input);
        mTitle.setText(title);
        mSubtitle = dialogLayout.findViewById(R.id.subtitle_of_custom_input);
        mSubtitle.setText(subtitle);

        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if( listener != null )
                    listener.onOK(String.valueOf(mInputEdit.getText()));

            }
        });

        setNegativeButton("Retour", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if( listener != null )
                    listener.onCancel(String.valueOf(mInputEdit.getText()));
            }
        });
    }

    public CustomInputDialog setCurrentValueOfInput(String text){
        mInputEdit.setText( text );
        return this;
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = super.show();
        Window window = dialog.getWindow();
        if( window != null )
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }

    public String getResult() {
        return this.mInputEdit.getText().toString();
    }
}