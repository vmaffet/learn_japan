import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class LearnJapan extends JFrame implements MouseListener, MouseWheelListener, KeyListener {
	
	int height, width;
	
	BufferedImage back, jpflag, frflag;
	Graphics2D gBack;
	static JapCharac[] hirag;
	int mode, scroll;
	boolean firstInput;
	int toTranslate;
	int previousAsked;
	int good, all;
	boolean kataInput;
	
	String fontName= "";
	
	String keyboardInput, keyboardInput2;
	ArrayList<MotJap> dico;
	
	Color cblue= new Color(224,101,67);
	Color cred= new Color(245,245,221);
	Color cgreen= new Color(191,207,72);
	Color cgrey= new Color(200,190,170);
	Color csuccess= new Color(102,204,102);
	Color cmistake= new Color(255,51,102);
	
	public LearnJapan () {
		super("Learn Japanese");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 400);
		setMinimumSize(new Dimension(800, 400));
		
		mode= 0;
		scroll= 0;
		keyboardInput= "";
		keyboardInput2= "";
		firstInput= true;
		toTranslate= 0;
		previousAsked= -1;
		good= 1;
		all= 1;
		kataInput= false;
		importHiragana();
		importDictionary();
		try {
			frflag= ImageIO.read(new File("frflag.png"));
			jpflag= ImageIO.read(new File("jpflag.png"));
		} catch (Exception e) {
			System.out.println("fail load img");
		}
		
		setFocusTraversalKeysEnabled(false);
		
		addKeyListener(this);
		addMouseWheelListener(this);
		addMouseListener(this);
		setVisible(true);
		setSize(getWidth()+getInsets().left+getInsets().right, getHeight()+getInsets().top+getInsets().bottom);
	}
	
	public static void main (String[] args) {
		new LearnJapan();
	}
	
	public void paint (Graphics g) {
		scroll+= (getHeight()-getInsets().top-getInsets().bottom-height)>0?(getHeight()-getInsets().top-getInsets().bottom-height):0;
		scroll= scroll>0?0:scroll;
		height= getHeight()-getInsets().top-getInsets().bottom;
		width= getWidth()-getInsets().left-getInsets().right;
		back= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		gBack= (Graphics2D) back.getGraphics();
		
		//Background
		gBack.setColor(cred);
		gBack.fillRect(0, 0, width, height);
		
		//info display
		gBack.setColor(Color.black);
		gBack.setFont(new Font(fontName, Font.PLAIN, 60));
		gBack.setStroke(new BasicStroke(3));
		if (mode == 0) {
			gBack.setColor(Color.black);
			gBack.drawString(dico.get(toTranslate).fr, (width-gBack.getFontMetrics().stringWidth(dico.get(toTranslate).fr))/2, 60+(height-60)/5);
			gBack.drawRect((width-gBack.getFontMetrics().stringWidth(dico.get(toTranslate).fr))/2-5, 10+(height-60)/5, gBack.getFontMetrics().stringWidth(dico.get(toTranslate).fr)+10, 60);
			gBack.drawString(romajiToHirag(keyboardInput), (width-gBack.getFontMetrics().stringWidth(romajiToHirag(keyboardInput)))/2, 60+35+(2*(height-60)/5));
			if (keyboardInput.length() >= 1) {
				gBack.drawLine((width-gBack.getFontMetrics().stringWidth(romajiToHirag(keyboardInput)))/2, 60+40+2*(height-60)/5, (width+gBack.getFontMetrics().stringWidth(romajiToHirag(keyboardInput)))/2, 60+40+2*(height-60)/5);
			} else {
				gBack.drawLine((width-50)/2, 60+40+2*(height-60)/5, (width+50)/2, 60+40+2*(height-60)/5);
			}
			gBack.setColor(cgreen);
			gBack.fillRect((width-gBack.getFontMetrics().stringWidth("verify"))/2, (height-60)*3/5+60, gBack.getFontMetrics().stringWidth("verify"), 35);
			gBack.setColor(Color.black);
			gBack.drawRect((width-gBack.getFontMetrics().stringWidth("verify"))/2, (height-60)*3/5+60, gBack.getFontMetrics().stringWidth("verify"), 35);
			gBack.setFont(new Font(fontName, Font.PLAIN, 30));
			gBack.drawString("verify", (width-gBack.getFontMetrics().stringWidth("verify"))/2, (height-60)*3/5+87);
			gBack.setFont(new Font(fontName, Font.PLAIN, 60));
			if (previousAsked >= 0) {
				if (dico.get(previousAsked).score.charAt(0) == '1') {
					gBack.setColor(csuccess);
				} else {
					gBack.setColor(cmistake);
				}
				gBack.setFont(new Font(fontName, Font.PLAIN, 40));
				gBack.fillRect((width-gBack.getFontMetrics().stringWidth(dico.get(previousAsked).toString()))/2-5, (height-60)*4/5+60, gBack.getFontMetrics().stringWidth(dico.get(previousAsked).toString())+10, 40);
				gBack.setColor(Color.black);
				gBack.drawRect((width-gBack.getFontMetrics().stringWidth(dico.get(previousAsked).toString()))/2-5, (height-60)*4/5+60, gBack.getFontMetrics().stringWidth(dico.get(previousAsked).toString())+10, 40);
				gBack.drawString(dico.get(previousAsked).toString(), (width-gBack.getFontMetrics().stringWidth(dico.get(previousAsked).toString()))/2, (height-60)*4/5+60+34);
				gBack.setFont(new Font(fontName, Font.PLAIN, 60));
			}
			gBack.setFont(new Font(fontName, Font.PLAIN, 20));
			gBack.setColor(cgreen);
			gBack.fillRect(0, 60, gBack.getFontMetrics().stringWidth("Word score : 00000")+10, 30);
			gBack.fillRect(0, 90, gBack.getFontMetrics().stringWidth(String.format("Success : %.2f %%", (double)(good)/all*100))+10, 30);
			gBack.setColor(Color.black);
			gBack.drawRect(0, 60, gBack.getFontMetrics().stringWidth("Word score : 00000")+10, 30);
			gBack.drawRect(0, 90, gBack.getFontMetrics().stringWidth(String.format("Success : %.2f %%", (double)(good)/all*100))+10, 30);
			gBack.drawString("Word score : "+dico.get(toTranslate).score, 5, 60+24);
			gBack.drawString(String.format("Success : %.2f %%", (double)(good)/all*100), 5, 90+24);
			
			//Kata_Hirag switch
			gBack.setFont(new Font(fontName, Font.PLAIN, 30));
			gBack.drawOval(width-40-10, 60+10, 40, 40);
			if (kataInput) {
				gBack.drawString("\u30A2", width-43, 60+10+30);
			} else {
				gBack.drawString("\u3042", width-43, 60+10+30);
			}
			gBack.setFont(new Font(fontName, Font.PLAIN, 60));
		} else if (mode == 1){
			gBack.setColor(Color.black);
			gBack.drawString(dico.get(toTranslate).jp, (width-gBack.getFontMetrics().stringWidth(dico.get(toTranslate).jp))/2, 60+(height-60)/5);
			gBack.drawRect((width-gBack.getFontMetrics().stringWidth(dico.get(toTranslate).jp))/2-5, 10+(height-60)/5, gBack.getFontMetrics().stringWidth(dico.get(toTranslate).jp)+10, 60);
			gBack.drawString(keyboardInput, (width-gBack.getFontMetrics().stringWidth(keyboardInput))/2, 60+35+(2*(height-60)/5));
			if (keyboardInput.length() >= 1) {
				gBack.drawLine((width-gBack.getFontMetrics().stringWidth(keyboardInput))/2, 60+40+2*(height-60)/5, (width+gBack.getFontMetrics().stringWidth(keyboardInput))/2, 60+40+2*(height-60)/5);
			} else {
				gBack.drawLine((width-50)/2, 60+40+2*(height-60)/5, (width+50)/2, 60+40+2*(height-60)/5);
			}
			gBack.setColor(cgreen);
			gBack.fillRect((width-gBack.getFontMetrics().stringWidth("verify"))/2, (height-60)*3/5+60, gBack.getFontMetrics().stringWidth("verify"), 35);
			gBack.setColor(Color.black);
			gBack.drawRect((width-gBack.getFontMetrics().stringWidth("verify"))/2, (height-60)*3/5+60, gBack.getFontMetrics().stringWidth("verify"), 35);
			gBack.setFont(new Font(fontName, Font.PLAIN, 30));
			gBack.drawString("verify", (width-gBack.getFontMetrics().stringWidth("verify"))/2, (height-60)*3/5+87);
			gBack.setFont(new Font(fontName, Font.PLAIN, 60));
			if (previousAsked >= 0) {
				if (dico.get(previousAsked).score.charAt(0) == '1') {
					gBack.setColor(csuccess);
				} else {
					gBack.setColor(cmistake);
				}
				gBack.setFont(new Font(fontName, Font.PLAIN, 40));
				gBack.fillRect((width-gBack.getFontMetrics().stringWidth(dico.get(previousAsked).toString()))/2-5, (height-60)*4/5+60, gBack.getFontMetrics().stringWidth(dico.get(previousAsked).toString())+10, 40);
				gBack.setColor(Color.black);
				gBack.drawRect((width-gBack.getFontMetrics().stringWidth(dico.get(previousAsked).toString()))/2-5, (height-60)*4/5+60, gBack.getFontMetrics().stringWidth(dico.get(previousAsked).toString())+10, 40);
				gBack.drawString(dico.get(previousAsked).toString(), (width-gBack.getFontMetrics().stringWidth(dico.get(previousAsked).toString()))/2, (height-60)*4/5+60+34);
				gBack.setFont(new Font(fontName, Font.PLAIN, 60));
			}
			gBack.setFont(new Font(fontName, Font.PLAIN, 20));
			gBack.setColor(cgreen);
			gBack.fillRect(0, 60, gBack.getFontMetrics().stringWidth("Word score : 00000")+10, 30);
			gBack.fillRect(0, 90, gBack.getFontMetrics().stringWidth(String.format("Success : %.2f %%", (double)(good)/all*100))+10, 30);
			gBack.setColor(Color.black);
			gBack.drawRect(0, 60, gBack.getFontMetrics().stringWidth("Word score : 00000")+10, 30);
			gBack.drawRect(0, 90, gBack.getFontMetrics().stringWidth(String.format("Success : %.2f %%", (double)(good)/all*100))+10, 30);
			gBack.drawString("Word score : "+dico.get(toTranslate).score, 5, 60+24);
			gBack.drawString(String.format("Success : %.2f %%", (double)(good)/all*100), 5, 90+24);
			gBack.setFont(new Font(fontName, Font.PLAIN, 60));
		} else if (mode == 2) {
			//Mots
			for (int j= 0; j<dico.size(); j++) {
				gBack.drawString(dico.get(j).jp, width/11, scroll+105+(1+2*j)*50);
				gBack.drawString(dico.get(j).fr, -gBack.getFontMetrics().stringWidth(dico.get(j).fr)+10*width/11, scroll+105+(1+2*j)*50);
				gBack.drawString("\u02DF", 10*width/11+width/22-gBack.getFontMetrics().stringWidth("\u02DF")/2, scroll+105+18+(1+2*j)*50);
			}
			//Boites
			gBack.setColor(cgrey);
			gBack.fillRect(0, 60, width/2, 30);
			gBack.fillRect(width/2, 60, width/2, 30);
			gBack.setColor(Color.black);
			gBack.drawRect(0, 60, width/2, 30);
			gBack.drawRect(width/2, 60, width/2, 30);
			gBack.setFont(new Font(fontName, Font.PLAIN, 20));
			gBack.drawString("\u25B2", width/4-gBack.getFontMetrics().stringWidth("\u25B2")/2, 85);
			gBack.drawString("\u25B2", 3*width/4-gBack.getFontMetrics().stringWidth("\u25B2")/2, 85);
			gBack.setFont(new Font(fontName, Font.PLAIN, 60));
		} else if (mode == 3) {
			//Jap Input
			if (firstInput) {
				gBack.setColor(cblue);
			} else {
				gBack.setColor(Color.black);
			}
			gBack.drawImage(jpflag, width/11, (height-2*60-60)/3+60, this);
			gBack.drawLine(width/11+ 150, (height-2*60-60)/3+60, width/11+150, (height-2*60-60)/3+60+60);
			gBack.drawString(romajiToHirag(keyboardInput), width/11+155, (height-2*60-60)/3+60+50);
			gBack.drawLine(width/11+150, (height-2*60-60)/3+60+60, width/11+150+gBack.getFontMetrics().stringWidth(romajiToHirag(keyboardInput))+5, (height-2*60-60)/3+60+60);
			
			//Fr input
			if (!firstInput) {
				gBack.setColor(cblue);
			} else {
				gBack.setColor(Color.black);
			}
			gBack.drawImage(frflag, width/11, (2*(height-2*60-60))/3+60+60, this);
			gBack.drawLine(width/11+ 150, (2*(height-2*60-60))/3+60+60, width/11+150, (2*(height-2*60-60))/3+60+60+60);
			gBack.drawString(keyboardInput2, width/11+155, (2*(height-2*60-60))/3+60+60+50);
			gBack.drawLine(width/11+150, (2*(height-2*60-60))/3+60+60+60, width/11+150+gBack.getFontMetrics().stringWidth(keyboardInput2)+5, (2*(height-2*60-60))/3+60+60+60);
			
			//Save button
			gBack.setColor(cgreen);
			gBack.fillRect(10*width/11-20-gBack.getFontMetrics().stringWidth("save"), (height-60+30)/2, 20+gBack.getFontMetrics().stringWidth("save"), 60);
			gBack.setColor(Color.black);
			gBack.drawRect(10*width/11-20-gBack.getFontMetrics().stringWidth("save"), (height-60+30)/2, 20+gBack.getFontMetrics().stringWidth("save"), 60);
			gBack.drawString("save", 10*width/11-10-gBack.getFontMetrics().stringWidth("save"), (height+30+30)/2);
			
			//Kata_Hirag switch
			gBack.setFont(new Font(fontName, Font.PLAIN, 30));
			gBack.drawOval(width-40-10, 60+10, 40, 40);
			if (kataInput) {
				gBack.drawString("\u30A2", width-43, 60+10+30);
			} else {
				gBack.drawString("\u3042", width-43, 60+10+30);
			}
			gBack.setFont(new Font(fontName, Font.PLAIN, 60));
		}
		
		//Top boxes
		gBack.setColor(cblue);
		gBack.fillRect(0, 0, width/4, 60);
		gBack.fillRect(width/2, 0, width/4, 60);
		gBack.setColor(cgreen);
		gBack.fillRect(width/4, 0, width/4, 60);
		gBack.fillRect((3*width)/4, 0, width/4, 60);
		gBack.setColor(Color.black);
		gBack.drawRect(0, 0, width/4, 60);
		gBack.drawRect(width/4, 0, width/4, 60);
		gBack.drawRect(width/2, 0, width/4, 60);
		gBack.drawRect((3*width)/4, 0, width/4, 60);
		gBack.setFont(new Font(fontName, Font.PLAIN, 18));
		gBack.drawString("Fr to Jap", 0+width/8-gBack.getFontMetrics().stringWidth("Fr to Jap")/2, 30);
		gBack.drawString("Jap to Fr", width/4+width/8-gBack.getFontMetrics().stringWidth("Jap to Fr")/2, 30);
		gBack.drawString("Dictionary", width/2+width/8-gBack.getFontMetrics().stringWidth("Dictionary")/2, 30);
		gBack.drawString("Add a word", (3*width)/4+width/8-gBack.getFontMetrics().stringWidth("Add a word")/2, 30);
		
		g.drawImage(back, getInsets().left, getInsets().top, this);
	}
	
	public void importHiragana () {
		hirag= new JapCharac[229];
		hirag[0]= new JapCharac("\u3042", "A");
		hirag[1]= new JapCharac("\u3044", "I");
		hirag[2]= new JapCharac("\u3046", "U");
		hirag[3]= new JapCharac("\u3048", "E");
		hirag[4]= new JapCharac("\u304A", "O");
		hirag[5]= new JapCharac("\u304B", "KA");
		hirag[6]= new JapCharac("\u304C", "GA");
		hirag[7]= new JapCharac("\u304D", "KI");
		hirag[8]= new JapCharac("\u304E", "GI");
		hirag[9]= new JapCharac("\u304F", "KU");
		hirag[10]= new JapCharac("\u3050", "GU");
		hirag[11]= new JapCharac("\u3051", "KE");
		hirag[12]= new JapCharac("\u3052", "GE");
		hirag[13]= new JapCharac("\u3053", "KO");
		hirag[14]= new JapCharac("\u3054", "GO");
		hirag[15]= new JapCharac("\u3055", "SA");
		hirag[16]= new JapCharac("\u3056", "ZA");
		hirag[17]= new JapCharac("\u3057", "SHI");
		hirag[18]= new JapCharac("\u3058", "JI");
		hirag[19]= new JapCharac("\u3059", "SU");
		hirag[20]= new JapCharac("\u305A", "ZU");
		hirag[21]= new JapCharac("\u305B", "SE");
		hirag[22]= new JapCharac("\u305C", "ZE");
		hirag[23]= new JapCharac("\u305D", "SO");
		hirag[24]= new JapCharac("\u305E", "ZO");
		hirag[25]= new JapCharac("\u305F", "TA");
		hirag[26]= new JapCharac("\u3060", "DA");
		hirag[27]= new JapCharac("\u3061", "CHI");
		hirag[28]= new JapCharac("\u3062", "CJI");
		hirag[29]= new JapCharac("\u3063", "STSU");
		hirag[30]= new JapCharac("\u3064", "TSU");
		hirag[31]= new JapCharac("\u3065", "DZU");
		hirag[32]= new JapCharac("\u3066", "TE");
		hirag[33]= new JapCharac("\u3067", "DE");
		hirag[34]= new JapCharac("\u3068", "TO");
		hirag[35]= new JapCharac("\u3069", "DO");
		hirag[36]= new JapCharac("\u306A", "NA");
		hirag[37]= new JapCharac("\u306B", "NI");
		hirag[38]= new JapCharac("\u306C", "NU");
		hirag[39]= new JapCharac("\u306D", "NE");
		hirag[40]= new JapCharac("\u306E", "NO");
		hirag[41]= new JapCharac("\u306F", "HA");
		hirag[42]= new JapCharac("\u3070", "BA");
		hirag[43]= new JapCharac("\u3071", "PA");
		hirag[44]= new JapCharac("\u3072", "HI");
		hirag[45]= new JapCharac("\u3073", "BI");
		hirag[46]= new JapCharac("\u3074", "PI");
		hirag[47]= new JapCharac("\u3075", "FU");
		hirag[48]= new JapCharac("\u3076", "BU");
		hirag[49]= new JapCharac("\u3077", "PU");
		hirag[50]= new JapCharac("\u3078", "HE");
		hirag[51]= new JapCharac("\u3079", "BE");
		hirag[52]= new JapCharac("\u307A", "PE");
		hirag[53]= new JapCharac("\u307B", "HO");
		hirag[54]= new JapCharac("\u307C", "BO");
		hirag[55]= new JapCharac("\u307D", "PO");
		hirag[56]= new JapCharac("\u307E", "MA");
		hirag[57]= new JapCharac("\u307F", "MI");
		hirag[58]= new JapCharac("\u3080", "MU");
		hirag[59]= new JapCharac("\u3081", "ME");
		hirag[60]= new JapCharac("\u3082", "MO");
		hirag[61]= new JapCharac("\u3084", "YA");
		hirag[62]= new JapCharac("\u3086", "YU");
		hirag[63]= new JapCharac("\u3088", "YO");
		hirag[64]= new JapCharac("\u3089", "RA");
		hirag[65]= new JapCharac("\u308A", "RI");
		hirag[66]= new JapCharac("\u308B", "RU");
		hirag[67]= new JapCharac("\u308C", "RE");
		hirag[68]= new JapCharac("\u308D", "RO");
		hirag[69]= new JapCharac("\u308F", "WA");
		hirag[70]= new JapCharac("\u3092", "WO");
		hirag[71]= new JapCharac("\u3093", "N");
		hirag[72]= new JapCharac("\u304D\u3083", "KYA");
		hirag[73]= new JapCharac("\u304D\u3085", "KYU");
		hirag[74]= new JapCharac("\u304D\u3087", "KYO");
		hirag[75]= new JapCharac("\u3057\u3083", "SHA");
		hirag[76]= new JapCharac("\u3057\u3085", "SHU");
		hirag[77]= new JapCharac("\u3057\u3087", "SHO");
		hirag[78]= new JapCharac("\u3061\u3083", "CHA");
		hirag[79]= new JapCharac("\u3061\u3085", "CHU");
		hirag[80]= new JapCharac("\u3061\u3087", "CHO");
		hirag[81]= new JapCharac("\u306B\u3083", "NYA");
		hirag[82]= new JapCharac("\u306B\u3085", "NYU");
		hirag[83]= new JapCharac("\u306B\u3087", "NYO");
		hirag[84]= new JapCharac("\u3072\u3083", "HYA");
		hirag[85]= new JapCharac("\u3072\u3085", "HYU");
		hirag[86]= new JapCharac("\u3072\u3087", "HYO");
		hirag[87]= new JapCharac("\u307F\u3083", "MYA");
		hirag[88]= new JapCharac("\u307F\u3085", "MYU");
		hirag[89]= new JapCharac("\u307F\u3087", "MYO");
		hirag[90]= new JapCharac("\u308A\u3083", "RYA");
		hirag[91]= new JapCharac("\u308A\u3085", "RYU");
		hirag[92]= new JapCharac("\u308A\u3087", "RYO");
		hirag[93]= new JapCharac("\u304E\u3083", "GYA");
		hirag[94]= new JapCharac("\u304E\u3085", "GYU");
		hirag[95]= new JapCharac("\u304E\u3087", "GYO");
		hirag[96]= new JapCharac("\u3058\u3083", "JA");
		hirag[97]= new JapCharac("\u3058\u3085", "JU");
		hirag[98]= new JapCharac("\u3058\u3087", "JO");
		hirag[99]= new JapCharac("\u3073\u3083", "BYA");
		hirag[100]= new JapCharac("\u3073\u3085", "BYU");
		hirag[101]= new JapCharac("\u3073\u3087", "BYO");
		hirag[102]= new JapCharac("\u3074\u3083", "PYA");
		hirag[103]= new JapCharac("\u3074\u3085", "PYU");
		hirag[104]= new JapCharac("\u3074\u3087", "PYO");
		hirag[105]= new JapCharac("\u30A2", "A#");
		hirag[106]= new JapCharac("\u30A4", "I#");
		hirag[107]= new JapCharac("\u30A6", "U#");
		hirag[108]= new JapCharac("\u30A8", "E#");
		hirag[109]= new JapCharac("\u30AA", "O#");
		hirag[110]= new JapCharac("\u30AB", "KA#");
		hirag[111]= new JapCharac("\u30AC", "GA#");
		hirag[112]= new JapCharac("\u30AD", "KI#");
		hirag[113]= new JapCharac("\u30AE", "GI#");
		hirag[114]= new JapCharac("\u30AF", "KU#");
		hirag[115]= new JapCharac("\u30B0", "GU#");
		hirag[116]= new JapCharac("\u30B1", "KE#");
		hirag[117]= new JapCharac("\u30B2", "GE#");
		hirag[118]= new JapCharac("\u30B3", "KO#");
		hirag[119]= new JapCharac("\u30B4", "GO#");
		hirag[120]= new JapCharac("\u30B5", "SA#");
		hirag[121]= new JapCharac("\u30B6", "ZA#");
		hirag[122]= new JapCharac("\u30B7", "SHI#");
		hirag[123]= new JapCharac("\u30B8", "JI#");
		hirag[124]= new JapCharac("\u30B9", "SU#");
		hirag[125]= new JapCharac("\u30BA", "ZU#");
		hirag[126]= new JapCharac("\u30BB", "SE#");
		hirag[127]= new JapCharac("\u30BC", "ZE#");
		hirag[128]= new JapCharac("\u30BD", "SO#");
		hirag[129]= new JapCharac("\u30BE", "ZO#");
		hirag[130]= new JapCharac("\u30BF", "TA#");
		hirag[131]= new JapCharac("\u30C0", "DA#");
		hirag[132]= new JapCharac("\u30C1", "CHI#");
		hirag[133]= new JapCharac("\u30C2", "CJI#");
		hirag[134]= new JapCharac("\u30C3", "STSU#");
		hirag[135]= new JapCharac("\u30C4", "TSU#");
		hirag[136]= new JapCharac("\u30C5", "DZU#");
		hirag[137]= new JapCharac("\u30C6", "TE#");
		hirag[138]= new JapCharac("\u30C7", "DE#");
		hirag[139]= new JapCharac("\u30C8", "TO#");
		hirag[140]= new JapCharac("\u30C9", "DO#");
		hirag[141]= new JapCharac("\u30CA", "NA#");
		hirag[142]= new JapCharac("\u30CB", "NI#");
		hirag[143]= new JapCharac("\u30CC", "NU#");
		hirag[144]= new JapCharac("\u30CD", "NE#");
		hirag[145]= new JapCharac("\u30CE", "NO#");
		hirag[146]= new JapCharac("\u30CF", "HA#");
		hirag[147]= new JapCharac("\u30D0", "BA#");
		hirag[148]= new JapCharac("\u30D1", "PA#");
		hirag[149]= new JapCharac("\u30D2", "HI#");
		hirag[150]= new JapCharac("\u30D3", "BI#");
		hirag[151]= new JapCharac("\u30D4", "PI#");
		hirag[152]= new JapCharac("\u30D5", "FU#");
		hirag[153]= new JapCharac("\u30D6", "BU#");
		hirag[154]= new JapCharac("\u30D7", "PU#");
		hirag[155]= new JapCharac("\u30D8", "HE#");
		hirag[156]= new JapCharac("\u30D9", "BE#");
		hirag[157]= new JapCharac("\u30DA", "PE#");
		hirag[158]= new JapCharac("\u30DB", "HO#");
		hirag[159]= new JapCharac("\u30DC", "BO#");
		hirag[160]= new JapCharac("\u30DD", "PO#");
		hirag[161]= new JapCharac("\u30DE", "MA#");
		hirag[162]= new JapCharac("\u30DF", "MI#");
		hirag[163]= new JapCharac("\u30E0", "MU#");
		hirag[164]= new JapCharac("\u30E1", "ME#");
		hirag[165]= new JapCharac("\u30E2", "MO#");
		hirag[166]= new JapCharac("\u30E4", "YA#");
		hirag[167]= new JapCharac("\u30E6", "YU#");
		hirag[168]= new JapCharac("\u30E8", "YO#");
		hirag[169]= new JapCharac("\u30E9", "RA#");
		hirag[170]= new JapCharac("\u30EA", "RI#");
		hirag[171]= new JapCharac("\u30EB", "RU#");
		hirag[172]= new JapCharac("\u30EC", "RE#");
		hirag[173]= new JapCharac("\u30ED", "RO#");
		hirag[174]= new JapCharac("\u30EF", "WA#");
		hirag[175]= new JapCharac("\u30F2", "WO#");
		hirag[176]= new JapCharac("\u30F3", "N#");
		hirag[177]= new JapCharac("\u30AD\u30E3", "KYA#");
		hirag[178]= new JapCharac("\u30AD\u30E5", "KYU#");
		hirag[179]= new JapCharac("\u30AD\u30E7", "KYO#");
		hirag[180]= new JapCharac("\u30B7\u30E3", "SHA#");
		hirag[181]= new JapCharac("\u30B7\u30E5", "SHU#");
		hirag[182]= new JapCharac("\u30B7\u30E7", "SHO#");
		hirag[183]= new JapCharac("\u30C1\u30E3", "CHA#");
		hirag[184]= new JapCharac("\u30C1\u30E5", "CHU#");
		hirag[185]= new JapCharac("\u30C1\u30E7", "CHO#");
		hirag[186]= new JapCharac("\u30CB\u30E3", "NYA#");
		hirag[187]= new JapCharac("\u30CB\u30E5", "NYU#");
		hirag[188]= new JapCharac("\u30CB\u30E7", "NYO#");
		hirag[189]= new JapCharac("\u30D2\u30E3", "HYA#");
		hirag[190]= new JapCharac("\u30D2\u30E5", "HYU#");
		hirag[191]= new JapCharac("\u30D2\u30E7", "HYO#");
		hirag[192]= new JapCharac("\u30DF\u30E3", "MYA#");
		hirag[193]= new JapCharac("\u30DF\u30E5", "MYU#");
		hirag[194]= new JapCharac("\u30DF\u30E7", "MYO#");
		hirag[195]= new JapCharac("\u30EA\u30E3", "RYA#");
		hirag[196]= new JapCharac("\u30EA\u30E5", "RYU#");
		hirag[197]= new JapCharac("\u30EA\u30E7", "RYO#");
		hirag[198]= new JapCharac("\u30AE\u30E3", "GYA#");
		hirag[199]= new JapCharac("\u30AE\u30E5", "GYU#");
		hirag[200]= new JapCharac("\u30AE\u30E7", "GYO#");
		hirag[201]= new JapCharac("\u30B8\u30E3", "JA#");
		hirag[202]= new JapCharac("\u30B8\u30E5", "JU#");
		hirag[203]= new JapCharac("\u30B8\u30E7", "JO#");
		hirag[204]= new JapCharac("\u30D3\u30E3", "BYA#");
		hirag[205]= new JapCharac("\u30D3\u30E5", "BYU#");
		hirag[206]= new JapCharac("\u30D3\u30E7", "BYO#");
		hirag[207]= new JapCharac("\u30D4\u30E3", "PYA#");
		hirag[208]= new JapCharac("\u30D4\u30E5", "PYU#");
		hirag[209]= new JapCharac("\u30D4\u30E7", "PYO#");///
		hirag[210]= new JapCharac("\u30A6\u30A3", "WI#");
		hirag[211]= new JapCharac("\u30A6\u30A7", "WE#");
		hirag[212]= new JapCharac("\u30A6\u30A9", "WO#");
		hirag[213]= new JapCharac("\u30B7\u30A7", "SHE#");
		hirag[214]= new JapCharac("\u30C1\u30A7", "CHE#");
		hirag[215]= new JapCharac("\u30C4\u30A7", "TSE#");
		hirag[216]= new JapCharac("\u30C4\u30A9", "TSO#");
		hirag[217]= new JapCharac("\u30C4\u30A1", "TSA#");
		hirag[218]= new JapCharac("\u30C6\u30A3", "TI#");
		hirag[219]= new JapCharac("\u30C8\u30A5", "TU#");
		hirag[220]= new JapCharac("\u30D5\u30A1", "FA#");
		hirag[221]= new JapCharac("\u30D5\u30A3", "FI#");
		hirag[222]= new JapCharac("\u30D5\u30A7", "FE#");
		hirag[223]= new JapCharac("\u30D5\u30A9", "FO#");
		hirag[224]= new JapCharac("\u30B8\u30A7", "JE#");
		hirag[225]= new JapCharac("\u30C7\u30A3", "DI#");
		hirag[226]= new JapCharac("\u30C9\u30A5", "DU#");
		hirag[227]= new JapCharac("\u30C7\u30E5", "DYU#");
		hirag[228]= new JapCharac("\u30FC", "-#");
	}
	
	public static String romajiToHirag (String input) {
		input= input.toUpperCase();
		String[] chars= input.split(" ");
		String out= "";
		for (int i= 0; i<chars.length; i++) {
			out+= findHirag(chars[i]);
		}
		return out;
	}
	
	public static String findHirag (String roma) {
		for (int i= 0; i<hirag.length; i++) {
			if (roma.equals(hirag[i].romaji)) {
				return hirag[i].drawing;
			}
		}
		return roma;
	}
	
	public void importDictionary () {
		dico= new ArrayList<MotJap>();
		try {

			Scanner sc = new Scanner(new File("dico.txt"));

			while (sc.hasNextLine()) {
				dico.add(new MotJap(sc.nextLine()));
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Problem load dico");
			return;
		}
	}
	
	public void addWord () {
		dico.add(new MotJap(String.format("%s:%s", keyboardInput, keyboardInput2)));
		updateDicoFile();
		keyboardInput= "";
		keyboardInput2= "";
		firstInput= true;
		importDictionary();
		repaint();
	}
	
	public void updateDicoFile () {
		try {
			FileWriter fw= new FileWriter(new File("dico.txt"));
			
			String s= "";
			
			for (int i= 0; i<dico.size()-1; i++) {
				s+= String.format("%s:%s\n", dico.get(i).jpr, dico.get(i).fr);
			}
			s+= String.format("%s:%s", dico.get(dico.size()-1).jpr, dico.get(dico.size()-1).fr);
			fw.write(s);
			 
			fw.close();
		} catch (Exception e) {
			System.out.println("write issue");
			e.printStackTrace();
		}
	}
	
	public void randomPickWord () {
		int r= (int)(62*Math.random());
		if (r <= 31) {
			r= 0;
		} else if (r <= 47) {
			r= 1;
		} else if (r <= 55) {
			r= 2;
		} else if (r <= 59) {
			r= 3;
		} else if (r <= 61){
			r= 4;
		} else {
			r= 5;
		}
		int n= dico.size();
		int i= (int)(n*Math.random());
		do {
			if (i != previousAsked && dico.get(i).getScore() == r) {
				toTranslate= i;
				return;
			}
			i++;
			n--;
			if (i >= dico.size()) {
				i= 0;
			}
		} while (n >= 0);
		randomPickWord();
	}
	
	public void verifyMode0Input () {
		if (romajiToHirag(keyboardInput).equals(dico.get(toTranslate).jp)) {
			dico.get(toTranslate).setScore(true);
			if (previousAsked != -1) {
				good++;
			}
		} else {
			dico.get(toTranslate).setScore(false);
			if (previousAsked == -1) {
				good= 0;
			}
		}
		if (previousAsked != -1) {
			all++;
		}
		previousAsked= toTranslate;
		keyboardInput="";
		randomPickWord();
	}
	
	public void verifyMode1Input () {
		if (keyboardInput.equals(dico.get(toTranslate).fr)) {
			dico.get(toTranslate).setScore(true);
			if (previousAsked != -1) {
				good++;
			}
		} else {
			dico.get(toTranslate).setScore(false);
			if (previousAsked == -1) {
				good= 0;
			}
		}
		if (previousAsked != -1) {
			all++;
		}
		previousAsked= toTranslate;
		keyboardInput="";
		randomPickWord();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent e) {
		int x= e.getX()-getInsets().left;
		int y= e.getY()-getInsets().top;
		
		if (y < 60) {
			if (x < width/4) {
				mode= 0;
				keyboardInput= "";
				randomPickWord();
				previousAsked= -1;
				good= 1;
				all= 1;
				firstInput= true;
			} else if (x < width/2) {
				mode= 1;
				keyboardInput= "";
				randomPickWord();
				previousAsked= -1;
				good= 1;
				all= 1;
				firstInput= true;
			} else if (x < (3*width)/4) {
				scroll= 0;
				mode= 2;
			} else {
				mode= 3;
				keyboardInput= "";
				keyboardInput2= "";
				firstInput= true;
			}
		}
		if (mode == 0) {
			if (y >= (height-60)*3/5+60 && y <= (height-60)*3/5+60+35 && x >= (width-143)/2 && x <= (width+143)/2) {
				verifyMode0Input();
			}
			if (Math.sqrt(Math.pow((x-width+30), 2)+Math.pow((y-90), 2)) <= 20) {
				kataInput= !kataInput;
			}
		} else if (mode == 1) {
			if (y >= (height-60)*3/5+60 && y <= (height-60)*3/5+60+35 && x >= (width-143)/2 && x <= (width+143)/2) {
				verifyMode1Input();
			}
		} else if (mode == 2) {
			if(y >= 60 && y <= 90) {
				Quick q= new Quick();
				MotJap[] mots= dico.toArray(new MotJap[dico.size()]);
				int[] rank= new int[mots.length];
				if (x < width/2) {
					for (int i= 0; i<mots.length; i++) {
						rank[i]= mots[i].jp.length();
					}
				} else {
					String s;
					for (int i= 0; i<mots.length; i++) {
						s= Normalizer.normalize(mots[i].fr.toUpperCase(), Normalizer.Form.NFD);
						s= s.replaceAll("[^\\p{ASCII}]", "");
						switch (s.length()) {
							case 0:
								rank[i]= 0;
								break;
							case 1:
								rank[i]= (int)(s.charAt(0))*10000;
								break;
							case 2:
								rank[i]= (int)(s.charAt(1))*100+(int)(s.charAt(0))*10000;
								break;
							default:
								rank[i]= (int)(s.charAt(2))+(int)(s.charAt(1))*100+(int)(s.charAt(0))*10000;
								break;
						}
					}
				}
				mots= q.quickSortThat(mots, rank);
				dico= new ArrayList<MotJap>(Arrays.asList(mots));
			}
			if (x >= 10*width/11+width/22-gBack.getFontMetrics().stringWidth("\u02DF")/2 && x <= 10*width/11+width/22+gBack.getFontMetrics().stringWidth("\u02DF")/2) {
				double sel= (double)(y-scroll-90)/100-0.5;
				if (Math.abs(sel-(int)sel) <= 0.1 ) {
					dico.remove((int)sel);
					updateDicoFile();
				}
			}
		} else if (mode == 3) {
			if (x >= 10*width/11-20-127 && x <= 10*width/11 && y >= (height-60+30)/2 && y <= (height+30+30)/2) {
				addWord();
				return;
			}
			if (y >= (height-2*60-60)/3+60 && y <= (height-2*60-60)/3+60+60) {
				firstInput= true;
			} else if (y >= (2*(height-2*60-60))/3+60+60 && y <= (2*(height-2*60-60))/3+60+60+60) {
				firstInput= false;
			}
			if (Math.sqrt(Math.pow((x-width+30), 2)+Math.pow((y-90), 2)) <= 20) {
				kataInput= !kataInput;
			}
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		int d= arg0.getWheelRotation();
		int k= 35;
		do {
			if (scroll-k*d < 0 && scroll-k*d > height-30-50*(2*dico.size()+1)) {
				scroll-= k*d;
				repaint();
				return;
			} else {
				k--;
			}
		} while (k > 0);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			case KeyEvent.VK_BACK_SPACE:
				if (firstInput) {
					keyboardInput= keyboardInput.substring(0, keyboardInput.length()-1);
				} else {
					keyboardInput2= keyboardInput2.substring(0, keyboardInput2.length()-1);
				}
				break;
			case KeyEvent.VK_ENTER:
				if (mode == 0) {
					verifyMode0Input();
				} else if (mode == 1) {
					verifyMode1Input();
				} else if (mode == 3) {
					addWord();
				}
				break;
			case KeyEvent.VK_TAB:
				if (mode == 3) {
					firstInput= !firstInput;
				}
				break;
			case KeyEvent.VK_F1:
				if (mode == 0 || mode == 3) {
					kataInput= !kataInput;
				}
				break;
			case KeyEvent.VK_SHIFT:
				randomPickWord();
				break;
			case KeyEvent.VK_SPACE:
				if ((mode == 0 || mode == 3 ) && kataInput) {
					if (firstInput) {
						keyboardInput+= "#"+e.getKeyChar();
					} else {
						keyboardInput2+= e.getKeyChar();
					}
				} else {
					if (firstInput) {
						keyboardInput+= e.getKeyChar();
					} else {
						keyboardInput2+= e.getKeyChar();
					}
				}
				break;
			default:
				if (firstInput) {
					keyboardInput+= e.getKeyChar();
				} else {
					keyboardInput2+= e.getKeyChar();
				}
				break;
		}
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
}
