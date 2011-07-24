package cc.mincai.android.desecret.util;

import cc.mincai.android.desecret.model.ActivityEvent;
import cc.mincai.android.desecret.model.Message;
import com.google.gson.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;

public class SerializationHelper {
    private static GsonBuilder gsonBuilder;

    static {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Message.class, new AbstractTypeAdapter<Message>());
        gsonBuilder.registerTypeAdapter(ActivityEvent.class, new AbstractTypeAdapter<ActivityEvent>());
    }

    public static <T> String serialize(T obj) {
        return gsonBuilder.setPrettyPrinting().create().toJson(obj);
    }

    public static <T> T deserialize(String xml, Class<? extends T> clz) {
        return gsonBuilder.setPrettyPrinting().create().fromJson(xml, clz);
    }

    public static <T> T deserialize(InputStream is, Class<? extends T> clz) {
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer);
            return deserialize(writer.toString(), clz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class AbstractTypeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("type", new JsonPrimitive(src.getClass().getName()));
            result.add("properties", context.serialize(src, src.getClass()));

            return result;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement element = jsonObject.get("properties");

            try {
                return context.deserialize(element, Class.forName(type));
            } catch (ClassNotFoundException cnfe) {
                throw new JsonParseException("Unknown element type: " + type, cnfe);
            }
        }
    }
}
