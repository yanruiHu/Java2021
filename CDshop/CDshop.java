package CDshop;

class CD {
	private boolean rented = false;

	synchronized public void rentCD() throws InterruptedException {
		while (rented == true) {
			wait();
		}
		rented = true;
	}

	synchronized public void returnCD() throws InterruptedException {
		rented = false;
		notifyAll();
	}

	synchronized public boolean isrented() {
		return rented;
	}
}

class CDs {
	private int CDnum = 10;

	synchronized public int getNum() {
		return CDnum;
	}

	synchronized public void buy(int num) throws InterruptedException {
		while (CDnum < num) {
			wait();
		}
		CDnum -= num;
	}

	synchronized public void stock() {
		CDnum = 10;
		notifyAll();
	}
}

public class CDshop {
	CD[] rentable = new CD[10];
	CDs[] sellable = new CDs[10];

	public CDshop(CD[] r, CDs[] s) {
		rentable = r;
		sellable = s;
	}
}
