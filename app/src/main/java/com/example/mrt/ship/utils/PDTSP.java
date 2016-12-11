package com.example.mrt.ship.utils;

import android.util.Log;

import com.example.mrt.ship.models.maps.DistanceMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mrt on 30/11/2016.
 */

public class PDTSP {
    private  List<String> P;
    private  List<String> H;
    private  List<String> S;
    private List<String> temp;
    private DistanceMatrix c;
    private String TAG = "test";

    public PDTSP(List<String> P, List<String> D, DistanceMatrix c){
        this.P = P;
        this.c = c;
        H = new ArrayList<>();
        S = new ArrayList<>();
        temp = new ArrayList<>();
        S.addAll(P);
        S.addAll(D);
        H.add("p0");

    }

    public List<String> solve(){
        // step 0
        String p = getPMax();

        H.add(p);
        S.remove(p);
        print(S);

       do{
            // step 1
            String k = getVertexMax(getMinInSH());
           //Log.d(TAG, "solve: " + k);
            insertK(k);
       }while(S.size() != 0);

        return H;
    }

    private String getPMax(){
        String result = "";
        double max = 0.0;
        for(String p:P){
            int i = Integer.valueOf(p.substring(1));
            if(c.getRows().get(0).getElements().get(i).getDistance().getValue() > max){
                max = c.getRows().get(0).getElements().get(i).getDistance().getValue();
                result = p;
            }
        }
        return result;
    }

    private HashMap<String, Double> getMinInSH(){
        HashMap<String, Double> result = new HashMap<>();
        for(String s: S){
            double min = 100000;
            for(String h: H){
                int i = Integer.valueOf(s.substring(1));
                int j = Integer.valueOf(h.substring(1));
                if(c.getRows().get(i).getElements().get(j).getDistance().getValue() < min){
                    min = c.getRows().get(i).getElements().get(j).getDistance().getValue();
                    result.put(s, min);
                }
            }
        }
        return result;
    }

    private String getVertexMax(HashMap<String, Double> min){
        String result = "";
        double max = 0.0;
        for(String k: min.keySet()){
            double c = min.get(k);
            if(c > max){
                max = c;
                result = k;
            }
        }
        return result;
    }

    private void insertK(String vertexK){
        int k = Integer.valueOf(vertexK.substring(1));
        int position = 0;
        double min = 100000.0;
        if(vertexK.substring(0,1).equals("d")){
            int index_of_p = H.indexOf("p" + (k + 1));
            //Log.d(TAG, "insertK: " + index_of_p);
            if(index_of_p == -1){
                S.remove(vertexK);
                temp.add(vertexK);
                return;
            }else {
                for(int index = index_of_p; index < H.size(); index++){
                    if(index < H.size() - 1){
                        int i = Integer.valueOf(H.get(index).substring(1));
                        int j = Integer.valueOf(H.get(index + 1).substring(1));
                        double increase = c.getRows().get(i).getElements().get(k).getDistance().getValue()
                                + c.getRows().get(k).getElements().get(j).getDistance().getValue()
                                - c.getRows().get(k).getElements().get(j).getDistance().getValue();
                        if(increase < min){
                            min = increase;
                            position = index + 1;
                        }
                    }else {
                        int i = Integer.valueOf(H.get(index).substring(1));
                        double increase = c.getRows().get(i).getElements().get(k).getDistance().getValue();
                        if(increase < min){
                            min = increase;
                            position = index + 1;
                        }
                    }


                    //Log.d(TAG, "insertK: " + "increase: " + increase + " postition:" + position);
                }
            }
        }else {
            for(int index = 0; index < H.size(); index++){
                if(index < H.size() - 1){
                    int i = Integer.valueOf(H.get(index).substring(1));
                    int j = Integer.valueOf(H.get(index + 1).substring(1));
                    double increase = c.getRows().get(i).getElements().get(k).getDistance().getValue()
                            + c.getRows().get(k).getElements().get(j).getDistance().getValue()
                            - c.getRows().get(k).getElements().get(j).getDistance().getValue();
                    if(increase < min){
                        min = increase;
                        position = index + 1;
                    }
                }else {
                    int i = Integer.valueOf(H.get(index).substring(1));
                    double increase = c.getRows().get(i).getElements().get(k).getDistance().getValue();
                    if(increase < min){
                        min = increase;
                        position = index + 1;
                    }
                }


                //Log.d(TAG, "insertK: " + "increase: " + increase + " postition:" + position);
            }
        }

        if(position != 0){
            H.add(position, vertexK);
            S.remove(vertexK);
        }

        //Log.d(TAG, "insertK: S.size: " + S.size());
    }

    public void print(List<String> a){
        for (String s: a){
            Log.d(TAG, "print: " + s);
        }
    }
}
