package Controllers;
import java.io.File;

import Model.Model;

public class Controller {
    private Model model;
    public double thTfidf;
    public Controller(double thHold){
        model = new Model();
        thTfidf = thHold;
    }

    public void setFile(File[] files)
    {
        model.setSelectedFile(files);
    }

    public void setOutPutDir(String dir)
    {
        model.setOutputDirectory(dir);
    }
    public void startPreprocessing(double thHold){
        model.preprocessData(thHold);
    }
}