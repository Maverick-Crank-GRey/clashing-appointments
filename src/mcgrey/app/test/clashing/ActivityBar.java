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
class ActivityBar implements Comparable<ActivityBar> {
    private Set<TimeSlot> slots = Sets.newHashSet();
    private Activity activity;
    private int nominator = Integer.MAX_VALUE;
    private int denominator = Integer.MAX_VALUE;

    ActivityBar(Activity activity) {
        this.activity = activity;
    }

    public boolean add(TimeSlot slot) {
        return slots.add(slot);
    }

    public Set<TimeSlot> getSlots() {
        return slots;
    }

    public int maxNumberOfNeighbors() {
        int max = 0;
        for (TimeSlot slot : slots) {
            final int size = slot.getMembers().size();
            if (size > max) {
                max = size;
            }
        }
        return max;
    }

    public void setMetricis(int nominator, int denominator) {
        if (nominator < this.nominator) {
            this.nominator = nominator;
        }
        this.denominator = denominator;
    }

    public int getNominator() {
        return nominator;
    }

    public int getDenominator() {
        return denominator;
    }

    @Override
    public int compareTo(ActivityBar that) {
        return this.activity.compareTo(that.activity);
    }

    @Override
    public String toString() {
        return activity.toString();
    }
}
