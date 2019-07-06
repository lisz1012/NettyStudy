package lisz.com.nettystduy.s02;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatchers;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server extends Frame {
	private static final long serialVersionUID = -2789470537819312371L;
	private static final ChannelGroup CLIENTS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	public static final TextArea TEXT_AREA = new TextArea();
	
	public Server() {
		setSize(370, 650);
		setLocation(800, 20);
		add(TEXT_AREA, BorderLayout.CENTER);
		TEXT_AREA.setEditable(false);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	private void display(String str) {
		System.out.println(str);
		TEXT_AREA.setText(TEXT_AREA.getText() + "\n" + str);
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
		 .channel(NioServerSocketChannel.class)
		 .childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ServerHandler(server));
				CLIENTS.add(ch);
			}
		});
		try {
			ChannelFuture f = b.bind(8888).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private static final class ServerHandler extends ChannelInboundHandlerAdapter {
		private Server server;
		
		public ServerHandler(Server server) {
			this.server = server;
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			String str = "This is server, a client just connected to server. Assigning it the ID: " + CLIENTS.size();
			server.display(str);
			ByteBuf buf = Unpooled.copiedBuffer((CLIENTS.size() + "").getBytes());
			ctx.writeAndFlush(buf);
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ByteBuf buf = (ByteBuf)msg;
			String str = "Server received: " + buf.toString(CharsetUtil.UTF_8);
			server.display(str);
			CLIENTS.writeAndFlush(msg, ChannelMatchers.isNot(ctx.channel()));
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			ctx.close();
			cause.printStackTrace();
		}
	}
}
