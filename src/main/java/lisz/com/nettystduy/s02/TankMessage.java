/**
 * x和y一共只有8个字节，但是序列化的话就会成为好几十个字节，所以
 * 序列化在高效传输的时候不适用。在Netty里帮我们实现了非常好用的
 * 接口：Codec
 */
package lisz.com.nettystduy.s02;

public class TankMessage {
	public int x, y;

	public TankMessage(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "Tank message: " + x + ", " + y;
	}
}
