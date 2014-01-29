package mcgrey.app.test.clashing;

import org.joda.time.*;

public class Appointment {
    private Interval interval;

    public ReadableInterval getInterval() {
        return interval;
    }

    public Appointment(Interval interval) {
        this.interval = interval;
    }

    public Appointment(ReadableInstant start, ReadableInstant end) {
        interval = new Interval(start, end);
    }

    public Appointment(ReadableInstant start, ReadableDuration duration) {
        interval = new Interval(start, duration);
    }

    public Appointment clashed(Appointment tested) {
        boolean overlaps = this.interval.overlaps(tested.interval);
        boolean abuts = this.interval.abuts(tested.interval);

        if (overlaps || abuts) {
            final ReadableInstant thisStart = this.interval.getStart();
            final ReadableInstant testedStart = tested.interval.getStart();

            final ReadableInstant newStart;

            if (thisStart.isBefore(testedStart) || thisStart.isEqual(testedStart)) {
                newStart = thisStart;
            } else {
                newStart = testedStart;
            }

            final DateTime thisEnd = this.interval.getEnd();
            final DateTime testedEnd = tested.interval.getEnd();

            final ReadableInstant newEnd;

            if (thisEnd.isAfter(testedEnd) || thisEnd.isEqual(testedEnd)) {
                newEnd = thisEnd;
            } else {
                newEnd = testedEnd;
            }

            return new Appointment(newStart, newEnd);
        }

        return null;
    }
}
