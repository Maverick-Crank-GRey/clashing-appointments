package mcgrey.app.test.clashing;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.joda.time.Interval;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInterval;

import java.util.*;

/**
 * This is a schedule that contains activities. You have to instantiate it with a list of activities.
 * {@link #getSlotsBy(org.joda.time.ReadableDuration)} returns the list of time slots that contain activity bars
 * with calculated metrics.
 */
public class Schedule {
    /**
     * The interval tree data structure that helps in slicing activities into time slots.
     */
    private IntervalTree<Activity> tree;

    /**
     * The time slots index.
     */
    private static Map<Interval, TimeSlot> timeSlotCache = new HashMap<>();

    /**
     * The activity bars index.
     */
    private static Map<Activity, ActivityBar> activityBarCache = new HashMap<>();

    /**
     * Instantiates the schedule populated with activities.
     *
     * @param activities The list of activities. The order doesn't matter.
     */
    public Schedule(List<Activity> activities) {
        List<IntervalTree.IntervalData<Activity>> intervals = Lists.transform(
                activities,
                new Function<Activity, IntervalTree.IntervalData<Activity>>() {
                    @Override
                    public IntervalTree.IntervalData<Activity> apply(Activity input) {
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

    public ScheduleLayout getSlotsBy(ReadableDuration slotSize) {
        final long start = tree.getStart();
        final long end = tree.getEnd();

        // Flash indexes
        timeSlotCache.clear();
        activityBarCache.clear();

        final List<TimeSlot> result = new LinkedList<>();

        Interval timeSlotInterval = new Interval(start, start + slotSize.getMillis());
        do {
            result.add(getTimeSlot(timeSlotInterval));
            timeSlotInterval = new Interval(timeSlotInterval.getEndMillis(), timeSlotInterval.getEndMillis() + slotSize.getMillis());
        } while (timeSlotInterval.getEndMillis() <= end);

        calculate(result);

        Iterable<ActivityBar> bars = Iterables.transform(activityBarCache.entrySet(),
                new Function<Map.Entry<Activity, ActivityBar>, ActivityBar>() {
                    @Override
                    public ActivityBar apply(Map.Entry<Activity, ActivityBar> input) {
                        return input.getValue();
                    }
                });

        return new ScheduleLayout(slotSize, result, bars);
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

    /**
     * Create the
     *
     * @param interval
     * @return
     */
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

    /**
     * Calculates the layout of activity bars.
     *
     * @param list The time slot list.
     */
    static void calculate(List<TimeSlot> list) {
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

                bar.setMetrics(nominator, denominator);
            }
        }
    }

    /**
     * This class describes the schedule layout.
     */
    public class ScheduleLayout {
        private ReadableDuration timeSlotSize;
        private List<TimeSlot> timeSlots;
        private List<ActivityBar> activityBars;

        public ScheduleLayout(ReadableDuration timeSlotSize,
                              Iterable<TimeSlot> timeSlots,
                              Iterable<ActivityBar> activityBars) {
            this.timeSlotSize = timeSlotSize;
            this.timeSlots = ImmutableList.copyOf(timeSlots);
            this.activityBars = ImmutableList.copyOf(activityBars);
        }

        public ReadableDuration getTimeSlotSize() {
            return timeSlotSize;
        }

        public List<TimeSlot> getTimeSlots() {
            return timeSlots;
        }

        public List<ActivityBar> getActivityBars() {
            return activityBars;
        }
    }

    /**
     * This is a representation of the activity on the schedule.
     */
    static class ActivityBar implements Comparable<ActivityBar> {
        private Set<TimeSlot> slots = new HashSet<>();
        private Activity activity;
        private int nominator = Integer.MAX_VALUE;
        private int denominator = Integer.MIN_VALUE;
        private Integer maxNumberOfNeighbors = null;

        ActivityBar(Activity activity) {
            this.activity = activity;
        }

        public boolean add(TimeSlot slot) {
            // drop the number
            maxNumberOfNeighbors = null;
            return slots.add(slot);
        }

        public int maxNumberOfNeighbors() {
            if (maxNumberOfNeighbors == null) {
                maxNumberOfNeighbors = 0;  // alone

                for (TimeSlot slot : slots) {
                    final int size = slot.getMembers().size();
                    if (size > maxNumberOfNeighbors) {
                        maxNumberOfNeighbors = size;
                    }
                }
            }
            return maxNumberOfNeighbors;
        }

        public void setMetrics(int nominator, int denominator) {
            if (nominator < this.nominator) {
                this.nominator = nominator;
            }
            if (denominator > this.denominator) {
                this.denominator = denominator;
            }
        }

        public int getNominator() {
            return nominator;
        }

        public int getDenominator() {
            return denominator;
        }

        public String getName() {
            return activity.getName();
        }

        @Override
        public String toString() {
            return String.format("'%s': %d/%d", activity.getName(), nominator, denominator);
        }

        @Override
        public int compareTo(ActivityBar that) {
            return this.activity.compareTo(that.activity);
        }
    }

    /**
     * This class represents the time slot on the schedule.
     */
    public static class TimeSlot {
        private final Interval interval;
        private final SortedSet<ActivityBar> members = new TreeSet<>();

        public TimeSlot(Interval interval) {
            this.interval = interval;
        }

        public void add(ActivityBar activityBar) {
            activityBar.add(this);
            members.add(activityBar);
        }

        public Set<ActivityBar> getMembers() {
            return members;
        }

        public Interval getInterval() {
            return interval;
        }
    }
}
