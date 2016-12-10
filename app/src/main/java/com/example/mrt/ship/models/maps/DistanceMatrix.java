package com.example.mrt.ship.models.maps;

import java.util.List;

/**
 * Created by mrt on 03/12/2016.
 */

public class DistanceMatrix {
    private String status;
    private List<Row> rows;

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
