
public class Quick {
	
	Data[] base;
	
	public Quick () {
	}
	
	public <T> T[] quickSortThat (T[] labels, int[] values) {
		base= new Data[labels.length];
		for (int i= 0; i<labels.length; i++) {
			base[i]= new Data(i, values[i]);
		}
		splitCompareGeneric(0,base.length);
		T[] result= labels.clone();
		for (int j= 0; j<labels.length; j++) {
			result[j]= labels[base[j].index];
		}
		return result;
	}
	
	
	public void splitCompareGeneric (int start, int end) {
		if (end-start < 2) {
			return;
		}
		int limit= start+1;
		Data temp;
		for (int i= start+1; i<end; i++) {
			if (base[i].value < base[start].value) {
				temp= base[i].clone();
				base[i]= base[limit].clone();
				base[limit]= temp.clone();
				limit++;
			}
		}
		temp= base[start].clone();
		base[start]= base[limit-1].clone();
		base[limit-1]= temp.clone();
		splitCompareGeneric(start, limit-1);
		splitCompareGeneric(limit, end);
	}

}
