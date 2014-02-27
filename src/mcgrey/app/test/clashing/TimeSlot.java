package mcgrey.app.test.clashing;

import org.joda.time.Interval;

import java.util.SortedSet;
import java.util.TreeSet;

public class TimeSlot {
    private final Interval interval;
    private final SortedSet<ActivityBar> members = new TreeSet<>();

    public TimeSlot(Interval interval) {
        this.interval = interval;
    }

    public void add(ActivityBar activityBar) {
        activityBar.add(this);
        members.add(activityBar);
    }

    public SortedSet<ActivityBar> getMembers() {
        return members;
    }

    public Interval getInterval() {
        return interval;
    }
}
