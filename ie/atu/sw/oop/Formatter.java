package ie.atu.sw.oop;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.LongStream;

public class Formatter {
	public static void printProgress(long total, Supplier<Long> supplier) {
		try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
			es.execute(() -> {
				long METER_SIZE = 50;
				char done = '█'; // Change to whatever you like.
				char todo = '░'; // Change to whatever you like.
				long index = 0;
				long progress = 0;
				StringBuilder sb = new StringBuilder();
				while (index < total) {
					index = supplier.get();
					sb.setLength(0);
					sb.append("[");
					progress = (index / total * 100) * METER_SIZE;
					final long pgs = progress;
					LongStream.range(0, METER_SIZE).forEach(i -> sb.append(i > pgs ? todo : done));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.print("\r" + sb + "] " + progress + "%");
				}
				
			});
		}
	}
}
