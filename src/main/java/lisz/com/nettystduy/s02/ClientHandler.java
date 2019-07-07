package lisz.com.nettystduy.s02;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	private ClientFrame cf = ClientFrame.INSTANCE;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Connected to server");
		ByteBuf buf = Unpooled.copiedBuffer("A new client connected to server\n".getBytes());
		ctx.writeAndFlush(buf);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {//ctx代表Channel目前运行的网络环境
		try {																		//客户端和服务器是一个通道的两端，就好比两个手机通话
			if (ClientFrame.id == null) {
				String id = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
				System.out.println("ID assigned: " + id);
				ClientFrame.id = id;
				String initMsg = "You (ID: " + ClientFrame.id + ") have connected to Wechat server, have fun!" + 
						System.getProperty("line.separator");
				cf.updateTextArea(initMsg);
			} else {
				ByteBuf buf = (ByteBuf)msg;
				cf.updateTextArea(buf.toString(CharsetUtil.UTF_8));
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
