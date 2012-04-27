package diplomarbeit.agreement.serialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Serialisiert Aufzählungswerte für JAX-RS.
 * 
 * Dieser Provider ermöglicht eine Konvertierung für String-Aufzählungen von/nach <code>text/plain</code>.
 * 
 * Diese Implementierung kann von Apache CXF aufgefunden und genutzt werden, wenn sie als OSGi-Dienst
 * mit Schnittstelle MessageBodyReader/Writer bereitgestellt wird.
 * 
 * @author Florian Blümel
 */
@Produces("text/plain")
@Consumes("text/plain")
public class EnumReaderWriter implements MessageBodyWriter<Object>, MessageBodyReader<Object> {
	// genauer wäre MessageBodyWriter<Enum<?>> -- damit hat CXF/JAX-RS dann aber Probleme.
	
	@Override
	public long getSize(Object value, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
		return value.toString().length();
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// auch isEnum prüfen, weil das Template-Argument Object und nicht Enum<?> ist, siehe oben
		return clazz.isEnum() && mediaType.equals(MediaType.TEXT_PLAIN_TYPE);
	}

	@Override
	public void writeTo(Object value, Class<?> clazz, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> headerParams, OutputStream stream)
			throws IOException, WebApplicationException
	{
		stream.write(value.toString().getBytes());
	}

	@Override
	public boolean isReadable(Class<?> clazz, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// auch isEnum prüfen, weil das Template-Argument Object und nicht Enum<?> ist, siehe oben
		//return clazz.isEnum() && mediaType.equals(MediaType.TEXT_PLAIN_TYPE);
		return clazz.isEnum() && (mediaType.getType() + '/' + mediaType.getSubtype()).equals(MediaType.TEXT_PLAIN);
		// Es gab einen Grund, warum die obere Version nicht ausreichte und ich die untere geschrieben habe --
		// leider hab ich ihn nicht direkt dokumentiert und kann ihn nicht mehr erkennen ...
	}

	@Override
	public Object readFrom(Class<Object> clazz, Type arg1,
			Annotation[] arg2, MediaType arg3,
			MultivaluedMap<String, String> arg4, InputStream stream)
			throws IOException, WebApplicationException
	{
		String value = new BufferedReader(new InputStreamReader(stream)).readLine();
		for (final Object constant : clazz.getEnumConstants())
			if (constant.toString().equals(value))
				return constant;
		return null;
		// throw?
	}
}