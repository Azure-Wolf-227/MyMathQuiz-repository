package com.fanny.freyre.mymathquiz_fannyfreyre;

import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.Animator;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.fanny.freyre.mymathquiz_fannyfreyre.databinding.FragmentEasyBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Easy Level Quiz Fragment
public class EasyQuiz extends Fragment {
    // Constant and variables
    private FragmentEasyBinding binding;

    MediaPlayer correctSoundEffect, incorrectSoundEffect;

    ConstraintLayout easyLayout;
    TextView tvCurrentUser, tvQuestion, tvScore, tvQuestionNo, tvTimer, tvCorrect_Incorrect;
    RadioGroup radioGroup;
    RadioButton rb1, rb2, rb3, rb4;
    Button btnNext;

    int totalQuestions;
    int qCounter = 0;
    int score = 0;
    String currentuser;

    ColorStateList dfRbColor;
    boolean answered;

    CountDownTimer countDownTimer;

    private QuestionModel currentQuestion;

    private List<QuestionModel> questionsList;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentEasyBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // When quiz starts it displays the current user's name, a question, the score, a timer,
        // The number of questions answered and how many remain to be answered
        questionsList = new ArrayList<>();
        easyLayout = binding.easyQuizLayout;
        tvCurrentUser = requireView().findViewById(R.id.tvCurrentUser);
        tvQuestion = requireView().findViewById(R.id.tvQuestion);
        tvScore = requireView().findViewById(R.id.tvScore);
        tvQuestionNo = requireView().findViewById(R.id.tvQuestionNo);
        tvTimer = requireView().findViewById(R.id.tvTimer);
        tvCorrect_Incorrect = requireView().findViewById(R.id.tvCorrect_Incorrect);
        // Radio Group for options
        radioGroup = requireView().findViewById(R.id.radioGroup);
        rb1 = requireView().findViewById(R.id.rb1);
        rb2 = requireView().findViewById(R.id.rb2);
        rb3 = requireView().findViewById(R.id.rb3);
        rb4 = requireView().findViewById(R.id.rb4);
        btnNext = requireView().findViewById(R.id.btnNext); // Button for next question

        dfRbColor = binding.rb1.getTextColors();

        // Use SharedPreferences to get the name of the current user
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        currentuser = sharedPreferences.getString("name","");

        binding.tvCurrentUser.setText(currentuser); // Displays current user

        addQuestions(); // add questions
        totalQuestions = 20; // total questions in quiz
        showNextQuestion(); // show next question

        // Button NEXT
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!answered){
                    if(binding.rb1.isChecked() || binding.rb2.isChecked() ||
                            binding.rb3.isChecked() || binding.rb4.isChecked()){
                        checkAnswer();// Submit selected answer to check if it's correct
                        countDownTimer.cancel();// CountDown Timer will stop once answer is submitted

                    }else {
                        // if no answer is selected when submitting, a message will appear
                        Toast.makeText(requireActivity().getApplicationContext(), "Please select an option",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestion(); // Show next question
                }
            }
        });

    }

    // Method to check if the answer submitted is correct and add to current score
    private void checkAnswer() {
        answered = true;
        RadioButton rbSelected = requireView().findViewById(binding.radioGroup.getCheckedRadioButtonId());
        int answerNo = binding.radioGroup.indexOfChild(rbSelected) + 1; // add to question number
        if(answerNo == currentQuestion.getCorrectAnsNo()){
            score++;
            binding.tvScore.setText(getString(R.string.score, score));
            correctSoundEffect = MediaPlayer.create(getActivity(), R.raw.correct_answer);
            correctSoundEffect.start(); // play chime sound
            binding.tvCorrectIncorrect.setText(R.string.correct_answer); // If correct, text saying "CORRECT!" is displayed
            binding.tvCorrectIncorrect.setTextColor(Color.GREEN);

        } else {
            incorrectSoundEffect = MediaPlayer.create(getActivity(), R.raw.wrong_answer);
            incorrectSoundEffect.start(); // play buzzer sound
            binding.tvCorrectIncorrect.setText(R.string.incorrect_answer); //If incorrect, text saying "INCORRECT!" is displayed
            binding.tvCorrectIncorrect.setTextColor(Color.RED);

        }
        // Once answer is submitted, the correct option will turn green and the incorrect options will turn red
        binding.rb1.setTextColor(Color.RED);
        binding.rb2.setTextColor(Color.RED);
        binding.rb3.setTextColor(Color.RED);
        binding.rb4.setTextColor(Color.RED);
        switch (currentQuestion.getCorrectAnsNo()){
            case 1:
                binding.rb1.setTextColor(Color.GREEN);
                break;
            case 2:
                binding.rb2.setTextColor(Color.GREEN);
                break;
            case 3:
                binding.rb3.setTextColor(Color.GREEN);

                break;
            case 4:
                binding.rb4.setTextColor(Color.GREEN);
                break;
        }
        // If number if answered questions is less than the total amount questions, then go to next question
        // Else, the quiz is finished
        if(qCounter < totalQuestions){
            binding.btnNext.setText(R.string.next); // go to next question
        }else {
            binding.btnNext.setText(R.string.finish); // finish quiz
        }

    }

    // Go to the next question after submitting an answer
    private void showNextQuestion() {

        animate(false);

        // Clear the Radio Group options
        binding.radioGroup.clearCheck();
        // Reset the colors to normal
        binding.rb1.setTextColor(dfRbColor);
        binding.rb2.setTextColor(dfRbColor);
        binding.rb3.setTextColor(dfRbColor);
        binding.rb4.setTextColor(dfRbColor);
        // Reset "CORRECT!/INCORRECT!" to blank
        binding.tvCorrectIncorrect.setText("");
        // Shuffle list of questions
        Collections.shuffle(questionsList);

        // If number of answered questions is less than total number of questions;
        if(qCounter < totalQuestions){
            timer(); // Reset timer
            currentQuestion = questionsList.get(qCounter); // Display current question number
            binding.tvQuestion.setText(currentQuestion.getQuestion()); // Display current question
            // Display answer options
            binding.rb1.setText(currentQuestion.getOption1());
            binding.rb2.setText(currentQuestion.getOption2());
            binding.rb3.setText(currentQuestion.getOption3());
            binding.rb4.setText(currentQuestion.getOption4());

            // Add answered question to counter
            qCounter++;
            binding.btnNext.setText(R.string.submit); // Reset button to "Submit"
            binding.tvQuestionNo.setText(getString(R.string.question_number ,
                    qCounter , totalQuestions)); // Display number of answered questions/total number of questions
            answered = false;

            // Once all questions are answered, go to score board
        }else {
            // Use Shared Preferences to save the current user and score
            SharedPreferences preferences = requireActivity().getSharedPreferences("PREFS", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("currentuser", currentuser);
            editor.putInt("lastScore", score);
            editor.apply();

            // Go to Score Board
            NavHostFragment.findNavController(EasyQuiz.this)
                    .navigate(R.id.action_EasyQuiz_to_EasyScore);
        }
    } // end showNextQuestion

    // Method timer() shows how much time you have to answer the presented question
    // Timer resets on next question
    // If current question is not answered before timer runs out, then no point is given and go to next question
    private void timer() {
        countDownTimer = new CountDownTimer(21000, 1000) { // timer set at 21 seconds
            @Override
            public void onTick(long l) {
                binding.tvTimer.setText("00:" + l/1000);
            }

            @Override
            public void onFinish() {
                // If an option is chosen when timer runs out, it will be checked if it's correct or incorrect
                if(binding.rb1.isChecked() || binding.rb2.isChecked() ||
                        binding.rb3.isChecked() || binding.rb4.isChecked()){
                    checkAnswer();

                // Else, it will move on to the next question and no point will be given
                }else {
                    showNextQuestion();
                }
            }
        }.start();
    } // end timer

    // Method animate
    private void animate(boolean animateOut) {
        // Animation will not be executed on first question
        if (qCounter == 0)
            return;

        // calculate center, coordinates x, y
        int centerX = (easyLayout.getLeft() +
                easyLayout.getRight()) / 2; // calculate center of x axis
        int centerY = (easyLayout.getTop() +
                easyLayout.getBottom()) / 2; // calculate center of y axis
        // calculate the animation radius
        int radius = Math.max(easyLayout.getWidth(),
                easyLayout.getHeight());

        Animator animator;

        //Animation
        if (animateOut) {
            // create circular animation
            animator = ViewAnimationUtils.createCircularReveal(
                    easyLayout, centerX, centerY, radius, 0);
            animator.addListener(
                    new AnimatorListenerAdapter() {
                        // Method invoked on animation end
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            showNextQuestion(); // show next question once animation ends
                        }
                    }
            );
        } // end if
        else {
            animator = ViewAnimationUtils.createCircularReveal(
                    easyLayout, centerX, centerY, 0, radius);
        }
        animator.setDuration(500); // Duration of animation in milliseconds
        animator.start(); // Start animation

    } // end animate

    // List of possible questions for the quiz
    // Easy Quiz's questions are addition, subtraction, and multiplication
    // There is a total of 60 possible questions: 20 addition, 20 subtraction, and 20 multiplication
    private void addQuestions() {
        // Use QuestionModel method
        questionsList.add(new QuestionModel("4 + 3", "5", "7", "9", "11", 2));
        questionsList.add(new QuestionModel("5 + 4", "6", "7", "9", "5", 3));
        questionsList.add(new QuestionModel("3 + 7", "11", "12", "9", "10", 4));
        questionsList.add(new QuestionModel("12 + 7", "19", "21", "15", "20", 1));
        questionsList.add(new QuestionModel("6 + 7", "9", "13", "11", "10", 2));
        questionsList.add(new QuestionModel("5 - 2", "2", "4", "3", "1", 3));
        questionsList.add(new QuestionModel("3 - 1", "2", "1", "0", "4", 1));
        questionsList.add(new QuestionModel("3 - 0", "0", "3", "2", "1", 2));
        questionsList.add(new QuestionModel("70 - 45", "30", "20", "25", "15", 3));
        questionsList.add(new QuestionModel("55 - 25", "25", "20", "40", "30", 4));
        questionsList.add(new QuestionModel("15 x 3", "45", "30", "25", "40", 1));
        questionsList.add(new QuestionModel("9 x 5", "50", "35", "30", "45", 4));
        questionsList.add(new QuestionModel("7 x 10", "50", "70", "60", "40",2));
        questionsList.add(new QuestionModel("4 x 7", "29", "35", "28", "30", 3));
        questionsList.add(new QuestionModel("16 x 4", "60", "48", "68", "64", 4));

        questionsList.add(new QuestionModel("4 + 4", "8", "3", "10", "9", 1));
        questionsList.add(new QuestionModel("12 + 4", "17", "18", "20","16", 4));
        questionsList.add(new QuestionModel("8 + 5", "12", "13", "15", "10", 2));
        questionsList.add(new QuestionModel("27 + 15", "39", "43", "42", "45", 3));
        questionsList.add(new QuestionModel("14 + 19", "33", "30", "29", "35", 1));
        questionsList.add(new QuestionModel("45 - 20", "20", "15", "25", "30", 3));
        questionsList.add(new QuestionModel("3 - 3", "2", "1", "3", "0", 4));
        questionsList.add(new QuestionModel("70 - 40", "45", "30", "40", "25", 2));
        questionsList.add(new QuestionModel("4 - 3", "2", "3", "1", "0", 3));
        questionsList.add(new QuestionModel("55 - 23", "25", "32", "30", "20", 2));
        questionsList.add(new QuestionModel("9 x 3", "27", "18", "36", "28", 1));
        questionsList.add(new QuestionModel("17 x 2", "32", "30", "34", "33", 3));
        questionsList.add(new QuestionModel("6 x 8", "36", "48", "44", "32", 2));
        questionsList.add(new QuestionModel("11 x 9", "97", "100", "80", "99", 4));
        questionsList.add(new QuestionModel("5 x 6", "26", "15", "32", "30", 4));

        questionsList.add(new QuestionModel("1 + 1", "2", "3", "1", "4", 1));
        questionsList.add(new QuestionModel("21 + 12", "30", "33", "32", "34", 2));
        questionsList.add(new QuestionModel("1 + 3", "2", "5","4", "5", 3));
        questionsList.add(new QuestionModel("15 + 12", "22", "28", "29", "27", 4));
        questionsList.add(new QuestionModel("4 + 1", "5", "4", "6", "7", 1));
        questionsList.add(new QuestionModel("6 - 3", "4", "3", "2", "5", 2));
        questionsList.add(new QuestionModel("34 - 25", "12", "13", "9", "10", 3));
        questionsList.add(new QuestionModel("9 - 5", "3", "6", "2", "4", 4));
        questionsList.add(new QuestionModel("16 - 9", "7", "9", "8", "10", 1));
        questionsList.add(new QuestionModel("19 - 11", "10", "8", "9", "7", 2));
        questionsList.add(new QuestionModel("2 x 5", "12", "8", "10", "15", 3));
        questionsList.add(new QuestionModel("12 x 3", "30", "32", "34", "36", 4));
        questionsList.add(new QuestionModel("4 x 6", "24", "20", "22", "26", 1));
        questionsList.add(new QuestionModel("7 x 9", "67", "63", "60", "59", 2));
        questionsList.add(new QuestionModel("3 x 2", "7", "8", "6", "9", 3));

        questionsList.add(new QuestionModel("8 + 10", "12", "16", "14", "18", 4));
        questionsList.add(new QuestionModel("4 + 7", "9", "12", "11", "10", 3));
        questionsList.add(new QuestionModel("28 + 15", "40", "43", "42", "44", 2));
        questionsList.add(new QuestionModel("12 + 7", "19", "20", "18", "21", 1));
        questionsList.add(new QuestionModel("29 + 6", "30", "31", "34", "35", 4));
        questionsList.add(new QuestionModel("34 - 26", "10", "6", "8", "12", 3));
        questionsList.add(new QuestionModel("9 - 2", "8", "7", "6", "5", 2));
        questionsList.add(new QuestionModel("43 - 7", "36", "35", "32", "31", 1));
        questionsList.add(new QuestionModel("13 - 6", "9", "10", "8", "7", 4));
        questionsList.add(new QuestionModel("97 - 27", "69", "68", "70", "71", 3));
        questionsList.add(new QuestionModel("9 x 9", "89", "81", "85", "80", 2));
        questionsList.add(new QuestionModel("12 x 12", "144", "100", "122", "120", 1));
        questionsList.add(new QuestionModel("3 x 6", "12", "18", "20", "22", 2));
        questionsList.add(new QuestionModel("7 x 1", "9", "10", "7", "6", 3));
        questionsList.add(new QuestionModel("14 x 3", "40", "44", "43", "42", 4));

        // Shuffle list when starting a new game
        Collections.shuffle(questionsList);
    } // end addQuestions

}