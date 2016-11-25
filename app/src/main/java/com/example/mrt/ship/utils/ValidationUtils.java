package com.example.mrt.ship.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by mrt on 26/10/2016.
 */

public class ValidationUtils {
   public static void setListenFieldsEmpty(final Button submit, final TextView[] fields,
                                           final int bgOff, final int bgOn){

      submit.setEnabled(false);
      if(bgOff != 0){
         submit.setBackgroundResource(bgOff);
      }
      final int n = fields.length;
      for(int i = 0; i < n; i++){
         fields[i].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               String[] texts = new String[n];
               for(int i = 0; i < n; i++){
                  texts[i] = fields[i].getText().toString();
                  if (texts[i].isEmpty()){
                     if(submit.isEnabled()){
                        submit.setEnabled(false);
                        if(bgOff != 0){
                           submit.setBackgroundResource(bgOff);
                        }
                     }
                     return;
                  }
               }

               if(!submit.isEnabled()) {
                  submit.setEnabled(true);
                  if (bgOn != 0) {
                     submit.setBackgroundResource(bgOn);
                  }
               }
            }
         });
      }
   }
}
