package com.phc.prs.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phc.prs.Models.CheckStatusAllModel;
import com.phc.prs.R;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Constants.CUS_CODE;
import static com.phc.prs.Constants.Constants.CUS_DEP_CODE;
import static com.phc.prs.Constants.Constants.CUS_LOGIN;
import static com.phc.prs.Constants.Constants.CUS_PERMISSION;
import static com.phc.prs.Constants.Url.GET_CHECK_STATUS_ALL_URL;
import static com.phc.prs.Constants.Constants.PREF_KEY_LANGUAGE_USE;

public class CheckStatusRepairFragment extends Fragment {

    private static TextView dataNotFound;
    private ArrayList<CheckStatusAllModel> mData;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ScrollView scrollView;

    public CheckStatusRepairFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View _view = inflater.inflate(R.layout.fragment_check_status_repair, container, false);

        recyclerView = (RecyclerView) _view.findViewById(R.id.recycler_view);
        swipeRefresh = (SwipeRefreshLayout) _view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));

        scrollView = (ScrollView) _view.findViewById(R.id.scrollview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        dataNotFound = (TextView) _view.findViewById(R.id.data_not_found);
        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        feedData();
        bindEvents();
        setupScreen();
    }

    private class CustomStatusAdepter extends RecyclerView.Adapter<ViewHolder> {

        private String currentLanguage = "en";

        public CustomStatusAdepter(String currentLanguage) {
            this.currentLanguage = currentLanguage;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View _view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_check_status, parent, false);
            return new ViewHolder(_view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            String _repairStatus = mData.get(position).getRepair_Status();

            String _machine = mData.get(position).getDispenserID();
            String _location = getString(R.string.text_building) + " " + mData.get(position).getBuildingName() + " " +
                    getString(R.string.text_floor) + " " + mData.get(position).getBuildingFloor() + " " +
                    getString(R.string.text_room) + " " + mData.get(position).getRoomName();

            String _dateRequest = mData.get(position).getDate_Request();
            String _dateReceive = mData.get(position).getDate_Receive();
            String _dateRepair = mData.get(position).getDate_Repair();

            String _timeRequest = mData.get(position).getTime_RequestEng();
            String _timeReceive = mData.get(position).getTime_ReceiveEng();
            String _timeRepair = mData.get(position).getTime_RepairEng();

            //check language = thai or english
            if (currentLanguage.equals("th")) {
                _timeRequest = mData.get(position).getTime_RequestTH() + " น.";
                _timeReceive = mData.get(position).getTime_ReceiveTH() + " น.";
                _timeRepair = mData.get(position).getTime_RepairTH() + " น.";
            }

            //title
            holder.textMachine.setText(_machine);
            holder.textLocation.setText(_location);

            //date
            holder.dateRequest.setText(_dateRequest);
            holder.dateReceive.setText(_dateReceive);
            holder.dateRepair.setText(_dateRepair);

            //time
            holder.timeRequest.setText(getString(R.string.text_time) + " " + _timeRequest);
            holder.timeReceive.setText(getString(R.string.text_time) + " " + _timeReceive);
            holder.timeRepair.setText(getString(R.string.text_time) + " " + _timeRepair);

            //change image
            holder.imgReceive.setImageResource(setImageReceive(_repairStatus));
            holder.imgRepair.setImageResource(setImageRepair(_repairStatus));

            //set background label and hide detail
            if (_repairStatus.equals("0")) {
                //receive detail
                holder.labelReceive.setBackgroundResource(R.drawable.label_check_status_white_tranparent);
                holder.labelReceive.setTextColor(getResources().getColor(R.color.colorGray));
                holder.dateReceive.setVisibility(View.INVISIBLE);
                holder.timeReceive.setVisibility(View.INVISIBLE);

                //repair detail
                holder.labelRepair.setBackgroundResource(R.drawable.label_check_status_white_tranparent);
                holder.labelRepair.setTextColor(getResources().getColor(R.color.colorGray));
                holder.dateRepair.setVisibility(View.INVISIBLE);
                holder.timeRepair.setVisibility(View.INVISIBLE);

                //icon success
                holder.icSuccessReceive.setVisibility(View.INVISIBLE);
                holder.icSuccessRepair.setVisibility(View.INVISIBLE);
            } else if (_repairStatus.equals("1")) {
                //receive detail
                holder.labelReceive.setBackgroundResource(R.drawable.label_check_status_blue_background);
                holder.labelReceive.setTextColor(getResources().getColor(R.color.colorBlack));
                holder.dateReceive.setVisibility(View.VISIBLE);
                holder.timeReceive.setVisibility(View.VISIBLE);

                //repair detail
                holder.labelRepair.setBackgroundResource(R.drawable.label_check_status_white_tranparent);
                holder.labelRepair.setTextColor(getResources().getColor(R.color.colorGray));
                holder.dateRepair.setVisibility(View.INVISIBLE);
                holder.timeRepair.setVisibility(View.INVISIBLE);

                //icon success
                holder.icSuccessReceive.setVisibility(View.VISIBLE);
                holder.icSuccessRepair.setVisibility(View.INVISIBLE);
            } else {
                //receive detail
                holder.labelReceive.setBackgroundResource(R.drawable.label_check_status_blue_background);
                holder.labelReceive.setTextColor(getResources().getColor(R.color.colorBlack));
                holder.dateReceive.setVisibility(View.VISIBLE);
                holder.timeReceive.setVisibility(View.VISIBLE);

                //repair detail
                holder.labelRepair.setBackgroundResource(R.drawable.label_check_status_blue_background);
                holder.labelRepair.setTextColor(getResources().getColor(R.color.colorBlack));
                holder.dateRepair.setVisibility(View.VISIBLE);
                holder.timeRepair.setVisibility(View.VISIBLE);

                //icon success
                holder.icSuccessReceive.setVisibility(View.VISIBLE);
                holder.icSuccessRepair.setVisibility(View.VISIBLE);
            }
        }

        private int setImageReceive(String status) {
            if (status.equals("0")) {
                return R.drawable.ic_receive_w;
            }
            return R.drawable.ic_receive;
        }

        private int setImageRepair(String status) {
            if (status.equals("2")) {
                return R.drawable.ic_repair;
            }
            return R.drawable.ic_repair_w;
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView textMachine, textLocation;
        ImageView imgReceive, imgRepair;

        TextView labelReceive, labelRepair;

        TextView dateRequest, dateReceive, dateRepair;
        TextView timeRequest, timeReceive, timeRepair;

        ImageView icSuccessReceive, icSuccessRepair;

        public ViewHolder(View View) {
            super(View);

            textMachine = View.findViewById(R.id.textMachine);
            textLocation = View.findViewById(R.id.textLocation);

            imgReceive = View.findViewById(R.id.imgReceive);
            imgRepair = View.findViewById(R.id.imgRepair);

            labelReceive = View.findViewById(R.id.labelReceive);
            labelRepair = View.findViewById(R.id.labelRepair);

            dateRequest = View.findViewById(R.id.dateRequest);
            dateReceive = View.findViewById(R.id.dateReceive);
            dateRepair = View.findViewById(R.id.dateRepair);

            timeRequest = View.findViewById(R.id.timeRequest);
            timeReceive = View.findViewById(R.id.timeReceive);
            timeRepair = View.findViewById(R.id.timeRepair);

            icSuccessReceive = View.findViewById(R.id.icSuccessReceive);
            icSuccessRepair = View.findViewById(R.id.icSuccessRepair);
        }
    }

    private void setupScreen() {
        scrollView.setSmoothScrollingEnabled(true);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);
    }

    private void bindEvents() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedData();
            }
        });


        //when item in recycler view position <= 3 set swipe_refresh(true)
        final LinearLayoutManager _LayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(_LayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (_LayoutManager.findLastCompletelyVisibleItemPosition() < 5) {
                    swipeRefresh.setEnabled(true);
                } else {
                    swipeRefresh.setEnabled(false);
                }

            }
        });
    }

    private void feedData() {
        swipeRefresh.setRefreshing(true);
        feedDataRequestSuccess();
    }

    private void feedDataRequestSuccess() {
        String _key = "";
        String _value = "";
        String _permission = Prefs.getString(CUS_PERMISSION, "");

        switch (_permission) {
            case "2":
                _key = "cusCode";
                _value = Prefs.getString(CUS_CODE, "");
                break;
            case "1":
                _key = "cusDepCode";
                _value = Prefs.getString(CUS_DEP_CODE, "");
                break;
            default :
                _key = "cusLogin";
                _value = Prefs.getString(CUS_LOGIN, "");
                break;
        }

        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add(_key, _value)
                .add("status", "2")
                .build();

        Request _request = new Request.Builder().url(BASE_URL + GET_CHECK_STATUS_ALL_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(getContext(), R.string.check_your_network, Toast.LENGTH_LONG).show();
                        dataNotFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {

                    String _result = response.body().string();

                    Gson _gson = new Gson();

                    Type type = new TypeToken<List<CheckStatusAllModel>>() {
                    }.getType();

                    mData = _gson.fromJson(_result, type);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefresh.setRefreshing(false);
                            recyclerView.setVisibility(View.VISIBLE);
                            if (!mData.isEmpty()) {
                                dataNotFound.setVisibility(View.GONE);
                                recyclerView.setAdapter(new CustomStatusAdepter(Prefs.getString(PREF_KEY_LANGUAGE_USE, "en")));
                            } else {
                                dataNotFound.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

    }


}