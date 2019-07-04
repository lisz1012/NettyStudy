/**
 * Netty是基于事件模型的，事件发生之后怎么处理，写这部分的逻辑代码就行了，用户可以更关注与业务
 * Netty里的所有方法都是异步方法，执行就是用别的线程干活，然后自己干别的去了，要想等着它执行完
 * 在做某些事，要调用一下sync()，这是一个ChannelFuture的方法
 */
package lisz.com.nettystduy.s01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * Hello Netty!
 *
 */
public class Client {
    public static void main( String[] args ) {
    	// 其实就是个线程池,  传个参数1，默认是核数*2个线程，没必要。线程池用来处理整个Channel上的所有事件，如b.connect()
    	// 以及read，都是线程池里出一个线程帮着连接或者读取
        EventLoopGroup workers = new NioEventLoopGroup(1); 
        Bootstrap b = new Bootstrap(); // “解靴带”理解为一个辅助启动的类就行了
        b.group(workers) // 工厂方法，把线程池设置进来, 以后任何事件都交给里面的线程处理
         .channel(NioSocketChannel.class) // 这里换成 SocketChannel就成了BIO，阻塞版
         .handler(new ChannelInitializer<SocketChannel>() { // ChannelInitializer是channel做初始化用的，初始化的时候添加handler
			@Override
			protected void initChannel(SocketChannel ch) throws Exception { // channel连上去之后会调用initChannel
				ch.pipeline().addLast(new MyHandler());
			}
		});
        try {
			ChannelFuture f = b.connect("127.0.0.1", 8888).sync(); // 这个connect是个异步方法，sync()表示connect得执行完成了才可以往下走
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workers.shutdownGracefully();
		}
    }
    
    private static final class MyHandler extends ChannelInboundHandlerAdapter {
    	
    	@Override
    	public void channelActive(ChannelHandlerContext ctx) throws Exception {
    		final ChannelFuture f = ctx.writeAndFlush(Unpooled.copiedBuffer("Hello server".getBytes()));
    		f.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						System.out.println("Client successfully wrote to server");
					} else {
						System.out.println("Client connect to server failed");
					}
				}           
			});//.addListener(ChannelFutureListener.CLOSE); //长连接变短连接,但这里有这句会报异常，因为server还会尝试往这里写数据呢
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
    /* 打印，先连接然后读取server写过来的消息
     * Client successfully wrote to server
	   Hello client!!!
     */
}
