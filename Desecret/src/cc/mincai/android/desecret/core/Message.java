package cc.mincai.android.desecret.core;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public abstract class Message implements Serializable {
    private Date timeCreated;

    public Message() {
        this(Calendar.getInstance().getTime());
    }

    public Message(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }
}
