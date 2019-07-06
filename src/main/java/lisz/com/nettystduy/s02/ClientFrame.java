package lisz.com.nettystduy.s02;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientFrame extends Frame {
	private static final long serialVersionUID = 8851447650164397784L;
	public static final TextArea TEXT_AREA = new TextArea();
	public static final TextField TEXT_FIELD = new TextField();
	public static String id;
	private EventLoopGroup workers = new NioEventLoopGroup(1);
	private Bootstrap b = new Bootstrap();
	private ChannelInitializer<SocketChannel> channelInitializer = new ClientChannelInitializer();
	private ChannelFuture f;
	
	public ClientFrame() {
		setSize(600, 400);
		setLocation(100, 20);
		add(TEXT_AREA, BorderLayout.CENTER);
		add(TEXT_FIELD, BorderLayout.SOUTH);
		TEXT_AREA.setEditable(false);
		TEXT_FIELD.addActionListener(new ActionListener() { // ActionListener在回车的时候会触发下面actionPerformed里面的语句
			@Override
			public void actionPerformed(ActionEvent e) {
				ByteBuf buf = Unpooled.copiedBuffer(TEXT_FIELD.getText().getBytes());
				f.channel().writeAndFlush(buf);
				TEXT_AREA.setText(TEXT_AREA.getText() + "\n" + TEXT_FIELD.getText());
				TEXT_FIELD.setText("");
			}
		});
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		connect();
	}
	
	private void connect() {
		b.group(workers)
		 .channel(NioSocketChannel.class)
		 .handler(channelInitializer);
		try {
			f = b.connect("127.0.0.1", 8888).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workers.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		new ClientFrame();
	}
	
}
