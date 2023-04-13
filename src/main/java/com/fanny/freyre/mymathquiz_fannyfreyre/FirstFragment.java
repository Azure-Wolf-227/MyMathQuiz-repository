package com.fanny.freyre.mymathquiz_fannyfreyre;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.fanny.freyre.mymathquiz_fannyfreyre.databinding.FragmentFirstBinding;
import java.util.Objects;

public class FirstFragment extends Fragment {

    // variables
    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // PLAY Button
            binding.btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Save imputed user name into String currentuser
                    String currentuser = binding.etName.getText().toString();

                    // If String currentuser is empty, show message
                    if (currentuser.isEmpty()) {
                        Toast.makeText(getActivity().getApplicationContext(), "Please enter an user name", Toast.LENGTH_SHORT).show();
                    } else {
                        // Confirm input data
                        Toast.makeText(getActivity().getApplicationContext(), "Input Data Confirmation", Toast.LENGTH_SHORT).show();
                        binding.tvLastUser.setVisibility(View.INVISIBLE);
                        // go to levels screen in SelectLevel
                        NavHostFragment.findNavController(FirstFragment.this)
                                .navigate(R.id.action_FirstFragment_to_SecondFragment);
                    }
                }
            });

    }

    @Override
    public void onStart() {
        super.onStart();

        // Use SharedPreferences to show the name of the Last User
        SharedPreferences sh = getActivity().getSharedPreferences("SharedPref", Context.MODE_PRIVATE);

        String user = sh.getString("name", "");

        if(!user.isEmpty()) {
            binding.tvLastUser.setText(getString(R.string.last_user, user));
        }
        else binding.tvLastUser.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Use SharedPreferences to save user name input
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putString("name", Objects.requireNonNull(binding.etName.getText().toString()));
        myEdit.apply();
    }

}