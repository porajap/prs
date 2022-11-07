package com.phc.prs.Adepter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.phc.prs.Models.QuestionHistoryModel;
import com.phc.prs.R;

import java.util.ArrayList;

public class HistoryQuestionAdepter extends RecyclerView.Adapter<HistoryQuestionAdepter.ViewHolder> {

    private ArrayList<QuestionHistoryModel> mData;
    private static final int[] TITLE_COLOR =
            new int[]{
                    R.drawable.title_primary_background,
                    R.drawable.title_warning_background,
                    R.drawable.title_success_background
            };
    int indexColor = 0;
    private String currentLanguage = "en";

    public HistoryQuestionAdepter(ArrayList<QuestionHistoryModel> mData, String currentLanguage) {
        this.mData = mData;
        this.currentLanguage = currentLanguage;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View _view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_history_question, parent, false);

        return new ViewHolder(_view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String _time = mData.get(position).getTimeEng();
        if (currentLanguage.equals("th")) {
            _time = mData.get(position).getTimeTh() + " น.";
        }

        holder.textView_name.setText(mData.get(position).getCus_Name());
        holder.textView_date_request.setText(mData.get(position).getDate_Request() + " " + _time);
        holder.textView_request.setText(mData.get(position).getComment());

        holder.titleColor.setBackgroundResource(TITLE_COLOR[indexColor]);
        indexColor++;
        if (indexColor == 3) {
            indexColor = 0;
        }

        if (mData.get(position).getReply().equals("-") || mData.get(position).getReply().isEmpty()) {
            holder.layout_reply.setVisibility(View.GONE);
        } else {
            holder.layout_reply.setVisibility(View.VISIBLE);

            holder.textView_admin.setText(mData.get(position).getTechnician_Reply());
            holder.textView_date_reply.setText(mData.get(position).getDate_Request() + " " + _time);
            holder.textView_reply.setText(mData.get(position).getReply());
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout titleColor;
        TextView textView_request, textView_name, textView_date_request;
        TextView textView_reply, textView_date_reply, textView_admin;
        LinearLayoutCompat layout_reply;

        public ViewHolder(@NonNull View View) {
            super(View);

            textView_request = (TextView) View.findViewById(R.id.textview_request);
            textView_name = (TextView) View.findViewById(R.id.textview_name);
            textView_date_request = (TextView) View.findViewById(R.id.textview_date_request);

            textView_reply = (TextView) View.findViewById(R.id.textview_reply);
            textView_date_reply = (TextView) View.findViewById(R.id.textview_date_reply);
            textView_admin = (TextView) View.findViewById(R.id.textview_admin);

            layout_reply = (LinearLayoutCompat) View.findViewById(R.id.layout_reply);
            titleColor = (RelativeLayout) View.findViewById(R.id.titleColor);

        }
    }


}



