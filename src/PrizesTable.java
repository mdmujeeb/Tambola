import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;


public class PrizesTable extends JPanel{
	
	private static final long serialVersionUID = 6494425664945467592L;
	
	public static final int ROW_COUNT = 8;
	public static final int COLUMN_COUNT = 4;
	List<Prize> prizes;
	MyJLabel[][] labels = new MyJLabel[ROW_COUNT][COLUMN_COUNT];
	
	public PrizesTable(List<Prize> prizes) {
		this.prizes = prizes;
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		labels[0][0] = new MyJLabel("Prize Name", false);
		labels[0][1] = new MyJLabel("Prize 1", false);
		labels[0][2] = new MyJLabel("Prize 2", false);
		labels[0][3] = new MyJLabel("Prize 3", false);
		
		for(int i=0; i<ROW_COUNT-1; i++) {
			labels[i+1][0] = new MyJLabel(PrizesDialog.LABELS[i], false);
			labels[i+1][1] = new MyJLabel("00", true);
			labels[i+1][2] = new MyJLabel("00", true);
			labels[i+1][3] = new MyJLabel("00", true);
		}
		setPreferredSize(new Dimension(140,140));
		setMinimumSize(new Dimension(140,140));
		setMaximumSize(new Dimension(140,140));
		
		GridBagConstraints cons = new GridBagConstraints();
		for(int i=0; i<ROW_COUNT; i++) {
			for(int j=0; j<COLUMN_COUNT; j++) {
				labels[i][j].setFont(new Font("Arial",30,30));
				labels[i][j].setBorder(new EtchedBorder());
				cons.gridx = j;
				cons.gridy = i;
				layout.setConstraints(labels[i][j], cons);
				add(labels[i][j]);
				if(j>0) {
					labels[i][j].setPreferredSize(new Dimension(70,40));
					labels[i][j].setMinimumSize(new Dimension(70,40));
					labels[i][j].setMaximumSize(new Dimension(70,40));
				} else {
					labels[i][j].setPreferredSize(new Dimension(170,40));
					labels[i][j].setMinimumSize(new Dimension(170,40));
					labels[i][j].setMaximumSize(new Dimension(170,40));
				}
			}
		}
		
		labels[0][0].setFont(new Font("Arial",15,15));
		labels[0][1].setFont(new Font("Arial",15,15));
		labels[0][2].setFont(new Font("Arial",15,15));
		labels[0][3].setFont(new Font("Arial",15,15));
	}
	
	public void updatePrizes(List<Prize> prizes) {
		this.prizes = prizes;
		for(int i=0; i<prizes.size(); i++) {
			Prize prize = prizes.get(i);
			if(prize.getAmount1() == 0) {
				labels[i+1][1].prizeGone();
			} else {
				labels[i+1][1].setText("" + prize.getAmount1());
			}
			if(prize.getAmount2() == 0) {
				labels[i+1][2].prizeGone();
			} else {
				labels[i+1][2].setText("" + prize.getAmount2());
			}
			if(prize.getAmount3() == 0) {
				labels[i+1][3].prizeGone();
			} else {
				labels[i+1][3].setText("" + prize.getAmount3());
			}
		}
	}
	
	public void updatePrizes(Prize prize) {
		int index = getIndexOfPrize(prize);
		if(prize.getAmount1() == 0) {
			labels[index+1][1].prizeGone();
		}
		if(prize.getAmount2() == 0) {
			labels[index+1][2].prizeGone();
		}
		if(prize.getAmount3() == 0) {
			labels[index+1][3].prizeGone();
		}
	}
	
	private int getIndexOfPrize(Prize prize) {
		for(int i=0; i<prizes.size(); i++) {
			Prize localPrize = prizes.get(i);
			if(localPrize.getNameIndex() == prize.getNameIndex()) {
				return i; 
			}
		}
		return 0;
	}
	
	public void reset() {
		for(int i=0; i<ROW_COUNT; i++) {
			for(int j=0; j<COLUMN_COUNT; j++) {
				labels[i][j].prizeRestored();
			}
		}
	}
}
