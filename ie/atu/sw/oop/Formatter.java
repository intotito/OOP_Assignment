package ie.atu.sw.oop;

import java.util.function.Supplier;
import java.util.stream.LongStream;

public class Formatter {
	/**
	 * This utility method emulates a Progress Meter that keeps track of a
	 * background process progress. The method executes on a Virtual Thread
	 * 
	 * <pre>
	 * *******************
	 * Big-O Running Time*
	 * *******************
	 * </pre>
	 * 
	 * This method running time is quadratic i.e. <code>x²</code>. It executes a
	 * nested loop.
	 * 
	 * @param total    - The maximum value the background process is expected to
	 *                 reach to indicate it has finished execution.
	 * @param supplier - A {@link Supplier Supplier} Functional Interface that
	 *                 supplies the current progress of the background process. This
	 *                 argument can be supplied in form of a lambda expression or a
	 *                 method reference.
	 */
	public static void printProgress(long total, Supplier<Long> supplier) {
		Thread.startVirtualThread(() -> {
			long METER_SIZE = 50;
			char done = '█'; // Change to whatever you like.
			char todo = '░'; // Change to whatever you like.
			long index = 0;
			long progress = 0;
			while (index < total) {
				index = supplier.get();
				System.out.print("\r[");
				progress = (index * 100) / total;
				long pgs = progress;
				LongStream.range(0, METER_SIZE)
						.forEach(i -> System.out.printf("%c", i > pgs * METER_SIZE / 100 ? todo : done));
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.printf("] %d%%  ", progress);
			}
		});
	}
}
