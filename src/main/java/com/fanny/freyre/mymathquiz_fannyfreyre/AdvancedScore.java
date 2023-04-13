package com.fanny.freyre.mymathquiz_fannyfreyre;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fanny.freyre.mymathquiz_fannyfreyre.databinding.AdvancedScoreBinding;
// Advanced Level Score Fragment
public class AdvancedScore extends Fragment {


    // Declare variables
    private AdvancedScoreBinding binding;

    MediaPlayer finalScoreSE;

    ConstraintLayout advancedscoreLayout;

    TextView tv_advancedscore, tv_topfive, tv_currentuser;

    int lastScore;
    int best1, best2, best3, best4, best5;
    String currentuser;
    String user1, user2, user3, user4, user5;

    Button btnNewGame;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = AdvancedScoreBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Play sound when entering score screen
        finalScoreSE = MediaPlayer.create(getActivity(), R.raw.final_score);
        finalScoreSE.start();

        // Get Views of layout
        advancedscoreLayout = binding.advancedScoreLayout;
        tv_currentuser = requireView().findViewById(R.id.tv_currentuser);
        tv_advancedscore = requireView().findViewById(R.id.tv_advancedscore);
        tv_topfive = requireView().findViewById(R.id.tv_topfive);
        btnNewGame = requireView().findViewById(R.id.btnNewGame);

        // Load old users
        SharedPreferences pref = requireActivity().getSharedPreferences("PREFS3", 0);
        currentuser = pref.getString("currentuser", "");
        user1 = pref.getString("user1", "");
        user2 = pref.getString("user2", "");
        user3 = pref.getString("user3", "");
        user4 = pref.getString("user4", "");
        user5 = pref.getString("user5", "");

        // Load old scores
        SharedPreferences preferences = requireActivity().getSharedPreferences("PREFS3", 0);
        lastScore = preferences.getInt("lastScore", 0);
        best1 = preferences.getInt("best1", 0);
        best2 = preferences.getInt("best2", 0);
        best3 = preferences.getInt("best3", 0);
        best4 = preferences.getInt("best4", 0);
        best5 = preferences.getInt("best5", 0);

        // Replace if there is a high score
        if (lastScore > best5) {
            best5 = lastScore;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("best5", best5);
            editor.apply();
            // Replace username along with score
            user5 = currentuser;
            SharedPreferences.Editor editor2 = pref.edit();
            editor2.putString("user5", user5);
            editor2.apply();
        }
        if (lastScore > best4) {
            int temp = best4;
            best4 = lastScore;
            best5 = temp;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("best5", best5);
            editor.putInt("best4", best4);
            editor.apply();
            // Replace username along with score
            String temp2 = user4;
            user4 = currentuser;
            user5 = temp2;
            SharedPreferences.Editor editor2 = pref.edit();
            editor2.putString("user5", user5);
            editor2.putString("user4", user4);
            editor2.apply();
        }
        if (lastScore > best3) {
            int temp = best3;
            best3 = lastScore;
            best4 = temp;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("best4", best4);
            editor.putInt("best3", best3);
            editor.apply();
            // Replace username along with score
            String temp2 = user3;
            user3 = currentuser;
            user4 = temp2;
            SharedPreferences.Editor editor2 = pref.edit();
            editor2.putString("user4", user4);
            editor2.putString("user3", user3);
            editor2.apply();
        }
        if (lastScore > best2) {
            int temp = best2;
            best2 = lastScore;
            best3 = temp;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("best3", best3);
            editor.putInt("best2", best2);
            editor.apply();
            // Replace username along with score
            String temp2 = user2;
            user2 = currentuser;
            user3 = temp2;
            SharedPreferences.Editor editor2 = pref.edit();
            editor2.putString("user3", user3);
            editor2.putString("user2", user2);
            editor2.apply();
        }
        if (lastScore > best1) {
            int temp = best1;
            best1 = lastScore;
            best2 = temp;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("best2", best2);
            editor.putInt("best1", best1);
            editor.apply();
            // Replace username along with score
            String temp2 = user1;
            user1 = currentuser;
            user2 = temp2;
            SharedPreferences.Editor editor2 = pref.edit();
            editor2.putString("user2", user2);
            editor2.putString("user1", user1);
            editor2.apply();

        }
        // Display 5 best scores

        tv_currentuser.setText(currentuser); // Display current user
        tv_advancedscore.setText(getString(R.string.last_score, lastScore)); // Display last score
        tv_topfive.setText(getString(R.string.top_five_scores) + "\n" + "\n" +
                getString(R.string.best_1, best1, user1) + "\n" +
                getString(R.string.best_2, best2, user2) + "\n" +
                getString(R.string.best_3, best3, user3) + "\n" +
                getString(R.string.best_4, best4, user4) + "\n" +
                getString(R.string.best_5, best5, user5)); // Display top five scores and users

        // Go to levels screen to start a new game
        binding.btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AdvancedScore.this)
                        .navigate(R.id.action_AdvancedScore_to_SecondFragment);
            }
        });
    }
}