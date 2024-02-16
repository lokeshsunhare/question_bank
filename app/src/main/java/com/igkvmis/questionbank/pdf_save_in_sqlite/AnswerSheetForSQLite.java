package com.igkvmis.questionbank.pdf_save_in_sqlite;

public class AnswerSheetForSQLite {
    private String Student_Id;
    private String Question_Paper_Id;
    private String Course_Id;
    private byte[] PDF_File;
    private String File_Name;
    private String Live_Answer_Sheet_Id;


    public AnswerSheetForSQLite(String student_Id, String question_Paper_Id) {
        Student_Id = student_Id;
        Question_Paper_Id = question_Paper_Id;
    }

    public AnswerSheetForSQLite(String student_Id, String question_Paper_Id, String course_Id,
                                byte[] PDF_File, String file_Name, String live_Answer_Sheet_Id) {
        Student_Id = student_Id;
        Question_Paper_Id = question_Paper_Id;
        Course_Id = course_Id;
        this.PDF_File = PDF_File;
        File_Name = file_Name;
        Live_Answer_Sheet_Id = live_Answer_Sheet_Id;
    }



    public String getStudent_Id() {
        return Student_Id;
    }

    public void setStudent_Id(String student_Id) {
        Student_Id = student_Id;
    }

    public String getQuestion_Paper_Id() {
        return Question_Paper_Id;
    }

    public void setQuestion_Paper_Id(String question_Paper_Id) {
        Question_Paper_Id = question_Paper_Id;
    }

    public String getCourse_Id() {
        return Course_Id;
    }

    public void setCourse_Id(String course_Id) {
        Course_Id = course_Id;
    }

    public byte[] getPDF_File() {
        return PDF_File;
    }

    public void setPDF_File(byte[] PDF_File) {
        this.PDF_File = PDF_File;
    }

    public String getFile_Name() {
        return File_Name;
    }

    public void setFile_Name(String file_Name) {
        File_Name = file_Name;
    }

    public String getLive_Answer_Sheet_Id() {
        return Live_Answer_Sheet_Id;
    }

    public void setLive_Answer_Sheet_Id(String live_Answer_Sheet_Id) {
        Live_Answer_Sheet_Id = live_Answer_Sheet_Id;
    }
}
