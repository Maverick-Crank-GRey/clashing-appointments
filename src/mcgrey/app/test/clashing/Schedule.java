package mcgrey.app.test.clashing;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;
import org.joda.time.Interval;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInterval;

import java.util.*;

public class Schedule {
    private IntervalTree<Activity> tree;
    private static Map<Interval, TimeSlot> timeSlotCache = new HashMap<>();
    private static Map<Activity, ActivityBar> activityBarCache = new HashMap<>();

    public Schedule(List<Activity> activities) {
        List<IntervalTree.IntervalData<Activity>> intervals = Lists.transform(
                activities,
                new Function<Activity, IntervalTree.IntervalData<Activity>>() {
                    @Override
                    public IntervalTree.IntervalData<Activity> apply(@Nullable Activity input) {
                        if (input != null) {
                            final ReadableInterval interval = input.getInterval();
                            return new IntervalTree.IntervalData<>(interval.getStartMillis(), interval.getEndMillis(), input);
                        } else {
                            return null;
                        }

                    }
                });

        tree = new IntervalTree<>(intervals);
    }

    public List<TimeSlot> getSlotsBy(ReadableDuration slotSize) {
        final long start = tree.getStart();
        final long end = tree.getEnd();

        final List<TimeSlot> result = new LinkedList<>();

        Interval interval = new Interval(start, start + slotSize.getMillis());
        do {
            result.add(getTimeSlot(interval));
            interval = new Interval(interval.getEndMillis(), interval.getEndMillis() + slotSize.getMillis());
        } while (interval.getEndMillis() <= end);

        calculate(result);

        return result;
    }

    TimeSlot getTimeSlot(Interval interval) {
        final TimeSlot timeSlot = createTimeSlot(interval);

        final IntervalTree.IntervalData<Activity> query = tree.query(interval.getStartMillis(), interval.getEndMillis() - 1);
        if (query != null) {
            final Set<Activity> activities = query.getValues();

            for (Activity activity : activities) {
                timeSlot.add(createActivityBar(activity));
            }
        }
        return timeSlot;
    }

    static TimeSlot createTimeSlot(Interval interval) {
        final TimeSlot existingSlot = timeSlotCache.get(interval);

        if (existingSlot == null) {
            final TimeSlot newSlot = new TimeSlot(interval);
            timeSlotCache.put(interval, newSlot);

            return newSlot;
        }

        return existingSlot;
    }

    static ActivityBar createActivityBar(Activity activity) {
        final ActivityBar existingActivityBar = activityBarCache.get(activity);

        if (existingActivityBar == null) {
            final ActivityBar newActivityBar = new ActivityBar(activity);
            activityBarCache.put(activity, newActivityBar);

            return newActivityBar;
        }

        return existingActivityBar;
    }

    static List<TimeSlot> calculate(List<TimeSlot> list) {
        for (TimeSlot slot : list) {
            int denominator = 1;
            for (ActivityBar bar : slot.getMembers()) {
                int max = bar.maxNumberOfNeighbors();
                if (max > denominator) {
                    denominator = max;
                }

                int nominator = 1;
                int max_n = denominator - slot.getMembers().size() + 1;
                if (max_n > nominator) {
                    nominator = max_n;
                }

                bar.setMetricis(nominator, denominator);
            }
        }

        return list;
    }

}
