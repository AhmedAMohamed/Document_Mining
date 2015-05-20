
public enum FuzzyState {
	NONE(-1),
	LOW(0),
	MEDIUM(1),
	HIGH(2);
	
	
	private int id;
	FuzzyState(int id) {
		this.id = id ;
	}
	
	public int getId(){
		return id;
	}
}
