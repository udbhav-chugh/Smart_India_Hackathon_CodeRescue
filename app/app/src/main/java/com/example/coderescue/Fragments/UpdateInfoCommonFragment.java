package com.example.coderescue.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderescue.Activities.UpdateInfoActivity;
import com.example.coderescue.R;

public class UpdateInfoCommonFragment extends Fragment {

    soup.neumorphism.NeumorphCardView update_victim, update_disaster;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.update_info_common, container, false);

        update_victim = root.findViewById(R.id.update_victim);
        update_disaster = root.findViewById(R.id.update_disaster);

        update_victim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateInfoActivity.class);
                startActivity(intent);
            }
        });

        update_disaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateInfoActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}
