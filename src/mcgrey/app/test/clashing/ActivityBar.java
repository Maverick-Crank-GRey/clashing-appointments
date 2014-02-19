package mcgrey.app.test.clashing;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: maverick
 * Date: 2/19/14
 * Time: 7:39 AM
 * To change this template use File | Settings | File Templates.
 */
class ActivityBar {
    private Set<TimeSlot> slots = Sets.newHashSet();
    private Activity activity;

    ActivityBar(Activity activity) {
        this.activity = activity;
    }

    public boolean add(TimeSlot slot) {
        return slots.add(slot);
    }
}
