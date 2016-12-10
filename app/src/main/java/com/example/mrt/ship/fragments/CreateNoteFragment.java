package com.example.mrt.ship.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mrt.ship.data.NotesDatabaseHelper;
import com.example.mrt.ship.models.Note;

/**
 * Created by mrt on 17/10/2016.
 */

public class CreateNoteFragment extends Fragment {
    public static final String ARG = "args";
    private Bundle args;
    private Note note;

    private OnCreateNoteFragmentListener mListener;

    public static CreateNoteFragment newInstance(Note note){
       Bundle args = new Bundle();
        args.putSerializable(ARG, note);
        CreateNoteFragment fragment = new CreateNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(com.example.mrt.ship.R.layout.fragment_create_note, container, false);
        final EditText header = (EditText) view.findViewById(com.example.mrt.ship.R.id.cr_note_header);
        final EditText content = (EditText)view.findViewById(com.example.mrt.ship.R.id.cr_note_content);
        if(args != null){
            note = (Note)args.getSerializable(ARG);
            if(note != null){
                header.setText(note.getHeader(), TextView.BufferType.EDITABLE);
                content.setText(note.getContent(), TextView.BufferType.EDITABLE);
            }
        }

        // Save note
        Button save = (Button)view.findViewById(com.example.mrt.ship.R.id.btn_save_note);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(args == null){
                    // Delete
                    Note new_note = getNoteFromInput(header, content);
                    NotesDatabaseHelper.getInstance(getContext()).addNote(new_note);
                    if(mListener != null){
                        mListener.startListNoteFragment();
                    }
                }
                else{
                    // Update
                    note.setHeader(header.getText().toString());
                    note.setContent(content.getText().toString());
                    NotesDatabaseHelper.getInstance(getContext()).updateNote(note);
                    if(mListener != null){
                        mListener.startListNoteFragment();
                    }
                }
            }
        });
        return view;
    }

    public Note getNoteFromInput(EditText header, EditText content){
        Note new_note = new Note();
        new_note.setHeader(header.getText().toString());
        new_note.setContent(content.getText().toString());
        return new_note;
    }

    public interface OnCreateNoteFragmentListener{
        void startListNoteFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnCreateNoteFragmentListener){
            mListener = (OnCreateNoteFragmentListener)context;
        }else{
            throw new RuntimeException(context.toString() +
                    " must be implement OnCreateNoteFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
