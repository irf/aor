package diplomarbeit.random.provider;

import diplomarbeit.random.RandomService;
import java.util.Random;

/**
 * Implementierung eines (nicht besonders zuverlässigen und recht unsinnigen) Dienstes für die Tests.
 * @author Florian Blümel
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
