package com.phc.prs.Adepter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phc.prs.ApproveActivity;
import com.phc.prs.Models.RegisterModel;
import com.phc.prs.R;

import java.util.ArrayList;

public class ApproveAdepter extends RecyclerView.Adapter<ApproveAdepter.ViewHolder> {

    private ArrayList<RegisterModel> dataRegister;
    private Context context;
    private String[] dayOfWeek;

    private String currentLanguage = "en";

    public ApproveAdepter(ArrayList<RegisterModel> dataRegister, Context context, String currentLanguage) {
        this.dataRegister = dataRegister;
        this.context = context;
        this.currentLanguage = currentLanguage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View _view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_approve_user, parent, false);
        return new ViewHolder(_view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String _cusLogin = dataRegister.get(position).getCusLogin();

        String _customerName = dataRegister.get(position).getFName();

        String _permission = dataRegister.get(position).getPermission();
        String _name = dataRegister.get(position).getName();
        String _department = dataRegister.get(position).getDepartmentNameEng();

        String _date = dataRegister.get(position).getDate();
        String _timeTh = dataRegister.get(position).getTimeTh();
        String _timeEng = dataRegister.get(position).getTimeEng();

        //set title name
        holder.textCustomerName.setText(_customerName);

        //set permission text
        String _textPermission = "";
        if(currentLanguage.equals("en")){
            _textPermission = "Normal";
            if(_permission.equals("1")){
                _textPermission = "Department";
            }else if(_permission.equals("2")){
                _textPermission = "Admin";
            }
        }else{
            _textPermission = "ผู้ใช้ทั่วไป";
            if(_permission.equals("1")){
                _textPermission = "หัวหน้าแผนก";
            }else if(_permission.equals("2")){
                _textPermission = "แอดมิน";
            }
        }
        holder.textPermissionRequest.setText(String.format("%s (%s)", _textPermission, _name));

        //set date
        String _textDate = "Date :";
        String _textTime = "Time :";
        String _timeShow = _timeEng;
        if(currentLanguage.equals("th")){
            _textDate = "วันที่ :";
            _textTime = "เวลา :";
            _timeShow = _timeTh;

            _department = dataRegister.get(position).getDepartmentNameTh();
        }

        //set department
        holder.textDepartment.setText(_department);

        holder.textDateTime.setText(String.format("%s %s %s %s", _textDate, _date, _textTime, _timeShow));


        //approve action
        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ApproveActivity) context).clickToApproveUser(_cusLogin, "approve");
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ApproveActivity) context).clickToApproveUser(_cusLogin, "delete");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataRegister.size();
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{

        TextView textCustomerName;
        TextView textPermissionRequest, textDepartment, textDateTime;
        ImageView btnApprove, btnCancel;


        public ViewHolder(@NonNull View v) {
            super(v);

            textCustomerName = v.findViewById(R.id.textCustomerName);


            textPermissionRequest = v.findViewById(R.id.textPermissionRequest);
            textDepartment = v.findViewById(R.id.textDepartment);
            textDateTime = v.findViewById(R.id.textDateTime);

            btnApprove = v.findViewById(R.id.btnApprove);
            btnCancel = v.findViewById(R.id.btnCancel);
        }
    }
}
