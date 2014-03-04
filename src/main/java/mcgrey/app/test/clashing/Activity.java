package mcgrey.app.test.clashing;

import org.joda.time.Interval;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;

import java.util.Objects;

/**
 * This is a simple wrapper against {@link Interval} that also contains a name of the activity.
 */
public class Activity implements Comparable<Activity> {
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

    public String getName() {
        return name;
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

    @Override
    public String toString() {
        return String.format("'%s' from [%s] to [%s]", name, interval.getStart(), interval.getEnd());
    }

    @Override
    public int compareTo(Activity that) {
        if (that == null) {
            throw new NullPointerException("It is impossible to compare with NULL!");
        }

        final ReadableInterval thisInterval = this.getInterval();
        final ReadableInterval thatInterval = that.getInterval();

        final long thisStart = thisInterval.getStartMillis();
        final long thatStart = thatInterval.getStartMillis();
        final long startDelta = thisStart - thatStart;

        final long thisEnd = thisInterval.getEndMillis();
        final long thatEnd = thatInterval.getEndMillis();
        final long endDelta = thisEnd - thatEnd;

        if (startDelta > 0) {
            return 1;
        } else if (startDelta < 0) {
            return -1;
        } else { // startDelta == 0
            if (endDelta > 0) {
                return -1;
            } else if (endDelta < 0) {
                return 1;
            }
        }

        return 0;
    }

}
