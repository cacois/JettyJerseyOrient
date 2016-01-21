package org.japhar81;

import com.google.gson.Gson;

import javax.inject.Singleton;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class GsonMessageBodyWriter<T> implements MessageBodyWriter<T> {
    public void writeTo(T t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream)
            throws IOException, WebApplicationException {
        Gson g = new Gson();
        httpHeaders.get("Content-Type").add("application/json");
        entityStream.write(g.toJson(t).getBytes("UTF-8"));
    }

    public long getSize(T t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return true;
    }
}
