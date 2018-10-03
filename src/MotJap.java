
public class MotJap {

	String jp;
	String jpr;
	String fr;
	String score;
	
	public MotJap (String s) {
		
		jpr= s.substring(0, s.indexOf(':'));
		jp= LearnJapan.romajiToHirag(jpr);
		fr= s.substring(s.indexOf(':')+1);
		score= "00000";
	}
	
	public void setScore (boolean success) {
		if (success) {
			score= "1"+score.substring(0, score.length()-1);
		} else {
			score= "0"+score.substring(0, score.length()-1);
		}
	}
	
	public int getScore () {
		int count= 0;
		for (int i= 0; i<score.length(); i++) {
			if (score.charAt(i) == '1') {
				count++;
			}
		}
		return count;
	}
	
	public String toString () {
		return jp+" = "+fr;
	}
	
}
