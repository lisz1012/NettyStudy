package lisz.com.nettystduy.s02;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Connected to server");
		ByteBuf buf = Unpooled.copiedBuffer("A new client connected to server\n".getBytes());
		ctx.writeAndFlush(buf);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (ClientFrame.id == null) {
				String id = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
				System.out.println("ID assigned: " + id);
				ClientFrame.id = id;
				ClientFrame.TEXT_AREA.setText("You (ID: " + ClientFrame.id + ") have connected to Wechat server, have fun!\n");
			} else {
				ByteBuf buf = (ByteBuf)msg;
				ClientFrame.TEXT_AREA.setText(ClientFrame.TEXT_AREA.getText() + "\n" + buf.toString(CharsetUtil.UTF_8));
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		cause.printStackTrace();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Server offline or disconnected.");
		ctx.close();
	}
}
