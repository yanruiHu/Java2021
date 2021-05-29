package CDshop;

import java.io.FileWriter;
import java.io.IOException;

public class StockThread implements Runnable {
	private static CDshop shop;
	private FileWriter fileWritter;
	long starttime, sec, mlsec;

	public StockThread(CDshop s, FileWriter fw, long startt) {
		shop = s;
		fileWritter = fw;
		starttime = startt;
	}

	void getTime() {
		long currenttime = System.currentTimeMillis();
		long use = currenttime - starttime;
		sec = use / 1000;
		mlsec = use % 1000;
	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				synchronized (this) {
					wait(1000);
				}
				for (int i = 0; i < 10; i++) {
					shop.sellable[i].stock();
					synchronized (fileWritter) {
						getTime();
						fileWritter.write(sec + "." + mlsec + "s: stock CDs of class" + i + "\r\n");
					}
				}
			}
		} catch (InterruptedException e) {
			try {
				synchronized (fileWritter) {
					getTime();
					fileWritter.write(sec + "." + mlsec + "s: interrupt-stocking!\r\n");
				}
			} catch (IOException e1) {
				System.out.println("Fail writing in!");
			}
		} catch (IOException e) {
			System.out.println("Fail writing in!");
		}
	}
}
