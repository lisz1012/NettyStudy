package lisz.com.nettystduy.s02;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
	private EventLoopGroup workers = new NioEventLoopGroup(1);
	private ClientFrame cf;
	private ChannelFuture f;
	
	public void connect() {
		Bootstrap b = new Bootstrap();
		ChannelInitializer<SocketChannel> channelInitializer = new ClientChannelInitializer(cf);
		
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
	
	public void send(String msg) {
		if (f == null) {
			return;
		}
		ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
		f.channel().writeAndFlush(buf);
	}
	
	public static Client getInstance(ClientFrame cf) {
		if (Inner.CLIENT.cf == null) {
			Inner.CLIENT.cf = cf;
		}
		return Inner.CLIENT;
	}
	
	private static final class Inner {
		private static final Client CLIENT = new Client();
	}
}
