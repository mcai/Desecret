package cc.mincai.android.desecret.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public abstract class ActivityEvent implements Serializable {
    private Date timeCreated;

    public ActivityEvent() {
        this(Calendar.getInstance().getTime());
    }

    public ActivityEvent(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }
}
