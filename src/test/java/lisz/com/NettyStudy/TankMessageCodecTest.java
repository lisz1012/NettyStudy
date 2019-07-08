/**
 * Junit最大的好处是复用测试，改完源码再测一遍不用改测试，不用眼睛再盯着看一遍
 * PS: 王者荣耀就是服务器只是做转发消息的工作，逻辑都在客户端；海岛奇兵的服务端
 * 有逻辑，程序简单很多，客户端也简单，客户端的状态就是服务器的copy，我们tank
 * 游戏的转发，客户端很快就会变得不同步，得需要不断的进行同步才可以
 */
package lisz.com.nettystudy;

import static org.junit.Assert.*;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import lisz.com.nettystduy.s02.TankMessage;
import lisz.com.nettystduy.s02.TankMessageDecoder;
import lisz.com.nettystduy.s02.TankMessageEncoder;

public class TankMessageCodecTest {

	@Test
	public void testTankMessageEncoder() {
		TankMessage tankMessage = new TankMessage(50, 60);
		TankMessageEncoder tankMessageEncoder = new TankMessageEncoder();
		EmbeddedChannel ch = new EmbeddedChannel(tankMessageEncoder);
		ch.writeOutbound(tankMessage);
		
		ByteBuf buf = (ByteBuf)ch.readOutbound();
		int x = buf.readInt();
		int y = buf.readInt();
		
		assertEquals(tankMessage.x, x);
		assertEquals(tankMessage.y, y);
		buf.release();
	}

	
	@Test
	public void testTankMessageDecoder() {
		ByteBuf buf = Unpooled.buffer();
		TankMessage tankMessage = new TankMessage(30, 50);
		buf.writeInt(tankMessage.x);
		buf.writeInt(tankMessage.y);
		TankMessageEncoder tankMessageEncoder = new TankMessageEncoder();
		TankMessageDecoder tankMessageDecoder = new TankMessageDecoder();
		// 由于写的是ByteBuf，不符合Encoder的要求，所以他会跳过Encoder，直接执行Decoder。Encoder要求输入是对象，输出是ByteBuf
		EmbeddedChannel ch = new EmbeddedChannel(tankMessageEncoder, tankMessageDecoder);
		ch.writeInbound(buf.duplicate());
		
		TankMessage tankMessage2 = (TankMessage)ch.readInbound();
		assertEquals(tankMessage.x, tankMessage2.x);
		assertEquals(tankMessage.y, tankMessage2.y);
		buf.release();
	}
}
