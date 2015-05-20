import Jama.Matrix;

public class Clustering {

	Matrix dtm;
	Matrix tdm;
	Matrix dcm;
	
	public void calculateDcm() {
		dcm = dtm.times(tdm);
	}
	
	
}
