package com.example.mrt.ship.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.example.mrt.ship.R;

/**
 * Created by mrt on 09/12/2016.
 */

public class DialogUtil {

    public interface Callback{
        void onPositiveButtonClick();
    }

    public static void conflictReceiveOrder(Context context, final Callback callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Không thể nhận đơn hàng");
        builder.setMessage("Có vẻ như ai đó đã nhận đơn hàng này trước bạn," +
                " hãy đảm bảo danh sách đơn hàng của bạn là mới nhất");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                callback.onPositiveButtonClick();
            }
        });
        builder.show();
    }


    public static void connectError(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Lỗi kết nối");
        builder.setMessage("Không thể kết nối tới máy chủ, vui lòng kiểm tra kết " +
                "nối internet của bạn.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                context.getResources().getColor(R.color.colorAccent));

    }


    public static void receiveSuccess(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Nhận đơn hàng thành công!");
        builder.setIcon(R.drawable.ic_checked);
        builder.setMessage("");
        builder.show();
    }


    public static void notEnoughMoney(Context context, @Nullable final Callback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Bạn không đủ tiền!");
        builder.setIcon(R.drawable.ic_crying);

        builder.setCancelable(false);

        if (callback != null) {
            builder.setMessage("Rất tiếc bạn không đủ tiền để nhận đơn hàng này, " +
                    "bạn có muốn hệ thống tìm những đơn hàng phù hợp với tài khoản của mình không?");
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    callback.onPositiveButtonClick();
                    dialogInterface.cancel();
                }
            });
            builder.setNegativeButton("Không, cảm ơn", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        } else {
            builder.setMessage("Rất tiếc tài khoản của bạn không đủ để nhận đơn hàng này");
        }

        builder.show();
    }


    public static void confirmPasswordFail(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Vui lòng xác nhận lại mật khẩu!");
        builder.setIcon(R.drawable.ic_mark);
        builder.setMessage("");
        builder.show();
    }


    public static void conflictSignUp(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Đăng ký không thành công");
        builder.setMessage("Tài khoản đã được sử dụng. Xin vui lòng nhập một tên tài khoản khác.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setIcon(R.drawable.ic_mark);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                context.getResources().getColor(R.color.colorPrimary));
    }


    public static void signUpSuccess(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Đăng ký thành công");
        builder.setMessage("Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi. " +
                "Đăng nhập ngay để dễ dàng kiếm thêm thu nhập.");
        builder.setIcon(R.drawable.ic_checked);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void cancelOrderSuccess(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Hủy đơn hàng thành công");
        builder.setMessage("");
        builder.setIcon(R.drawable.ic_checked);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
