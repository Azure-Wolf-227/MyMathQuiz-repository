package com.fanny.freyre.mymathquiz_fannyfreyre;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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

import com.fanny.freyre.mymathquiz_fannyfreyre.databinding.FragmentIntermediateBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// Intermediate Level Quiz Fragment
public class IntermediateQuiz extends Fragment {

    // Constant and variables
    private FragmentIntermediateBinding binding;

    MediaPlayer correctSoundEffect, incorrectSoundEffect;

    ConstraintLayout intermediateLayout;
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

        binding = FragmentIntermediateBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // When quiz starts it displays the current user's name, a question, the score, a timer,
        // The number of questions answered and how many remain to be answered
        questionsList = new ArrayList<>();
        intermediateLayout = binding.intermediateQuizLayout;
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
            SharedPreferences preferences = requireActivity().getSharedPreferences("PREFS2", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("currentuser", currentuser);
            editor.putInt("lastScore", score);
            editor.apply();

            // Go to Score Board
            NavHostFragment.findNavController(IntermediateQuiz.this)
                    .navigate(R.id.action_InterQuiz_to_InterScore);
        }
    } // end showNextQuestion

    // Method timer() shows how much time you have to answer the presented question
    // Timer resets on next question
    // If current question is not answered before timer runs out, then no point is given and go to next question
    private void timer() {
        countDownTimer = new CountDownTimer(31000, 1000) { // timer set at 31 seconds
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
        int centerX = (intermediateLayout.getLeft() +
                intermediateLayout.getRight()) / 2; // calculate center of x axis
        int centerY = (intermediateLayout.getTop() +
                intermediateLayout.getBottom()) / 2; // calculate center of y axis
        // calculate the animation radius
        int radius = Math.max(intermediateLayout.getWidth(),
                intermediateLayout.getHeight());

        Animator animator;

        //Animation
        if (animateOut) {
            // create circular animation
            animator = ViewAnimationUtils.createCircularReveal(
                    intermediateLayout, centerX, centerY, radius, 0);
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
                    intermediateLayout, centerX, centerY, 0, radius);
        }
        animator.setDuration(500); // Duration of animation in milliseconds
        animator.start(); // Start animation

    } // end animate

    // List of possible questions for the quiz
    // Easy Quiz's questions are addition, subtraction, multiplication, division, exponents, and square root
    // There is a total of 110 possible questions: 20 addition, 20 subtraction, 20 multiplication,
    // 20 division, 20 exponents, and 10 square root
    private void addQuestions() {
        // Use QuestionModel method
        // Addition
        questionsList.add(new QuestionModel("31 + 5", "36", "35", "34", "30", 1));
        questionsList.add(new QuestionModel("53 + 4", "56", "57", "58", "59", 2));
        questionsList.add(new QuestionModel("39 + 7", "48", "47", "46", "49", 3));
        questionsList.add(new QuestionModel("46 + 8", "51", "52", "53", "54", 4));
        questionsList.add(new QuestionModel("40 + 6", "46", "45", "47", "48", 1));
        questionsList.add(new QuestionModel("35 + 33", "67", "68", "69", "70", 2));
        questionsList.add(new QuestionModel("53 + 83", "134", "135", "136", "137", 3));
        questionsList.add(new QuestionModel("76 + 29", "102", "103", "104", "105", 4));
        questionsList.add(new QuestionModel("21 + 14", "35", "36", "37", "38", 1));
        questionsList.add(new QuestionModel("59 + 27", "85", "86", "87", "88", 2));
        questionsList.add(new QuestionModel("721 + 18", "737", "738", "739", "740", 3));
        questionsList.add(new QuestionModel("536 + 55", "588", "589", "590", "591", 4));
        questionsList.add(new QuestionModel("190 + 19", "209", "210", "208", "211", 1));
        questionsList.add(new QuestionModel("228 + 44", "271", "272", "274", "276", 2));
        questionsList.add(new QuestionModel("539 + 40", "575", "577", "579", "580", 3));
        questionsList.add(new QuestionModel("316 + 843", "1,156", "1,157", "1,158", "1,159", 4));
        questionsList.add(new QuestionModel("723 + 842", "1,565", "1,564", "1,563", "1,562", 1));
        questionsList.add(new QuestionModel("337 + 804", "1,140", "1,141", "1,142", "1,143", 2));
        questionsList.add(new QuestionModel("638 + 643", "1,283", "1,282", "1,281", "1,280", 3));
        questionsList.add(new QuestionModel("734 + 276", "1,013", "1,012", "1,011", "1,010", 4));
        // Subtraction
        questionsList.add(new QuestionModel("14 - 8", "6", "7", "8", "9", 1));
        questionsList.add(new QuestionModel("44 - 3", "40", "41", "39", "38", 2));
        questionsList.add(new QuestionModel("58 - 9", "47", "48", "49", "50", 3));
        questionsList.add(new QuestionModel("98 - 2", "94", "95", "97", "96", 4));
        questionsList.add(new QuestionModel("81 - 7", "74", "75", "76", "77", 1));
        questionsList.add(new QuestionModel("38 - 29", "8", "9", "10", "11", 2));
        questionsList.add(new QuestionModel("77 - 69", "6", "7", "8", "9", 3));
        questionsList.add(new QuestionModel("25 - 18", "10", "9", "8", "7", 4));
        questionsList.add(new QuestionModel("67 - 49", "18", "19", "20", "21", 1));
        questionsList.add(new QuestionModel("41 - 35", "5", "6", "7", "8", 2));
        questionsList.add(new QuestionModel("790 - 53", "735", "736", "737", "738", 3));
        questionsList.add(new QuestionModel("497 - 86", "408", "409", "410", "411", 4));
        questionsList.add(new QuestionModel("172 - 71", "101", "100", "99", "102", 1));
        questionsList.add(new QuestionModel("676 - 17", "660", "659", "658", "657", 2));
        questionsList.add(new QuestionModel("610 - 85", "523", "524", "525", "526", 3));
        questionsList.add(new QuestionModel("146 - 131", "18", "17", "16", "15", 4));
        questionsList.add(new QuestionModel("239 - 176", "63", "64", "65", "66", 1));
        questionsList.add(new QuestionModel("981 - 882", "100", "99", "98", "97", 2));
        questionsList.add(new QuestionModel("802 - 751", "49", "50", "51", "52", 3));
        questionsList.add(new QuestionModel("583 - 536", "44", "45", "46", "47", 4));
        // Multiplication
        questionsList.add(new QuestionModel("12 x 8", "96", "83", "74", "65", 1));
        questionsList.add(new QuestionModel("13 x 9", "116", "117", "118", "119", 2));
        questionsList.add(new QuestionModel("14 x 7", "92", "94", "98", "96", 3));
        questionsList.add(new QuestionModel("15 x 6", "96", "80", "86", "90", 4));
        questionsList.add(new QuestionModel("16 x 3", "48", "46", "44", "42", 1));
        questionsList.add(new QuestionModel("17 x 11", "177", "187", "197", "167", 2));
        questionsList.add(new QuestionModel("18 x 12", "200", "210", "216", "220", 3));
        questionsList.add(new QuestionModel("19 x 10", "160", "170", "180", "190", 4));
        questionsList.add(new QuestionModel("20 x 13", "260", "265", "270", "275", 1));
        questionsList.add(new QuestionModel("11 x 16", "170", "176", "180", "186", 2));
        questionsList.add(new QuestionModel("11 x 100", "110", "1,000", "1,100", "1,150", 3));
        questionsList.add(new QuestionModel("9 x 125", "1,200", "1,300", "1,400", "1,125", 4));
        questionsList.add(new QuestionModel("8 x 150", "1,200", "1,900", "2,000", "1,800", 1));
        questionsList.add(new QuestionModel("7 x 120", "820", "840", "860", "880", 2));
        questionsList.add(new QuestionModel("6 x 110", "650", "656", "660", "676", 3));
        questionsList.add(new QuestionModel("130 x 5", "630", "640", "645", "650", 4));
        questionsList.add(new QuestionModel("140 x 4", "560", "564", "568", "562", 1));
        questionsList.add(new QuestionModel("100 x 3", "320", "323", "300", "333", 2));
        questionsList.add(new QuestionModel("105 x 2", "100", "200", "210", "220", 3));
        questionsList.add(new QuestionModel("150 x 2", "250", "200", "350", "300", 4));
        // Division
        questionsList.add(new QuestionModel("225 ÷ 15", "15", "20", "25", "30", 1));
        questionsList.add(new QuestionModel("24 ÷ 2", "10", "12", "14", "16", 2));
        questionsList.add(new QuestionModel("140 ÷ 10", "10", "12", "14", "16", 3));
        questionsList.add(new QuestionModel("60 ÷ 12", "3", "15", "12", "5", 4));
        questionsList.add(new QuestionModel("100 ÷ 10", "10", "20", "25", "50", 1));
        questionsList.add(new QuestionModel("4 ÷ 2", "4", "2", "1", "0", 2));
        questionsList.add(new QuestionModel("6 ÷ 2", "1", "2", "3", "4", 3));
        questionsList.add(new QuestionModel("12 ÷ 1", "2", "6", "8", "12", 4));
        questionsList.add(new QuestionModel("88 ÷ 8", "11", "80", "10", "8", 1));
        questionsList.add(new QuestionModel("30 ÷ 3", "5", "10", "15", "20", 2));
        questionsList.add(new QuestionModel("12 ÷ 2", "2", "4", "6", "8", 3));
        questionsList.add(new QuestionModel("55 ÷ 5", "0", "5", "10", "11", 4));
        questionsList.add(new QuestionModel("78 ÷ 6", "13", "12", "11", "10", 1));
        questionsList.add(new QuestionModel("14 ÷ 2", "14", "7", "5", "3", 2));
        questionsList.add(new QuestionModel("72 ÷ 8", "3", "6", "9", "12", 3));
        questionsList.add(new QuestionModel("48 ÷ 8", "0", "2", "4", "6", 4));
        questionsList.add(new QuestionModel("14 ÷ 7", "2", "4", "7", "9", 1));
        questionsList.add(new QuestionModel("156 ÷ 13", "6", "12", "18", "24", 2));
        questionsList.add(new QuestionModel("105 ÷ 15", "3", "5", "7", "9", 3));
        questionsList.add(new QuestionModel("9 ÷ 3", "12", "9", "6", "3", 4));
        // Exponents
        questionsList.add(new QuestionModel("0 ^ 2", "0", "2", "4", "6", 1));
        questionsList.add(new QuestionModel("4 ^ 2", "12", "16", "20", "24", 2));
        questionsList.add(new QuestionModel("2 ^ 4", "8", "12", "16", "20", 3));
        questionsList.add(new QuestionModel("9 ^ 1", "0", "3", "6", "9", 4));
        questionsList.add(new QuestionModel("8 ^ 2", "64", "30", "16", "10", 1));
        questionsList.add(new QuestionModel("1 ^ 1", "2", "1", "3", "4", 2));
        questionsList.add(new QuestionModel("3 ^ 3", "9", "18", "27", "36", 3));
        questionsList.add(new QuestionModel("2 ^ 2", "10", "8", "6", "4", 4));
        questionsList.add(new QuestionModel("2 ^ 3", "8", "6", "4", "2", 1));
        questionsList.add(new QuestionModel("3 ^ 2", "6", "9", "12", "15", 2));
        questionsList.add(new QuestionModel("3 ^ 4", "7", "12", "81", "85", 3));
        questionsList.add(new QuestionModel("3 ^ 1", "0", "1", "2", "3", 4));
        questionsList.add(new QuestionModel("2 ^ 5", "32", "35", "38", "41", 1));
        questionsList.add(new QuestionModel("5 ^ 2", "10", "25", "30", "20", 2));
        questionsList.add(new QuestionModel("5 ^ 3", "75", "100", "125", "150", 3));
        questionsList.add(new QuestionModel("5 ^ 1", "8", "7", "6", "5", 4));
        questionsList.add(new QuestionModel("6 ^ 2", "36", "30", "24", "18", 1));
        questionsList.add(new QuestionModel("7 ^ 2", "45", "49", "53", "57", 2));
        questionsList.add(new QuestionModel("10 ^ 2", "25", "50", "100", "200", 3));
        questionsList.add(new QuestionModel("6 ^ 1", "0", "2", "4", "6", 4));
        // Square Root
        questionsList.add(new QuestionModel("√1", "1", "2", "3", "4", 1));
        questionsList.add(new QuestionModel("√4", "0", "2", "4", "8", 2));
        questionsList.add(new QuestionModel("√9", "9", "6", "3", "0", 3));
        questionsList.add(new QuestionModel("√16", "10", "8", "6", "4", 4));
        questionsList.add(new QuestionModel("√25", "5", "10", "15", "20", 1));
        questionsList.add(new QuestionModel("√36", "3", "6", "9", "12", 2));
        questionsList.add(new QuestionModel("√49", "21", "14", "7", "0", 3));
        questionsList.add(new QuestionModel("√64", "2", "4", "6", "8", 4));
        questionsList.add(new QuestionModel("√81", "9", "6", "3", "0", 1));
        questionsList.add(new QuestionModel("√100", "5", "10", "20", "40", 2));

        // Shuffle list when starting a new game
        Collections.shuffle(questionsList);
    } // end addQuestions
}