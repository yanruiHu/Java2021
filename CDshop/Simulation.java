package CDshop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulation {
	public static void main(String[] args) throws IOException {
		long starttime = System.currentTimeMillis();
		try {
			FileWriter fileWritter;
			fileWritter = new FileWriter("E:\\record1.TXT", true);
			CD[] rentable = new CD[10];
			CDs[] sellable = new CDs[10];
			for (int i = 0; i < 10; i++) {
				rentable[i] = new CD();
				sellable[i] = new CDs();
			}
			CDshop shop = new CDshop(rentable, sellable);
			StockThread st = new StockThread(shop, fileWritter, starttime);// 一个进货线程

			int rn, sn;
			Scanner scan = new Scanner(System.in);
			rn = scan.nextInt();
			sn = scan.nextInt();
			ExecutorService es = Executors.newCachedThreadPool();
			es.execute(st);
			for (int i = 0; i < rn; i++)
				es.execute(new RentThread(shop, fileWritter, starttime));
			for (int i = 0; i < sn; i++)
				es.execute(new SellThread(shop, st, fileWritter, starttime));

			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				System.out.println("Simulation interrupted!");
			}
			es.shutdownNow();
			scan.close();
			System.out.println("done");
		} catch (IOException e) {
			System.out.println("Fail opening file!");
		}
	}
}
