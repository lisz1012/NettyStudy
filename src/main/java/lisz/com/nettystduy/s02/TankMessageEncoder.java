/**
 * 对象信息转换成ByteBuf
 */
package lisz.com.nettystduy.s02;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TankMessageEncoder extends MessageToByteEncoder<TankMessage> {
	// 通过ByteBuf的writeInt，writeLong等方法把msg中的int属性写进ByteBuf
	@Override
	protected void encode(ChannelHandlerContext ctx, TankMessage msg, ByteBuf out) throws Exception {
		out.writeInt(msg.x);
		out.writeInt(msg.y);
	}

}
