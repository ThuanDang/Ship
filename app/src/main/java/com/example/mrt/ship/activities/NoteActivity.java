package com.example.mrt.ship.activities;



import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


import com.example.mrt.ship.R;
import com.example.mrt.ship.fragments.CreateNoteFragment;
import com.example.mrt.ship.fragments.NotesFragment;
import com.example.mrt.ship.models.Note;



public class NoteActivity extends AppCompatActivity implements
        NotesFragment.OnFragmentInteractionListener,
        CreateNoteFragment.OnCreateNoteFragmentListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Ghi chú");
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.container, new NotesFragment());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();

    }

    @Override
    public void startCreateNoteFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.zoom_in, R.anim.exit, R.anim.enter, R.anim.zoom_out);
        ft.replace(R.id.container, new CreateNoteFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void startCreateNoteFragment(Note note) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.pop_enter, R.anim.pop_exit);
        ft.replace(R.id.container, CreateNoteFragment.newInstance(note));
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void startListNoteFragment() {
        getSupportFragmentManager().popBackStack();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
