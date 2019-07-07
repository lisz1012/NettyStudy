package lisz.com.nettystduy.s02;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerFrame extends Frame {
	private static final long serialVersionUID = -2789470537819312371L;
	public static final ServerFrame INSTANCE = new ServerFrame();
	private TextArea textArea = new TextArea();
	
	private ServerFrame() {
		setSize(370, 650);
		setLocation(800, 20);
		add(textArea, BorderLayout.CENTER);
		textArea.setEditable(false);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	public void display(String str) {
		System.out.println(str);
		textArea.setText(textArea.getText() + "\n" + str);
	}
	
	public static void main(String[] args) {
		//ServerFrame sf = new ServerFrame();
		Server.getInstance().run();
	}
}
