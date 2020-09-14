import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;


public class Main extends JFrame {

	private static final long serialVersionUID = -8852685221984393107L;
	public static final String SONG_FILE = "clapping.mp3";
	
	private JLabel[] lblNumbers = new JLabel[90];
	private JPanel centerPanel = new JPanel(); 
	private CardLayout statusLayout = new CardLayout();
	private JLabel lblCurrentNumber = new JLabel("00");
	private JLabel[] lblLastNumbers = new JLabel[5];
	private NumberQueue lastNumbersQueue;
	private JPanel statusPanel = new JPanel();
	private JButton btnPlayShow = new JButton(" Status ");
	private JButton btnNextNumber = new JButton("Next Number");
	private JButton btnWinner = new JButton(new ImageIcon("winner.png"));

	private PrizesTable table = null;
	private HashSet<Integer> calledNumbersSet = new HashSet<Integer>();
	private List<Prize> prizes = new ArrayList<Prize>();
	
	public static void main(String[] args) throws Exception {
		new Main();
	}
	
	Main() throws Exception {
		super("Ahmad's Tambola Game...");
//		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		initializeQueue();
		initializeNumbers();
		initializeButtons();
		createMainPanel();
		createHandlers();
		createMenuBar();
		
		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		initializePrizes();
		showSetupPrizesDialog();
	}
	
	private void initializePrizes() {
		for(int i=0; i<PrizesDialog.LABELS.length; i++) {
			prizes.add(new Prize(i, 0, 0, 0));
		}
	}

	private void initializeQueue() {
		for(int i=0; i<lblLastNumbers.length; i++) {
			lblLastNumbers[i] = new JLabel();
			lblLastNumbers[i].setOpaque(true);
			lblLastNumbers[i].setBackground(Color.BLACK);
			lblLastNumbers[i].setFont(new Font("Arial", 50, 50));
			lblLastNumbers[i].setBorder(new EtchedBorder(EtchedBorder.RAISED));
			lblLastNumbers[i].setHorizontalAlignment(JLabel.CENTER);
			if(i==0) {
				lblLastNumbers[i].setForeground(Color.GREEN);
			} else {
				lblLastNumbers[i].setForeground(Color.WHITE);
			}
		}
		lastNumbersQueue = new NumberQueue(lblLastNumbers);
	}

	private void resetGame() {
		calledNumbersSet.clear();
		for(int i=0; i<90; i++) {
			lblNumbers[i].setBackground(Color.BLACK);
		}
		lblCurrentNumber.setText("00");
		lastNumbersQueue.reset();
		
		showSetupPrizesDialog();
	}

	private void showSetupPrizesDialog() {
		table.reset();
		ActionCallback<List<Prize>> callback = new ActionCallback<List<Prize>>() {

			private static final long serialVersionUID = 509273797495337002L;

			@Override
			public void onSuccess(List<Prize> result) {
				prizes = result;
				updatePrizesPanel();
			}			

			@Override
			public void onFailure(Throwable caught) {}
		};
		PrizesDialog dialog = new PrizesDialog(callback, prizes);
		UIUtils.centerDialog(dialog);
		dialog.setVisible(true);
	}

	private void createMenuBar() {
		final JMenuItem mnuNewGame = new JMenuItem(" New Game ");
		final JMenuItem mnuPrizes = new JMenuItem(" Setup Prizes ");
		final JMenuItem mnuExit = new JMenuItem(" Exit ");
		JMenu menuMain = new JMenu(" Game ");
		menuMain.add(mnuNewGame);
		menuMain.add(mnuPrizes);
		menuMain.add(mnuExit);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuMain);
		setJMenuBar(menuBar);
		
		menuMain.setMnemonic('g');
		mnuNewGame.setMnemonic('n');
		mnuPrizes.setMnemonic('p');
		mnuExit.setMnemonic('x');
		
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if(event.getSource() == mnuNewGame) {
					int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a New Game?"
									, "Confirm New Game"
									, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(choice == JOptionPane.YES_OPTION) {
						resetGame();
					}
				} else if(event.getSource() == mnuPrizes) {
					showSetupPrizesDialog();
				} else  {
					int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want cancel game and Exit?"
							, "Confirm Exit"
							, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(choice == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			}			
		};
		mnuNewGame.addActionListener(listener);
		mnuPrizes.addActionListener(listener);
		mnuExit.addActionListener(listener);
	}

	private void createHandlers() {
		btnPlayShow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(btnPlayShow.getText().equals(" Status ")) {
					btnPlayShow.setIcon(new ImageIcon("play.png"));
					btnPlayShow.setText(" Continue ");
					statusLayout.show(centerPanel, "status");
					
				} else {
					btnPlayShow.setIcon(new ImageIcon("board.png"));
					btnPlayShow.setText(" Status ");
					statusLayout.show(centerPanel, "number");
				}
			}
		});
		
		btnNextNumber.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				callNextNumber();
			}
		});
		
		btnWinner.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				VoiceUtil.play(SONG_FILE);
				WinnerDialog dialog = new WinnerDialog(new ActionCallback<Prize>() {

					private static final long serialVersionUID = -5833037589513171975L;
					@Override
					public void onSuccess(Prize result) {
						table.updatePrizes(result);						
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				UIUtils.centerDialog(dialog);
				dialog.setVisible(true);
			}
		});	
	}

	private void callNextNumber() {
		if(calledNumbersSet.size() >= 90) {
			return;
		}
		
		int nextNumber = getNextRandomNumber();
		while(calledNumbersSet.contains(nextNumber)) {
			nextNumber = getNextRandomNumber();
		}
		calledNumbersSet.add(nextNumber);
		lblCurrentNumber.setText("" + nextNumber);
		lastNumbersQueue.push(nextNumber);
		lblNumbers[nextNumber-1].setBackground(new Color(0,85,0));
		VoiceUtil.speak(getSpeakableString(nextNumber));
	}

	private String getSpeakableString(int nextNumber) {
		StringBuilder builder = new StringBuilder();
		if(nextNumber < 10) {
			builder.append("Number. " + nextNumber);
		} else {
			int firstNumber = nextNumber / 10;
			int secondNumber = nextNumber % 10;
			builder.append("" + firstNumber + ".. " + secondNumber + ".. " + nextNumber);
		}
		return builder.toString();
	}

	private void initializeButtons() {
		btnPlayShow.setIcon(new ImageIcon("board.png"));
		btnNextNumber.setIcon(new ImageIcon("next.png"));
		
		btnPlayShow.setMnemonic('s');
		btnNextNumber.setMnemonic('n');
	}

	private void initializeNumbers() {
		for(int i=0; i<90; i++) {
			JLabel label = new JLabel("" + (i+1));
			label.setFont(new Font("Arial", 50, 50));
			label.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			label.setBackground(Color.BLACK);
			label.setForeground(Color.WHITE);
			label.setOpaque(true);
			lblNumbers[i] = label;
		}
		
		lblCurrentNumber.setFont(new Font("Arial", 700, 700));
		lblCurrentNumber.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		lblCurrentNumber.setBackground(Color.BLACK);
		lblCurrentNumber.setForeground(Color.WHITE);
		lblCurrentNumber.setOpaque(true);
		lblCurrentNumber.setHorizontalAlignment(JLabel.CENTER);
	}

	private void createMainPanel() {
		JPanel panel = (JPanel) getContentPane();
		panel.setLayout(new BorderLayout());
		panel.add(getCenterPane(), BorderLayout.CENTER);
		panel.add(getSouthPane(), BorderLayout.SOUTH);
		panel.add(getEastPanel(), BorderLayout.EAST);
	}

	private Component getEastPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		topPanel.add(new JLabel(" Last Numbers:"));
		for(int i=0; i<lblLastNumbers.length; i++) {
			topPanel.add(lblLastNumbers[i]);
			topPanel.add(new JLabel("-"));
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(topPanel,BorderLayout.NORTH);
		
		panel.add(getPrizesPanel(),BorderLayout.CENTER);
		
		JPanel panelWinner = new JPanel();
		panelWinner.setLayout(new FlowLayout());
		panelWinner.add(btnWinner);
		panel.add(panelWinner, BorderLayout.SOUTH);
		return panel;
	}

	private Component getPrizesPanel() {
		table =  new PrizesTable(prizes);
		return table;
	}

	private void updatePrizesPanel() {
		table.updatePrizes(prizes);
	}

	private Component getSouthPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(btnPlayShow);
		panel.add(btnNextNumber);
		return panel;
	}

	private Component getCenterPane() {
		centerPanel.setLayout(statusLayout);
		centerPanel.add("number" , lblCurrentNumber);
		centerPanel.add("status" , createStatusPanel());
		return centerPanel;
	}

	private Component createStatusPanel() {
		statusPanel.setLayout(new GridLayout(10, 9));
		for(int i=0; i<10; i++) {
			int num = i;
			for(int j=0; j<9; j++) {
				lblNumbers[num].setHorizontalAlignment(JLabel.CENTER);
				statusPanel.add(lblNumbers[num]);
				num += 10;
			}
		}
		return statusPanel;
	}
	
	private int getNextRandomNumber() {
		double number = Math.random() * 90;
		return (int) number+1;
	}
}
