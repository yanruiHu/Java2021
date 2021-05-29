package CDshop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SellThread implements Runnable {
	private static CDshop shop;
	private StockThread st;
	private FileWriter fileWritter;
	long starttime, sec, mlsec;

	public SellThread(CDshop s, StockThread std, FileWriter fw, long startt) {
		shop = s;
		st = std;
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
				Random rand = new Random();
				int classnum = rand.nextInt(10);
				int buynum = rand.nextInt(5) + 1;

				if (shop.sellable[classnum].getNum() < buynum) {
					synchronized (fileWritter) {
						getTime();
						fileWritter.write(sec + "." + mlsec + "s: CDs of class" + classnum + " is not enough\r\n");
					}
					synchronized (st) {
						st.notify();
					}
					boolean isWait = rand.nextBoolean();
					if (isWait == true) {
						synchronized (fileWritter) {
							getTime();
							fileWritter.write(sec + "." + mlsec + "s: waiting for CDs of class" + classnum + "\r\n");
						}
						synchronized (shop.sellable[classnum]) {
							while (shop.sellable[classnum].getNum() < buynum) {
								shop.sellable[classnum].wait();
							}
						}
					} else {
						synchronized (fileWritter) {
							getTime();
							fileWritter
									.write(sec + "." + mlsec + "s: giving up selling CDs of class" + classnum + "\r\n");
						}
						continue;
					}
				}

				shop.sellable[classnum].buy(buynum);
				Thread.sleep(rand.nextInt(200));
				synchronized (fileWritter) {
					getTime();
					fileWritter.write(sec + "." + mlsec + "s: selling " + buynum + " CDs of class" + classnum + "\r\n");
				}
			}
		} catch (InterruptedException e) {
			try {
				synchronized (fileWritter) {
					getTime();
					fileWritter.write(sec + "." + mlsec + "s: interrupt-selling!\r\n");
				}
			} catch (IOException e1) {
				System.out.println("Fail writing in!");
			}
		} catch (IOException e) {
			System.out.println("Fail writing in!");
		}
	}
}
