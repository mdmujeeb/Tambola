import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;


public class MyJLabel extends JLabel {

	private static final long serialVersionUID = 473788681083649464L;
	
	private String value;
	private boolean isPrizeLabel = false;
	private boolean isPrizeGone = false;

	public MyJLabel(String text, boolean isPrizeLabel) {
		super(text);
		this.isPrizeLabel = isPrizeLabel;
		this.value = text;
		setOpaque(true);
		if(!isPrizeLabel) {
			return;
		}
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() > 1) {
					if(isPrizeGone) {
						isPrizeGone = false;
					} else {
						VoiceUtil.play(Main.SONG_FILE);
						isPrizeGone = true;
					}
					setText(value);
				}
			}
		});
	}
	
	@Override
	public void setText(String text) {
		this.value = text;
		if(!isPrizeLabel) {
			super.setText(value);
			return;
		}
		
		if(!isPrizeGone) {
			setBackground(Color.LIGHT_GRAY);
			super.setText(value);
		} else {
			setBackground(Color.RED);
			super.setText("00");
		}
	}
	
	public void prizeGone() {
		isPrizeGone = true;
		setText(value);
	}
	
	public void prizeRestored() {
		isPrizeGone = false;
		setText(value);
	}
}
