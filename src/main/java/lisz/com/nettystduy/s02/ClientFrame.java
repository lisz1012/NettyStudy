package lisz.com.nettystduy.s02;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientFrame extends Frame {
	private static final long serialVersionUID = 8851447650164397784L;
	private TextArea textArea = new TextArea();
	private TextField textField = new TextField();
	public static String id;  //运行程序的时候会首先起一个JVM虚拟机，所以启动多个客户端他们的Class类对象也是不同的，这个id也会不一样
	public static final ClientFrame INSTANCE = new ClientFrame();
	
	private ClientFrame() {}
	
	public void init() {// 不放在构造方法里是怕NPE，还没执行完构造方法就往Client.getInstance(cf);传
		setSize(370, 470);
		setLocation(100, 20);
		add(textArea, BorderLayout.CENTER);
		add(textField, BorderLayout.SOUTH);
		textArea.setEditable(false);
		Client client = Client.getInstance();
		textField.addActionListener(new ActionListener() { // ActionListener在回车的时候会触发下面actionPerformed里面的语句
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "[" + ClientFrame.id + "]: " + textField.getText();
				client.send(msg);
				textArea.setText(textArea.getText() + System.getProperty("line.separator") + msg);
				textField.setText("");
			}
		});
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		client.connect();
	}
	
	public static void main(String[] args) {
		INSTANCE.init();
	}
	
	public TextArea getTextArea() {
		return textArea;
	}
	
	public void updateTextArea(String msg) {
		textArea.setText(textArea.getText() + System.getProperty("line.separator") + msg);
	}
}
