import java.util.Comparator;
import java.util.PriorityQueue;

public final class ElevatorCar {
	private static ElevatorCar car = null;
	private static int currentFloor;
	private static int status; // -1 : going down, 0 : stopping, 1 : going up
	private static PriorityQueue<Request> requestList;
	private static PriorityQueue<Command> commandList;

	Comparator<Request> requestCompare = new Comparator<Request>() {
		public int compare(Request r1, Request r2) {
			if (r1.senderFloor == currentFloor && r1.direction == status)
				return -1;
			if (r2.senderFloor == currentFloor && r2.direction == status)
				return 1;
			if ((r1.senderFloor - currentFloor) * (r2.senderFloor - currentFloor) < 0) {
				// one in front, one behind
				return status * (currentFloor - r1.senderFloor);
			}
			if ((status * (r1.senderFloor - currentFloor)) > 0) {
				// two in front
				if (r1.direction != r2.direction) {
					// one same, one opposite
					return -(status * r1.direction);
				} else {
					if (r1.direction == status) {
						// two same
						return Math.abs(r1.senderFloor - currentFloor) - Math.abs(r2.senderFloor - currentFloor);
					} else {
						// two opposite
						return Math.abs(r2.senderFloor - currentFloor) - Math.abs(r1.senderFloor - currentFloor);
					}
				}
			} else {
				// two behind
				if (r1.direction != r2.direction) {
					// one same, one opposite
					return status * r1.direction;
				} else {
					if (r1.direction == status) {
						// two same
						return Math.abs(r2.senderFloor - currentFloor) - Math.abs(r1.senderFloor - currentFloor);
					} else {
						// two opposite
						return Math.abs(r1.senderFloor - currentFloor) - Math.abs(r2.senderFloor - currentFloor);
					}
				}
			}
		}
	};

	Comparator<Command> commandCompare = new Comparator<Command>() {
		public int compare(Command c1, Command c2) {
			return Math.abs(c1.destFloor - currentFloor) - Math.abs(c2.destFloor - currentFloor);
		}
	};

	private ElevatorCar() {
		currentFloor = 1;
		status = 0;
		requestList = new PriorityQueue<>(requestCompare);
		commandList = new PriorityQueue<>(commandCompare);
	}

	public static ElevatorCar getInstance() {
		if (car == null) {
			synchronized (ElevatorCar.class) {
				if (car == null) {
					car = new ElevatorCar();
				}
			}
		}
		return car;
	}

	private static void moveUp() {
		currentFloor++;
	}

	private static void moveDown() {
		currentFloor--;
	}

	public static void addRequest(Request r) {
		requestList.offer(r);
	}

	private static void run() {
		if (commandList.isEmpty() && requestList.isEmpty()) {
			status = 0;
		} else {
			Command primaryCommand;
			Request primaryRequest;
			if (!commandList.isEmpty()) {
				primaryCommand = commandList.peek();
			} else {
				primaryCommand = null;
			}
			if (!requestList.isEmpty()) {
				primaryRequest = requestList.peek();
			} else {
				primaryRequest = null;
			}
			if (primaryCommand != null) {
				if (primaryCommand.destFloor == currentFloor) {
					commandList.poll();
					if (commandList.isEmpty() && requestList.isEmpty())
						status = 0;
				} else if (primaryCommand.destFloor > currentFloor) {
					status = 1;
					moveUp();
				} else {
					status = -1;
					moveDown();
				}
			} else {
				if (primaryRequest.senderFloor > currentFloor) {
					status = 1;
					moveUp();
				} else if (primaryRequest.senderFloor < currentFloor) {
					status = -1;
					moveDown();
				}
			}
			if (primaryRequest != null) {
				if (primaryRequest.senderFloor == currentFloor
						&& (primaryCommand == null || status == primaryRequest.direction)) {
					requestList.poll();
					commandList.offer(new Command(primaryRequest.wantToGo));
				}
			}
		}
	}

	public static void start() {
		int i = 0;
		while (i < 60) {
			run();
			System.out.println(currentFloor);
			if (i == 9)
				addRequest(new Request(9, 13));
			if (i == 11)
				addRequest(new Request(7, 15));
			if (i == 15)
				addRequest(new Request(14, 10));
			if (i == 20)
				addRequest(new Request(6, 13));
			i++;
		}
	}
}
