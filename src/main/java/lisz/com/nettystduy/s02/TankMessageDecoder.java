package lisz.com.nettystduy.s02;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class TankMessageDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 8) return; // 还没有读全的情况下不处理，什么时候够了8个字节什么时候处理。解决了TCP的拆包和粘包的问题
		in.markReaderIndex();
		int x = in.readInt();
		int y = in.readInt();
		out.add(new TankMessage(x, y));    // 解析出来的对象全都装到list中就可以了
	}

}
