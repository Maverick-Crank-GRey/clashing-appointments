package mcgrey.app.test.clashing;

import com.google.common.collect.Sets;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TimeSlot {
    private static Map<Interval, TimeSlot> cache = new HashMap<>();
    private Interval interval;
    private Set<ActivityBar> members = Sets.newHashSet();

    private TimeSlot(Interval interval) {
        this.interval = interval;
    }

    public static TimeSlot createSlot(ReadableInstant start, ReadableInstant end) {
        final Interval interval = new Interval(start, end);
        final TimeSlot existing = cache.get(interval);

        if (existing == null) {
            final TimeSlot slot = new TimeSlot(interval);
            cache.put(interval, slot);

            return slot;
        } else {
            return existing;
        }
    }

    public void add(ActivityBar activity) {
        activity.add(this);
        members.add(activity);
    }

    public ReadableInstant getStart() {
        return interval.getStart();
    }

    public ReadableInstant getEnd() {
        return interval.getEnd();
    }

}
