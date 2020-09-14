import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PrizesDialog extends JDialog {
	
	private static final long serialVersionUID = -1782322382479133971L;
	public static final int FULL_HOUSE_INDEX = 0;
	public static final int TOP_ROW_INDEX = 1;
	public static final int MIDDLE_ROW_INDEX = 2;
	public static final int BOTTOM_ROW_INDEX = 3;
	public static final int CORNER_INDEX = 4;
	public static final int STAR_INDEX = 5;
	public static final int JALDI_5_INDEX = 6;
	
	public static final String[] LABELS = {"Full House","Top Row","Middle Row","Bottom Row","Corner","Star","Jaldi 5"};
	
	private JLabel[] labels = new JLabel[LABELS.length];
	private JTextField[][] amounts = new JTextField[LABELS.length][3];
	
	private JButton btnSave = new JButton(" Save ");
	private JButton btnReset = new JButton(" Reset ");
	
	private List<Prize> prizes;
	private ActionCallback<List<Prize>> callback;
	
	public PrizesDialog(ActionCallback<List<Prize>> callback, List<Prize> prizes) {
		
		this.callback = callback;
		this.prizes = prizes;
		setTitle("Prizes Setup");
		setSize(450,300);
		
		for(int i=0; i<LABELS.length; i++) {
			labels[i] = new JLabel("   " + LABELS[i]);
			amounts[i][0] = new JTextField(5);
			amounts[i][1] = new JTextField(5);
			amounts[i][2] = new JTextField(5);
		}
		
		if(prizes != null && !prizes.isEmpty()) {
			for(int i=0; i<prizes.size();i++) {
				Prize prize = prizes.get(i);
				amounts[i][0].setText("" + prize.getAmount1());
				amounts[i][1].setText("" + prize.getAmount2());
				amounts[i][2].setText("" + prize.getAmount3());
			}
		}
		
		createMainPanel();
		createListeners();
	}

	private void createListeners() {
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				for(int i=0; i<LABELS.length; i++) {
					amounts[i][0].setText("");
					amounts[i][1].setText("");
					amounts[i][2].setText("");
				}
			}
		});
		
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
				prizes = new ArrayList<Prize>();
				for(int i=0; i<LABELS.length; i++) {
					String amount1 = amounts[i][0].getText();
					String amount2 = amounts[i][1].getText();
					String amount3 = amounts[i][2].getText();
					
					int amt1 = 0;
					int amt2 = 0;
					int amt3 = 0;
					if(amount1 != null && !amount1.isEmpty()) {
						try {
							amt1 = Integer.parseInt(amount1);
						} catch(Exception ex) {
							JOptionPane.showMessageDialog(null, "Please enter valid amount in Prize 1 field for "+ LABELS[i]
											, "Error", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
					if(amount2 != null && !amount2.isEmpty()) {
						try {
							amt2 = Integer.parseInt(amount2);
						} catch(Exception ex) {
							JOptionPane.showMessageDialog(null, "Please enter valid amount in Prize 2 field for "+ LABELS[i]
											, "Error", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
					if(amount3 != null && !amount3.isEmpty()) {
						try {
							amt3 = Integer.parseInt(amount3);
						} catch(Exception ex) {
							JOptionPane.showMessageDialog(null, "Please enter valid amount in Prize 3 field for "+ LABELS[i]
											, "Error", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
					
					Prize prize = new Prize(i, amt1, amt2, amt3);
					prizes.add(prize);
				}
				if(callback != null) {
					callback.onSuccess(prizes);
				}
				setVisible(false);
			}
		});
	}

	private void createMainPanel() {
		JPanel panel = (JPanel) getContentPane();
		panel.setLayout(new BorderLayout());
		panel.add(getCenterPanel(), BorderLayout.CENTER);
		panel.add(getSouthPanel(), BorderLayout.SOUTH);
	}

	private Component getSouthPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(btnReset);
		panel.add(btnSave);
		return panel;
	}

	private Component getCenterPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(LABELS.length+1, 3));
		panel.add(new JLabel(" "));
		panel.add(new JLabel("Prize 1 (Rs.)"));
		panel.add(new JLabel("Prize 2 (Rs.)"));
		panel.add(new JLabel("Prize 3 (Rs.)"));
		for(int i=0; i<LABELS.length; i++) {
			panel.add(labels[i]);
			panel.add(amounts[i][0]);
			panel.add(amounts[i][1]);
			panel.add(amounts[i][2]);
		}
		return panel;
	}
}
