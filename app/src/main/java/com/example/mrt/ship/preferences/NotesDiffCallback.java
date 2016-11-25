package com.example.mrt.ship.preferences;

import android.support.v7.util.DiffUtil;

import com.example.mrt.ship.models.Note;

import java.util.List;
import java.util.Objects;

/**
 * Created by mrt on 17/10/2016.
 */

public class NotesDiffCallback extends DiffUtil.Callback {
    private List<Note> oldList, newList;

    public NotesDiffCallback(List<Note> oldList, List<Note> newList){
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Note oldNote = oldList.get(oldItemPosition);
        Note newNote = newList.get(newItemPosition);
        return Objects.equals(oldNote.getHeader(), newNote.getHeader())
                && Objects.equals(oldNote.getContent(), newNote.getContent());
    }
}
