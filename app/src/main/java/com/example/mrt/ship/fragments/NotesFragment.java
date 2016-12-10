package com.example.mrt.ship.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mrt.ship.adapters.NotesAdapter;
import com.example.mrt.ship.data.NotesDatabaseHelper;
import com.example.mrt.ship.models.Note;
import com.example.mrt.ship.preferences.SpacesItemDecoration;

import java.util.List;


public class NotesFragment extends Fragment {

    List<Note> notes;
    private NotesAdapter adapter;
    private Handler handler = new Handler();
    private TextView list_empty;

    private OnFragmentInteractionListener mListener;    // Communication Interface


    public NotesFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new NotesAdapter(getContext());
        notes = NotesDatabaseHelper.getInstance(getContext()).getAllNotes();
        adapter.setData(notes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(com.example.mrt.ship.R.layout.fragment_note_list, container, false);

        list_empty = (TextView)view.findViewById(com.example.mrt.ship.R.id.note_list_empty);
        list_empty.setVisibility(View.GONE);
        RecyclerView list_note = (RecyclerView)view.findViewById(com.example.mrt.ship.R.id.list_note);

        // Config List
        list_note.setAdapter(adapter);
        list_note.setLayoutManager(new LinearLayoutManager(getContext()));
        list_note.addItemDecoration(new SpacesItemDecoration(16));

        adapter.setOnOptionMenuListener(new NotesAdapter.OnOptionMenuClickListener() {
            @Override
            public void onDeleteClick(int position) {
                NotesDatabaseHelper.getInstance(getContext())
                        .deleteNote(notes.get(position).getId());
                notes.remove(position);
                adapter.notifyItemRemoved(position);
                Log.d("test", "onDeleteClick: " + position);
            }

            @Override
            public void onUpdateClick(int position) {
                if(mListener != null){
                    mListener.startCreateNoteFragment(notes.get(position));
                }
            }
        });



        // FloatAction event
        FloatingActionButton fab = (FloatingActionButton)view.findViewById(com.example.mrt.ship.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.startCreateNoteFragment();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notes = NotesDatabaseHelper.getInstance(getContext()).getAllNotes();
                adapter.swapItems(notes);
                // Check if List Empty
                if(notes.isEmpty()){
                    list_empty.setVisibility(View.VISIBLE);
                }else{
                    list_empty.setVisibility(View.GONE);
                }
            }
        }, 450);
    }

    public interface OnFragmentInteractionListener {
        void startCreateNoteFragment();
        void startCreateNoteFragment(Note note);
    }
}
