package cc.mincai.android.desecret.core;

import java.util.ArrayList;
import java.util.List;

public class ActivityEventContainer {
    private List<ActivityEvent> events;

    public ActivityEventContainer() {
        this.events = new ArrayList<ActivityEvent>();
    }

    public List<ActivityEvent> getEvents() {
        return events;
    }
}
