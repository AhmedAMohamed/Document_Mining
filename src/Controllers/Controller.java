package Controllers;
import java.io.File;

import Model.Model;

public class Controller {
    private Model model;
    public double thTfidf;
    public int wordNetLevel;
    public Controller(double thHold, int levels){
        model = new Model();
        thTfidf = thHold;
        wordNetLevel = levels;
    }

    public void setFile(File[] files)
    {
        model.setSelectedFile(files);
    }

    public void setOutPutDir(String dir)
    {
        model.setOutputDirectory(dir);
    }
    public void startPreprocessing(){
    	System.out.println("start");
        model.preprocessData(thTfidf,wordNetLevel);
        System.out.println("end");
    }
}