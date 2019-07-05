package lisz.com.nettystduy.s01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server {											//本质就是线程池
	public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);//一个默认的线程来处理通道组上的事件
	
	
	public static void main(String[] args) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 其实就是两个线程池。boss是迎宾的管家，worker是饭店里的服务员
		EventLoopGroup workerGroup = new NioEventLoopGroup(2); // bossGroup负责接受连接，workerGroup负责处理跟client之间的IO事件。2代表两个线程，不代表线程池
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
		 .channel(NioServerSocketChannel.class) // 这里指定了netty中所有方法都是异步的
		 .childHandler(new ChannelInitializer<SocketChannel>() { // 这是对于每一个客户端的连接。如果写handler的话会被加在server的大面板以及所有的连上来的channel上，childHandler只是加在client端那部分
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ServerHandler()); //各个handler形成一个责任链
				clients.add(ch); // 也可以在以上ServerHandler的channelActive的重写方法里用client。add(ctx.channel());的方式添加
			}
		});
		
		try {
			ChannelFuture f = b.bind(8888).sync(); //监听这里也可以加allFutureListener。bind也是异步的，想同步还是得用sync
			f.channel().closeFuture().sync(); //没有这里的sync的话，server刚起来监听好，又随着main的结束而结束了，有了它会阻塞在这里，：
		} catch (InterruptedException e) {   //这里的channel是server的那个channel。closeFuture()的意思是：如果有人调用了close()
			e.printStackTrace();             //它的返回值是ChannelFuture，如果没人调用close方法，则会永远等待着结果，因为有sync。
		} finally { 						 //close的时候才执行完。closeFuture就是等着关门的那机器人儿,close()被调用的时候才会继续执行
			bossGroup.shutdownGracefully();  // Server的停止是相当有讲究的，客户端连在上面，可能有一些事务，他们必须执行完才继续停机，
			workerGroup.shutdownGracefully();// 还需要告诉客户端：我要宕机了，请你赶紧保存你那边的数据，然后才会一个个停下来，把跟client的连接一个个关闭掉，然后才把总的服务器关掉，然后重启
		}
		
	}

	private static final class ServerHandler extends ChannelInboundHandlerAdapter { //ChannelOutboundHandlerAdapter用得很少
		@Override                                // 最初没有泛型，系统遗留问题。SimpleChannelInboundHandler跟 Codec结合可以用泛型，见坦克项目
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception { // 真正读数据的是workers中拿出的一个线程。msg不是客户端那个，而是数据过来之后又做成一个ByteBuf.网络环境中对channel处理需要的东西都在ctx里了
			ByteBuf buf = (ByteBuf) msg;  // ByteBuf指向的是系统机内存，而不是虚拟机内存，所以下面必须自己手动释放。PS：ctx的writeAndFlsh会自动释放，所以下面不用手动释放。
			//System.out.println(buf.refCnt());
			//System.out.println(buf.toString(CharsetUtil.UTF_8));
			byte bytes[] = new byte[buf.readableBytes()];
			buf.getBytes(buf.readerIndex(), bytes);
			System.out.println("Server received: " + new String(bytes));
			//ctx.writeAndFlush(Unpooled.copiedBuffer("Hello client!!!".getBytes()));//有这一句write操作就不要手动释放了，要不然报错，当成规则记住就可以了
			//ctx.writeAndFlush(msg);
			clients.writeAndFlush(msg);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			ctx.close();
			cause.printStackTrace();
		}
	}
}
