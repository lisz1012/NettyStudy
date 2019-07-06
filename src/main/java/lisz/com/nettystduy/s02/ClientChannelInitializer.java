package lisz.com.nettystduy.s02;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
		ch.pipeline()
		  .addLast(new DelimiterBasedFrameDecoder(256, buf))
		  .addLast(new StringDecoder())
		  .addLast(new ClientHandler());
	}
}
