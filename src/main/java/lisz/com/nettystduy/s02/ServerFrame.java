package lisz.com.nettystduy.s02;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerFrame extends Frame {
	private static final long serialVersionUID = -2789470537819312371L;
	public static final ServerFrame INSTANCE = new ServerFrame();
	private Button btnStart = new Button("Start");
	private TextArea serverTextArea = new TextArea();
	private TextArea clientTextArea = new TextArea();
	
	private ServerFrame() {
		setSize(1600, 600);
		setLocation(300, 30);
		add(btnStart, BorderLayout.NORTH);
		Panel p = new Panel(new GridLayout(1, 2));
		p.add(serverTextArea);
		p.add(clientTextArea);
		add(p);
		serverTextArea.setEditable(false);
		clientTextArea.setEditable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(()->{
					Server.getInstance().run();}).start();
			}
		});
	}
	
	public void display(String str) {
		System.out.println(str);
		serverTextArea.setText(serverTextArea.getText() + "\n" + str);
	}
	
	public static void main(String[] args) {
		//ServerFrame sf = new ServerFrame();
		//Server.getInstance().run();
		INSTANCE.setVisible(true);
	}
	
	public void updateServerMessage(String msg) {
		serverTextArea.setText(serverTextArea.getText() + System.getProperty("line.separator") + msg);
	}
}
