package mcgrey.app.test.clashing;

import junit.framework.Assert;
import org.joda.time.Instant;
import org.junit.Test;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: maverick
 * Date: 2/19/14
 * Time: 10:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityTest {

    @Test
    public void testCompareTo() throws Exception {
        final Activity lunch = new Activity(new Instant(0), new Instant(99), "Lunch");
        final Activity tea = new Activity(new Instant(0), new Instant(15), "Tea");
        final Activity meeting = new Activity(new Instant(50), new Instant(199), "Meeting");
        final Activity dinner = new Activity(new Instant(300), new Instant(400), "Dinner");

        Assert.assertTrue(lunch.compareTo(meeting) < 0);
        Assert.assertTrue(meeting.compareTo(lunch) > 0);

        Assert.assertTrue(dinner.compareTo(lunch) > 0);
        Assert.assertTrue(dinner.compareTo(meeting) > 0);

        Assert.assertTrue(lunch.compareTo(tea) < 0);

        SortedSet<Activity> activitySet = new TreeSet<>();

        activitySet.add(meeting);
        activitySet.add(tea);
        activitySet.add(dinner);
        activitySet.add(lunch);

        List<Activity> activityList = new CopyOnWriteArrayList<>(activitySet);

        Assert.assertSame(lunch, activityList.get(0));
        Assert.assertSame(tea, activityList.get(1));
        Assert.assertSame(meeting, activityList.get(2));
        Assert.assertSame(dinner, activityList.get(3));
    }
}
