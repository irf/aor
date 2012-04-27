package diplomarbeit.random.provider;

import diplomarbeit.random.RandomService;
import java.util.Random;

/**
 * Implementierung eines (nicht besonders zuverl�ssigen und recht unsinnigen) Dienstes f�r die Tests.
 * @author Florian Bl�mel
 */
public class Implementation implements RandomService {
    private Random generator = new Random();

	@Override
	public String sayHello() {
		if (generator.nextFloat() < 0.9)
			return "Hello World!";
		throw new RuntimeException();
	}
}
