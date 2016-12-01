package com.example.mrt.ship.networks;

import com.example.mrt.ship.models.Order;

import java.util.List;

/**
 * Created by mrt on 28/10/2016.
 */

public class GetJson {
    private int total;
    private String next_page_url;
    private List<Order> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(String next_page_url) {
        this.next_page_url = next_page_url;
    }

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
}


