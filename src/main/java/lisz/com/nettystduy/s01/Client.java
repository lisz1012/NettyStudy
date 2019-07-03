package lisz.com.nettystduy.s01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * Hello world!
 *
 */
public class Client {
    public static void main( String[] args ) {
        EventLoopGroup workers = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap(); // “解靴带”理解为一个辅助启动的类就行了
        b.group(workers)
         .channel(NioSocketChannel.class)
         .handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new MyHandler());
			}
		});
        try {
			ChannelFuture f = b.connect("127.0.0.1", 8888).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        workers.shutdownGracefully();
    }
    
    private static final class MyHandler extends ChannelInboundHandlerAdapter {
    	
    	@Override
    	public void channelActive(ChannelHandlerContext ctx) throws Exception {
    		ctx.writeAndFlush(Unpooled.copiedBuffer("Hello server".getBytes()));
    	}
    	
    	@Override
    	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    		ByteBuf buf = (ByteBuf)msg;
    		System.out.println(buf.toString(CharsetUtil.UTF_8));
    	}
    	
    	@Override
    	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    		ctx.close();
    		cause.printStackTrace();
    	}
    	
    	@Override
    	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    		ctx.close();
    		System.out.println("Server is offline!");
    	}
    }
}
