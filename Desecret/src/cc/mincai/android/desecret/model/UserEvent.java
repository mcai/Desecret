package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Attribute;

import java.util.Calendar;
import java.util.Date;

public abstract class UserEvent implements Identifiable {
    @Attribute
    private String id;
    @Attribute
    private String userId;
    @Attribute
    private Date timeCreated;
    @Attribute
    private String description;

    public UserEvent(
            @Attribute(name = "id") String id,
            @Attribute(name = "userId") String userId,
            @Attribute(name = "description") String description
    ) {
        this.id = id;
        this.userId = userId;
        this.description = description;

        this.timeCreated = Calendar.getInstance().getTime();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public String getDescription() {
        return description;
    }
}
