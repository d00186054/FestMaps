package com.example.androidrealtimelocation2020.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.androidrealtimelocation2020.AllPeopleActivity;
import com.example.androidrealtimelocation2020.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Toast.makeText(getActivity(), "testingggggggg", Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(getActivity(), AllPeopleActivity.class);
        //startActivity(intent);


        return root;
    }
}
