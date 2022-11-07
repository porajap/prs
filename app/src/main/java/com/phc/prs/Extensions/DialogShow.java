package com.phc.prs.Extensions;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.phc.prs.R;

public class DialogShow {

    public DialogShow(final Context context, ViewGroup view, String detail) {

        ViewGroup viewGroup = view.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_show, viewGroup, false);
        TextView _text = (TextView)dialogView.findViewById(R.id.textview_message);
        _text.setText(detail);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context , R.style.AlertDialogStyle);

        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //close dialog when click OK
        Button btn_click= (Button) dialogView.findViewById(R.id.btn_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}
