package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

@Root
public class Message implements Identifiable {
    @Attribute
    private String id;
    @Attribute
    private String from;
    @Attribute
    private String to;
    @Element
    private MessageContent content;
    @Attribute
    private Date timeCreated;

    public Message(
            @Attribute(name = "id") String id,
            @Attribute(name = "from") String from,
            @Attribute(name = "to") String to,
            @Element(name = "content") MessageContent content
    ) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.content = content;

        this.timeCreated = Calendar.getInstance().getTime();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public MessageContent getContent() {
        return content;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public static String toXml(Message message) {
        Serializer serializer = new Persister();

        StringWriter sw = new StringWriter();

        try {
            serializer.write(message, sw);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sw.toString();
    }

    public static Message fromXml(InputStream is) {
        Serializer serializer = new Persister();

        try {
            return serializer.read(Message.class, is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Message fromXml(String xml) {
        Serializer serializer = new Persister();

        try {
            return serializer.read(Message.class, xml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        testMessageSerialization(new Message("hello", "world", "android", new TextMessageContent("content is empty")));
        testMessageSerialization(new Message("hello", "world", "android", new UserEventMessageContent(new MemoEvent("hello", "flexim", "tidbit", "desc"))));
    }

    private static void testMessageSerialization(Message message) {
        String str = Message.toXml(message);
        System.out.println(str);
        Message message1 = Message.fromXml(str);
        String str1 = Message.toXml(message1);
        System.out.println(str1);
    }
}
