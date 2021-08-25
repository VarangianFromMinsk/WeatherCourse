package com.weathercourse.main.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.weathercourse.main.R;
import com.weathercourse.main.databinding.FragmentRequestsBinding;


public class Requests_Fragment extends Fragment {

    private FragmentRequestsBinding binding;
    private static final int LOCATION_PERMISSION_CODE = 20;

    // TODO: Rename and change types and number of parameters
    public static Requests_Fragment newInstance(String param1, String param2) {
        Requests_Fragment fragment = new Requests_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//
//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_requests,
                container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!checkPermission()){
            Navigation.findNavController(view).navigate(R.id.action_requestsFragment_to_geoPosition_Fragment);
        }
        else{
            binding.buttonRequests.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(checkPermission()){

                        if(ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)){

                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setMessage("This permissions are needed for normal work.");
                            builder.setTitle("Please grant those permissions");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(
                                            requireActivity(),
                                            new String[]{
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                            },
                                            LOCATION_PERMISSION_CODE
                                    );
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            Button yes = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

                            if(yes != null ) {
                                yes.setBackgroundColor(requireActivity().getResources().getColor(R.color.black));
                            }

                            }else{

                            ActivityCompat.requestPermissions(
                                    requireActivity(),
                                    new String[]{
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                    },
                                    LOCATION_PERMISSION_CODE
                            );
                        }
                    }else{
                        Navigation.findNavController(view).navigate(R.id.action_requestsFragment_to_geoPosition_Fragment);
                    }
                }
            });
        }

    }

    public boolean checkPermission(){
        return ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(
                requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
    }

}