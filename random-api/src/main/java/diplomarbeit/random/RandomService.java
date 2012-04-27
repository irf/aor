package diplomarbeit.random;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * Einfacher Beispieldienst für Testzwecke.
 * @author Florian Blümel
 */
@Path("random")
public interface RandomService {
    @GET
    @Produces("text/plain")
    public String sayHello();
}
