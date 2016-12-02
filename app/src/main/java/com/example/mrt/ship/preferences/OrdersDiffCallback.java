package com.example.mrt.ship.preferences;

import android.support.v7.util.DiffUtil;

import com.example.mrt.ship.models.Order;

import java.util.List;
import java.util.Objects;

/**
 * Created by mrt on 28/10/2016.
 */

public class OrdersDiffCallback extends DiffUtil.Callback{
    private List<Order> oldList;
    private List<Order> newList;

    public OrdersDiffCallback(List<Order> oldList, List<Order> newList){
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
        Order oldOrder = oldList.get(oldItemPosition);
        Order newOrder = newList.get(newItemPosition);
        return Objects.equals(oldOrder.getName(), newOrder.getName())
                && Objects.equals(oldOrder.getWare_house().getAddress(),
                newOrder.getRecipient().getAddress())
                && oldOrder.getShip_cost() == newOrder.getShip_cost();
    }
}
