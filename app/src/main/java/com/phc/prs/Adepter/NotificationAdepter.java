package com.phc.prs.Adepter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.phc.prs.Models.NotificationModel;
import com.phc.prs.NotificationsActivity;
import com.phc.prs.R;


import java.util.ArrayList;

public class NotificationAdepter extends RecyclerView.Adapter<NotificationAdepter.ViewHolder> {

    private ArrayList<NotificationModel> dataNotification;
    private Context context;
    private String[] dayOfWeek;

    private String currentLanguage = "en";


    public NotificationAdepter(ArrayList<NotificationModel> dataNotification, Context context, String currentLanguage) {
        this.dataNotification = dataNotification;
        this.context = context;

        dayOfWeek = new String[]{
                context.getResources().getString(R.string.monday),
                context.getResources().getString(R.string.tuesday),
                context.getResources().getString(R.string.wednesday),
                context.getResources().getString(R.string.thursday),
                context.getResources().getString(R.string.friday),
                context.getResources().getString(R.string.saturday),
                context.getResources().getString(R.string.sunday),
        };

        this.currentLanguage = currentLanguage;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View _view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_notification, parent, false);
        return new ViewHolder(_view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String _rowId = dataNotification.get(position).getID();
        final String _status = dataNotification.get(position).getRepairStatus();
        String _machine = dataNotification.get(position).getDispenserID();

        String _dateReceive = dataNotification.get(position).getDateReceive();
        String _timeReceiveTH = dataNotification.get(position).getTimeReceiveTh();
        String _timeReceiveEng = dataNotification.get(position).getTimeReceiveEng();

        String _dateRepair = dataNotification.get(position).getDateRepair();
        String _timeRepairTH = dataNotification.get(position).getTimeRepairTh();
        String _timeRepairEng = dataNotification.get(position).getTimeRepairEng();

        int _dayDiff = Integer.parseInt(dataNotification.get(position).getDayDiff());
        int _timeDiff = Integer.parseInt(dataNotification.get(position).getTimeDiff());
        int _dayOfWeek = Integer.parseInt(dataNotification.get(position).getDayOfWeek());

        //background read
        int _readOnSelection = Integer.parseInt(dataNotification.get(position).getNotification_Read());
        
        /*status = receive And Read = 0 or
        status = repair And Read = 1 (when status = repair And Read = 1 because selected on status = receive) or
        Read = 0 (check when first open activity)*/
        if ((_status.equals("1") && _readOnSelection == 0) || (_status.equals("2") && _readOnSelection == 1) || _readOnSelection == 0) {
            holder.backgroundNotification.setBackgroundResource(R.drawable.notification_new_selection_color);
            holder.textDateTimeTitle.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            holder.textDateTime.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
        } else {
            holder.textDateTimeTitle.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
            holder.textDateTime.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
            holder.backgroundNotification.setBackgroundResource(R.drawable.notification_read_selection_color);
        }

        //action to this
        holder.backgroundNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NotificationsActivity) context).actionToThisStatusOnSelection(_rowId, _status);
            }
        });

        //update count notification when scroll
        int _scrollRead = Integer.parseInt(dataNotification.get(position).getNotification_Read_Scroll());
        if ((_status.equals("1") && _scrollRead == 0) || (_status.equals("2") && _scrollRead == 1) || _scrollRead == 0) {
            holder.imgBell.setImageResource(R.drawable.ic_notification_b);
            ((NotificationsActivity) context).scrollNotification(_rowId, _status);
        } else {
            holder.imgBell.setImageResource(R.drawable.ic_notification);
        }

        //title machine id
        holder.textMachineTitle.setText(context.getString(R.string.check_status_text_machine) + " : " + _machine);

        //title date time
        String _date = _dateReceive;
        String _time = _timeReceiveEng;

        if(currentLanguage.equals("th")){
            _time = _timeReceiveTH + " น.";
        }

        if (_status.equals("2")) {
            _date = _dateRepair;
            _time = _timeRepairEng;

            if(currentLanguage.equals("th")){
                _time = _timeRepairTH + " น.";;
            }
        }

        String _last = "";
        if (_dayDiff == 0 && _timeDiff == 0) {
            _last = _time;
        } else if (_dayDiff == 0) {
            _last = _timeDiff + " " + context.getString(R.string.hour_ago);
        } else if (_dayDiff == 1) {
            _last = context.getString(R.string.yesterday) + _time;
        } else {
            _last = dayOfWeek[_dayOfWeek] + " " + _time;
        }
        holder.textDateTimeTitle.setText(_last);

        //text label
        if (_status.equals("1")) {
            holder.textLabel.setText(R.string.check_status_text_receive);
        } else {
            holder.textLabel.setText(R.string.check_status_text_repair);
        }

        holder.textDateTime.setText(context.getString(R.string.date) + " " + _date + " " + context.getString(R.string.time) + " " + _time);


        //close or delete
        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NotificationsActivity) context).deleteNotification(_rowId, _status);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataNotification.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textMachineTitle, textDateTimeTitle;
        ImageView imgBell, imgClose;
        TextView textLabel, textDateTime;

        LinearLayoutCompat backgroundNotification;

        public ViewHolder(@NonNull View v) {
            super(v);

            textMachineTitle = v.findViewById(R.id.textMachineTitle);
            textDateTimeTitle = v.findViewById(R.id.textDateTimeTitle);

            imgBell = v.findViewById(R.id.imgBell);
            imgClose = v.findViewById(R.id.imgClose);

            textLabel = v.findViewById(R.id.textLabel);
            textDateTime = v.findViewById(R.id.textDateTime);

            backgroundNotification = v.findViewById(R.id.backgroundNotification);

        }
    }
}
