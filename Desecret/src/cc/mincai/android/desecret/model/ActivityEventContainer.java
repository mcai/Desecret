package cc.mincai.android.desecret.model;

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
