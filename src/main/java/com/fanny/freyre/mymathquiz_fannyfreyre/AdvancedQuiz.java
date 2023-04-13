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

import com.fanny.freyre.mymathquiz_fannyfreyre.databinding.FragmentAdvancedBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// Advanced Level Quiz Fragment
public class AdvancedQuiz extends Fragment {

    // Constant and variables
    private FragmentAdvancedBinding binding;

    MediaPlayer correctSoundEffect, incorrectSoundEffect;

    ConstraintLayout advancedLayout;
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

        binding = FragmentAdvancedBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // When quiz starts it displays the current user's name, a question, the score, a timer,
        // The number of questions answered and how many remain to be answered
        questionsList = new ArrayList<>();
        advancedLayout = binding.advancedQuizLayout;
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
            SharedPreferences preferences = requireActivity().getSharedPreferences("PREFS3", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("currentuser", currentuser);
            editor.putInt("lastScore", score);
            editor.apply();

            // Go to Score Board
            NavHostFragment.findNavController(AdvancedQuiz.this)
                    .navigate(R.id.action_AdvancedQuiz_to_Advanced_Score);
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
        int centerX = (advancedLayout.getLeft() +
                advancedLayout.getRight()) / 2; // calculate center of x axis
        int centerY = (advancedLayout.getTop() +
                advancedLayout.getBottom()) / 2; // calculate center of y axis
        // calculate the animation radius
        int radius = Math.max(advancedLayout.getWidth(),
                advancedLayout.getHeight());

        Animator animator;

        //Animation
        if (animateOut) {
            // create circular animation
            animator = ViewAnimationUtils.createCircularReveal(
                    advancedLayout, centerX, centerY, radius, 0);
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
                    advancedLayout, centerX, centerY, 0, radius);
        }
        animator.setDuration(500); // Duration of animation in milliseconds
        animator.start(); // Start animation

    } // end animate

    // List of possible questions for the quiz
    // Easy Quiz's questions are addition, subtraction, and multiplication
    // There is a total of 120 possible questions: 20 addition, 20 subtraction, 20 multiplication,
    // 20 division, 20 exponents, 10 square root, and 10 logical operators
    // Some of the exercises will have positive numbers and some will have negative numbers
    private void addQuestions() {
        // Use QuestionModel method
        // Addition
        questionsList.add(new QuestionModel("747 + 21", "768", "769", "770", "771", 1));
        questionsList.add(new QuestionModel("926 + 22", "947", "948", "949", "950", 2));
        questionsList.add(new QuestionModel("433 + 70", "500", "501", "502", "503", 3));
        questionsList.add(new QuestionModel("255 + 66", "318", "319", "320", "321", 4));
        questionsList.add(new QuestionModel("369 + 85", "481", "482", "483", "484", 1));
        questionsList.add(new QuestionModel("359 + 200", "558", "559", "560", "561", 2));
        questionsList.add(new QuestionModel("201 + 277", "476", "477", "478", "479", 3));
        questionsList.add(new QuestionModel("479 + 121", "450", "500", "550", "600", 4));
        questionsList.add(new QuestionModel("737 + 252", "989", "990", "991", "992", 1));
        questionsList.add(new QuestionModel("597 + 118", "341", "342", "343", "344", 2));
        questionsList.add(new QuestionModel("-431 + -65", "495", "496", "-496", "-495", 3));
        questionsList.add(new QuestionModel("-105 + 13", "-90", "90", "92", "-92", 4));
        questionsList.add(new QuestionModel("-668 + -76", "-744", "744", "800", "-800", 1));
        questionsList.add(new QuestionModel("967 + -57", "909", "910", "-909", "-910", 2));
        questionsList.add(new QuestionModel("-998 + -76", "-1,000", "1,074", "-1,074", "1,000", 3));
        questionsList.add(new QuestionModel("-264 + 117", "140", "-140", "147", "-147", 4));
        questionsList.add(new QuestionModel("-581 + -114", "-695", "695", "650", "-650", 1));
        questionsList.add(new QuestionModel("230 + -454", "224", "-224", "225", "-225", 2));
        questionsList.add(new QuestionModel("-151 + -602", "753", "750", "-753", "-750", 3));
        questionsList.add(new QuestionModel("-248 + 525", "-265", "265", "-277", "277", 4));
        // Subtraction
        questionsList.add(new QuestionModel("430 - 82", "348", "350", "352", "354", 1));
        questionsList.add(new QuestionModel("814 - 35", "778", "779", "780", "781", 2));
        questionsList.add(new QuestionModel("314 - 15", "297", "298", "299", "300", 3));
        questionsList.add(new QuestionModel("868 - 19", "846", "847", "848", "849", 4));
        questionsList.add(new QuestionModel("151 - 148", "3", "4", "5", "6", 1));
        questionsList.add(new QuestionModel("577 - 11", "567", "566", "568", "569", 2));
        questionsList.add(new QuestionModel("732 - 547", "183", "184", "185", "186", 3));
        questionsList.add(new QuestionModel("580 - 381", "196", "197", "198", "199", 4));
        questionsList.add(new QuestionModel("583 - 536", "47", "48", "49", "50", 1));
        questionsList.add(new QuestionModel("146 - 131", "14", "15", "16", "17", 2));
        questionsList.add(new QuestionModel("482 - -35", "447", "-447", "517", "-517", 3));
        questionsList.add(new QuestionModel("-537 - -17", "-554", "554", "520", "-520", 4));
        questionsList.add(new QuestionModel("-163 - 66", "-229", "229", "97", "-97", 1));
        questionsList.add(new QuestionModel("-693 - -11", "581", "-682", "581", "682", 2));
        questionsList.add(new QuestionModel("180 - -71", "109", "-109", "251", "-251", 3));
        questionsList.add(new QuestionModel("-696 - -112", "550", "-550", "584", "-584", 4));
        questionsList.add(new QuestionModel("-547 - 541", "-1,088", "-6", "6", "1,088", 1));
        questionsList.add(new QuestionModel("-718 - -157", "561", "-561", "500", "-500", 2));
        questionsList.add(new QuestionModel("802 - -751", "-51", "51", "1,553", "-1,553", 3));
        questionsList.add(new QuestionModel("-474 - -276", "-200", "200", "198", "-198", 4));
        // Multiplication
        questionsList.add(new QuestionModel( "14 x 7", "98", "94", "92", "96",1));
        questionsList.add(new QuestionModel( "15 x 6", "96", "90", "86", "80",2));
        questionsList.add(new QuestionModel( "13 x 9", "116", "121", "117", "119",3));
        questionsList.add(new QuestionModel( "12 x 8", "65", "83", "74", "96", 4));
        questionsList.add(new QuestionModel("16 x 3", "48", "46", "44", "42", 1));
        questionsList.add(new QuestionModel("17 x 11", "177", "147", "197", "187", 4));
        questionsList.add(new QuestionModel("18 x 12", "216", "210", "214", "220", 1));
        questionsList.add(new QuestionModel("19 x 10", "160", "170", "190", "197", 3));
        questionsList.add(new QuestionModel("20 x 13", "295", "260", "270", "275", 2));
        questionsList.add(new QuestionModel("11 x 16", "170", "176", "180", "186", 2));
        questionsList.add(new QuestionModel("-11 x 100", "-110", "1,100", "110", "-1,100", 4));
        questionsList.add(new QuestionModel("-9 x -125", "1,200", "-1,300", "1,125", "-1,125", 3));
        questionsList.add(new QuestionModel("8 x -150", "1,200", "-1,200", "1,000", "-1,800", 2));
        questionsList.add(new QuestionModel("-7 x -120", "820", "-860", "840", "-880", 3));
        questionsList.add(new QuestionModel("-6 x 110", "630", "-630", "660", "-660", 4));
        questionsList.add(new QuestionModel("-130 x -5", "650", "640", "-645", "-650", 1));
        questionsList.add(new QuestionModel("140 x -4", "560", "-565", "-560", "565", 3));
        questionsList.add(new QuestionModel("-100 x -3", "320", "300", "-300", "-333", 2));
        questionsList.add(new QuestionModel("-105 x 2", "-210", "200", "210", "-220", 1));
        questionsList.add(new QuestionModel("-150 x -2", "-250", "200", "-300", "300", 4));
        // Division
        questionsList.add(new QuestionModel("182 ÷ 13", "14", "15", "16", "17", 1));
        questionsList.add(new QuestionModel("130 ÷ 13", "5", "10", "15", "20", 2));
        questionsList.add(new QuestionModel("20 ÷ 5", "0", "2", "4", "6", 3));
        questionsList.add(new QuestionModel("90 ÷ 15", "0", "2", "4", "6", 4));
        questionsList.add(new QuestionModel("70 ÷ 5", "14", "15", "16", "17", 1));
        questionsList.add(new QuestionModel("168 ÷ 14", "11", "12", "13", "14", 2));
        questionsList.add(new QuestionModel("18 ÷ 2", "3", "6", "9", "12", 3));
        questionsList.add(new QuestionModel("28 ÷ 4", "4", "5", "6", "7", 4));
        questionsList.add(new QuestionModel("40 ÷ 10", "4", "8", "12", "16", 1));
        questionsList.add(new QuestionModel("16 ÷ 8", "0", "2", "4", "6", 2));
        questionsList.add(new QuestionModel("64 ÷ -8", "8", "-4", "-8", "4", 3));
        questionsList.add(new QuestionModel("-8 ÷ -8", "-2", "2", "-1", "1", 4));
        questionsList.add(new QuestionModel("-22 ÷ 2", "-11", "-10", "10", "11", 1));
        questionsList.add(new QuestionModel("-108 ÷ -12", "-8", "9", "-9", "8", 2));
        questionsList.add(new QuestionModel("135 ÷ -5", "27", "25", "-27", "-25", 3));
        questionsList.add(new QuestionModel("-130 ÷ -5", "27", "-27", "-26", "26", 4));
        questionsList.add(new QuestionModel("-8 ÷ 4", "-2", "4", "-4", "2", 1));
        questionsList.add(new QuestionModel("-54 ÷ -6", "", "-9", "9", "", 3));
        questionsList.add(new QuestionModel("120 ÷ -15", "5", "-8", "8", "-5", 2));
        questionsList.add(new QuestionModel("-30 ÷ -2", "3", "-5", "-10", "15", 4));
        //Exponents
        questionsList.add(new QuestionModel("0 ^ 10", "0", "10", "20", "30", 1));
        questionsList.add(new QuestionModel("40 ^ 2", "1,200", "1,600", "2,000", "2,400", 2));
        questionsList.add(new QuestionModel("2 ^ 4", "8", "12", "16", "20", 3));
        questionsList.add(new QuestionModel("91 ^ 1", "100", "31", "61", "91", 4));
        questionsList.add(new QuestionModel("2 ^ 8", "256", "300", "160", "100", 1));
        questionsList.add(new QuestionModel("100 ^ 1", "200", "100", "300", "400", 2));
        questionsList.add(new QuestionModel("12 ^ 2", "100", "122", "144", "146", 3));
        questionsList.add(new QuestionModel("2 ^ 2", "10", "8", "6", "4", 4));
        questionsList.add(new QuestionModel("2 ^ 3", "8", "6", "4", "2", 1));
        questionsList.add(new QuestionModel("3 ^ 2", "6", "9", "12", "15", 2));
        questionsList.add(new QuestionModel("-3 ^ 4", "81", "-12", "-81", "7", 3));
        questionsList.add(new QuestionModel("-3 ^ 1", "0", "-1", "2", "-3", 4));
        questionsList.add(new QuestionModel("-2 ^ 5", "-32", "32", "-10", "10", 1));
        questionsList.add(new QuestionModel("5 ^ -2", "0.25", "0.04", "0.02", "0", 2));
        questionsList.add(new QuestionModel("-5 ^ 3", "15", "125", "-125", "-15", 3));
        questionsList.add(new QuestionModel("-5 ^ 1", "5", "-1", "6", "-5", 4));
        questionsList.add(new QuestionModel("-6 ^ 2", "-36", "36", "-24", "18", 1));
        questionsList.add(new QuestionModel("-7 ^ 2", "49", "-49", "14", "-14", 2));
        questionsList.add(new QuestionModel("10 ^ -2", "0.25", "20", "0.01", "200", 3));
        questionsList.add(new QuestionModel("6 ^ -2", "0.025", "0.25", "0.04", "0.0025", 4));
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
        // Logical Operators
        questionsList.add(new QuestionModel("3 + 4 = 7 && 3 x 4 = 12", "True", "False", "Yes", "No", 1));
        questionsList.add(new QuestionModel("2 ^ 2 = 4 && 4 = 1 + 2", "True", "False", "Yes", "No", 2));
        questionsList.add(new QuestionModel("12 ÷ 3 = 6 && 12 ÷ 4 = 3", "No", "True", "False", "Yes", 3));
        questionsList.add(new QuestionModel("3 - 2 = 0 && 5 - 4 = 2", "Yes", "No", "Truth", "False", 4));
        questionsList.add(new QuestionModel("4 + 3 = 7 || 3 x 4 = 12", "True", "False", "Yes", "No", 1));
        questionsList.add(new QuestionModel("2 ^ 2 || 4 = 2 + 1", "False", "True", "No", "Yes", 2));
        questionsList.add(new QuestionModel("12 ÷ 3 = 6 || 12 ÷ 4 = 3", "Yes", "No", "True", "False", 3));
        questionsList.add(new QuestionModel("2 + 3 = 4 || 5 + 4 = 8", "Yes", "No", "True", "False", 4));
        questionsList.add(new QuestionModel("4 > 3", "!4 > 3", "4 < 3", "3 > 4", "!4 < 3", 1));
        questionsList.add(new QuestionModel("!2 + 2 = 4", "4", "2 + 2 = 4", "2 > 2", "2 = 2", 2));
        questionsList.add(new QuestionModel("5 < 8", "5 > 8", "8 < 5", "!5 < 8", "!8 < 5", 3));
        questionsList.add(new QuestionModel("!10 = 2 x 5", "2 x 5 = 10", "2 ÷ 5 = 10", "!2 x 5 = 10", "10 = 2 x 5", 4));
        questionsList.add(new QuestionModel("1 + 1 = 2 → 2 ÷ 2 = 1", "True", "", "", "", 1));
        questionsList.add(new QuestionModel("10 > 9 → 9 < 8", "True", "False", "Yes", "No", 2));
        questionsList.add(new QuestionModel("10 < 9 → 9 > 8", "Yes", "No", "True", "False", 3));
        questionsList.add(new QuestionModel("7 < 5 → 4 > 9", "No", "Yes", "False", "True", 4));
        questionsList.add(new QuestionModel("1 + 1 = 2 ↔ 2 ÷ 2 = 1", "True", "False", "Yes", "No", 1));
        questionsList.add(new QuestionModel("10 > 9 ↔ 9 < 8", "True", "False", "Yes", "No", 2));
        questionsList.add(new QuestionModel("10 < 9 ↔ 9 > 8", "No", "Yes", "False", "True", 3));
        questionsList.add(new QuestionModel("7 < 5 ↔ 4 > 9", "No", "Yes", "False", "True", 4));
        // Shuffle list when starting a new game
        Collections.shuffle(questionsList);
    } // end addQuestions
}