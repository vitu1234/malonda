package com.example.malonda.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Toast;

import com.example.malonda.R;
import com.mrntlu.toastie.Toastie;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyProgressDialog {

    Context context;
    public ProgressDialog progressDialog;
    public SweetAlertDialog pDialog;
    public MyProgressDialog(Context context) {
        this.context = context;
    }


    public void showDialog(String msg){
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setTitle(msg);
//        progressDialog.setCancelable(false);
////        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#233067"));
        pDialog.getProgressHelper().spin();
        pDialog.setTitleText(msg);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    public void closeDialog(){
//        if(progressDialog != null){
//            progressDialog.dismiss();
//        }
        if (pDialog != null){
            pDialog.dismissWithAnimation();
        }
    }

    public void showDangerAlert(String msg){
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#FF3D00"));
        pDialog.setTitleText(msg);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void showSuccessAlert(String msg){
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#FC00D16B"));
        pDialog.setTitleText(msg);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public  void showSuccessToast(String message){
        Toastie.allCustom(context)
                .setTypeFace(Typeface.DEFAULT_BOLD)
                .setTextSize(16)
                .setCardRadius(25)
                .setCardElevation(10)
                .setIcon(R.drawable.ic_check_circle_black_24dp)
                .setCardBackgroundColor(R.color.teal_200)
                .setMessage(message)
                .setGravity(Gravity.BOTTOM, 5, 5)
                .createToast(Toast.LENGTH_LONG)
                .show();
    }

    public  void showErrorToast(String message){
        Toastie.allCustom(context)
                .setTypeFace(Typeface.DEFAULT_BOLD)
                .setTextSize(16)
                .setCardRadius(25)
                .setCardElevation(10)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setCardBackgroundColor(R.color.red)
                .setMessage(message)
                .setGravity(Gravity.BOTTOM, 5, 5)
                .createToast(Toast.LENGTH_LONG)
                .show();
    }

}
