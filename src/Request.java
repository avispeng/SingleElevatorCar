
public class Request {
	public final int senderFloor;
	public final int direction;// -1 : going down, +1 : going up
	public final int wantToGo;

	public Request(int fromFloor, int toFloor) {
		this.senderFloor = fromFloor;
		this.wantToGo = toFloor;
		if (toFloor > fromFloor) {
			this.direction = 1;
		} else {
			this.direction = -1;
		}
	}
}
