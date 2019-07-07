package lisz.com.nettystduy.s02;

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

public class Server {
	private static final ChannelGroup CLIENTS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private ServerFrame sf;
	
	public static Server getInstance(ServerFrame sf) {
		Inner.SERVER.sf = sf;
		return Inner.SERVER;
	}
	
	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
		 .channel(NioServerSocketChannel.class)
		 .childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ServerHandler(sf));
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
	
	private static final class Inner {
		private static final Server SERVER = new Server();
	}
	
	private static final class ServerHandler extends ChannelInboundHandlerAdapter {
		private ServerFrame sf;
		
		public ServerHandler(ServerFrame sf) {
			this.sf = sf;
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			String str = "This is server, a client just connected to server. Assigning it the ID: " + CLIENTS.size();
			sf.display(str);
			ByteBuf buf = Unpooled.copiedBuffer((CLIENTS.size() + "").getBytes());
			ctx.writeAndFlush(buf);
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ByteBuf buf = (ByteBuf)msg;
			String str = "Server received: " + buf.toString(CharsetUtil.UTF_8);
			sf.display(str);
			CLIENTS.writeAndFlush(msg, ChannelMatchers.isNot(ctx.channel()));
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			ctx.close();
			cause.printStackTrace();
		}
	}
}
