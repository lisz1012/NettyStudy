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
		ch.pipeline().addLast(new ClientHandler(cf));
	}
}
