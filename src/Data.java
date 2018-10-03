public class Data {
	
	int index;
	int value;
	
	public Data (int at, int weight) {
		index= at;
		value= weight;
	}
	
	public Data clone () {
		return new Data (index, value);
	}
	
	public String toString () {
		return String.format("index : %d value : %d", index, value);
	}
	
}
