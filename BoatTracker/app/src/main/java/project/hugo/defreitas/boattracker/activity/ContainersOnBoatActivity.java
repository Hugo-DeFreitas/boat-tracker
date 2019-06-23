package project.hugo.defreitas.boattracker.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import project.hugo.defreitas.boattracker.DAO.BoatDAO;
import project.hugo.defreitas.boattracker.R;
import project.hugo.defreitas.boattracker.classes.ContainerListAdapter;

public class ContainersOnBoatActivity extends AppCompatActivity {
    private BoatDAO _current_boat;

    private ListView _containers_list_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_containers_on_boat);

        this._current_boat = (BoatDAO) getIntent().getSerializableExtra("current_boat");

        //Récupération de la ListView
        this._containers_list_view = (ListView) findViewById (R.id.containers_of_boat_list_view);;
        _containers_list_view.setAdapter(new ContainerListAdapter(this.getApplicationContext(),_current_boat));
    }
}
