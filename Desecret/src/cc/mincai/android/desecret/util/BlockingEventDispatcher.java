package cc.mincai.android.desecret.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockingEventDispatcher {
    private final Map<Class<? extends BlockingEvent>, List<Action1<? extends BlockingEvent>>> listeners = new LinkedHashMap<Class<? extends BlockingEvent>, List<Action1<? extends BlockingEvent>>>();

    @SuppressWarnings({"unchecked"})
    public <BlockingEventT extends BlockingEvent> void dispatch(BlockingEventT event) {
        Class<? extends BlockingEvent> eventClass = event.getClass();

        if (listeners.containsKey(eventClass)) {
            for (Action1<?> listener : listeners.get(eventClass)) {
                ((Action1<BlockingEventT>) listener).apply(event);
            }
        }
    }

    public <BlockingEventT extends BlockingEvent> void addListener(Class<BlockingEventT> eventClass, Action1<BlockingEventT> listener) {
        if (!listeners.containsKey(eventClass)) {
            listeners.put(eventClass, new ArrayList<Action1<? extends BlockingEvent>>());
        }

        if (!listeners.get(eventClass).contains(listener)) {
            listeners.get(eventClass).add(listener);
        }
    }

    public <BlockingEventT extends BlockingEvent> void removeListener(Class<BlockingEventT> eventClass, Action1<BlockingEventT> listener) {
        if (listeners.containsKey(eventClass)) {
            listeners.get(eventClass).remove(listener);
        }
    }

    public void clearListeners() {
        listeners.clear();
    }
}