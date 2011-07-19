package cc.mincai.android.desecret.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public abstract class Event implements Identifiable, Serializable {
    private String id;
    private String userId;
    private Date timeCreated;
    private String description;

    public Event(String id, String userId, String description) {
        this(id, userId, Calendar.getInstance().getTime(), description);
    }

    public Event(String id, String userId, Date timeCreated, String description) {
        this.id = id;
        this.userId = userId;
        this.timeCreated = timeCreated;
        this.description = description;
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
