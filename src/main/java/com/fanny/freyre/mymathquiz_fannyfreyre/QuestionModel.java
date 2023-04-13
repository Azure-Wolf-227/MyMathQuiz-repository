package com.fanny.freyre.mymathquiz_fannyfreyre;
// Method on how the questions are displayed
public class QuestionModel {
    // constants
    private String question, option1, option2, option3, option4;
    private int correctAnsNo;


    // The question and options are strings, the answer number is int
    public QuestionModel(String question, String option1, String option2, String option3, String option4, int correctAnsNo) {
        this.question = question; // question
        this.option1 = option1; // Option 1
        this.option2 = option2; // Option 2
        this.option3 = option3; // Option 3
        this.option4 = option4; // Option 4
        this.correctAnsNo = correctAnsNo; // Correct Answer Number
    }

    // get question
    public String getQuestion() {
        return question;
    }

    //set question
    public void setQuestion(String question) {
        this.question = question;
    }

    // get Option 1
    public String getOption1() {
        return option1;
    }

    // set Option 1
    public void setOption1(String option1) {
        this.option1 = option1;
    }

    // get Option 2
    public String getOption2() {
        return option2;
    }

    // set Option 2
    public void setOption2(String option2) {
        this.option2 = option2;
    }

    // get Option 3
    public String getOption3() {
        return option3;
    }

    // set Option 3
    public void setOption3(String option3) {
        this.option3 = option3;
    }

    // get Option 4
    public String getOption4() {
        return option4;
    }

    // set Option 4
    public void setOption4(String option4) {
        this.option4 = option4;
    }

    // get correct answer number
    public int getCorrectAnsNo() {
        return correctAnsNo;
    }

    // set correct answer number
    public void setCorrectAnsNo(int correctAnsNo) {
        this.correctAnsNo = correctAnsNo;
    }
}
