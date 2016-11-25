package com.example.mrt.ship.sync;

import com.example.mrt.ship.models.Order;

import java.util.List;

/**
 * Created by mrt on 28/10/2016.
 */

public class GetJson {
    private int count;
    private String next;
    private String previous;
    private List<Order> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<Order> getResults() {
        return results;
    }

    public void setResults(List<Order> results) {
        this.results = results;
    }
}
