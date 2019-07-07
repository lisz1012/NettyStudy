package lisz.com.nettystduy.s02;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	private ClientFrame cf;
	
	public ClientChannelInitializer(ClientFrame cf) {
		this.cf = cf;
	} 
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new ClientHandler(cf)); // Channel是通道，pipeline是各种Handler的一个责任链，和Channel相关的
													  // Channel相当于Socket必须用它来发消息
	}
}
