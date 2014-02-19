package mcgrey.app.test.clashing;

import org.joda.time.Interval;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;

import java.util.Objects;

/**
 * This is a simple wrapper against {@link Interval} that also contains a name.
 */
public class Activity {
    private Interval interval;
    private String name;

    public Activity(Interval interval, String name) {
        this.interval = interval;
        this.name = name;
    }

    public Activity(ReadableInstant start, ReadableInstant end, String name) {
        this.name = name;
        interval = new Interval(start, end);
    }

    public Activity(ReadableInstant start, ReadableDuration duration, String name) {
        this.name = name;
        interval = new Interval(start, duration);
    }

    public ReadableInterval getInterval() {
        return interval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Activity) {
            Activity that = (Activity) obj;

            return Objects.equals(that.interval.getStart(), interval.getStart())
                    && Objects.equals(that.interval.getEnd(), interval.getEnd())
                    && Objects.equals(that.name, name);
        } else {
            return false;
        }
    }
}
