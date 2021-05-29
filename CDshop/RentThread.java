package CDshop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RentThread implements Runnable {
	private static CDshop shop;
	private FileWriter fileWritter;
	long starttime, sec, mlsec;

	public RentThread(CDshop s, FileWriter fw, long startt) {
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
				Random rand = new Random();
				int num = rand.nextInt(10);

				if (shop.rentable[num].isrented()) {
					synchronized (fileWritter) {
						getTime();
						fileWritter.write(sec + "." + mlsec + "s: CD" + num + " is not available\r\n");
					}
					boolean isWait = rand.nextBoolean();
					if (isWait == true) {
						synchronized (fileWritter) {
							getTime();
							fileWritter.write(sec + "." + mlsec + "s: waiting for CD" + num + "\r\n");
						}
						synchronized (shop.rentable[num]) {
							while (shop.rentable[num].isrented()) {
								shop.rentable[num].wait();
							}
						}
					} else {
						synchronized (fileWritter) {
							getTime();
							fileWritter.write(sec + "." + mlsec + "s: giving up CD" + num + "\r\n");
						}
						continue;
					}
				}
				shop.rentable[num].rentCD();
				Thread.sleep(rand.nextInt(200));// 租借CD用时
				synchronized (fileWritter) {
					getTime();
					fileWritter.write(sec + "." + mlsec + "s: CD" + num + " is rented\r\n");
				}

				Thread.sleep(rand.nextInt(100) + 200);

				shop.rentable[num].returnCD();
				synchronized (fileWritter) {
					getTime();
					fileWritter.write(sec + "." + mlsec + "s: CD" + num + " is returned\r\n");
				}
			}
		} catch (InterruptedException e) {
			try {
				synchronized (fileWritter) {
					getTime();
					fileWritter.write(sec + "." + mlsec + "s: interrupt-renting!\r\n");
				}
			} catch (IOException e1) {
				System.out.println("Fail writing in!");
			}
		} catch (IOException e) {
			System.out.println("Fail writing in!");
		}
	}
}
