package mcgrey.app.test.clashing;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;

import java.util.List;
import java.util.Set;

public class Schedule {
    private IntervalTree<Activity> tree;

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

    public TimeSlot getSlot(ReadableInstant start, ReadableInstant end) {
        final IntervalTree.IntervalData<Activity> query = tree.query(start.getMillis(), end.getMillis() - 1);
        final Set<Activity> activities = query.getValues();

        final TimeSlot timeSlot = TimeSlot.createSlot(start, end);

        for (Activity activity : activities) {
            timeSlot.add(new ActivityBar(activity));
        }
        return timeSlot;
    }
}
