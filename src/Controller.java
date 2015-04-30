import java.io.File;

public class Controller {
    private Model model;
    public Controller(){
        model = new Model();
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
        model.preprocessData();
    }


}
