package mcgrey.app.test.clashing;

import junit.framework.Assert;
import org.joda.time.Instant;
import org.junit.Before;
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
public class ActivityBarTest {

    private ActivityBar lunchBar;
    private ActivityBar teaBar;
    private ActivityBar meetingBar;
    private ActivityBar dinnerBar;

    @Before
    public void setUp() {
        final Activity lunch = new Activity(new Instant(0), new Instant(99), "Lunch");
        final Activity tea = new Activity(new Instant(0), new Instant(15), "Tea");
        final Activity meeting = new Activity(new Instant(50), new Instant(199), "Meeting");
        final Activity dinner = new Activity(new Instant(300), new Instant(400), "Dinner");

        lunchBar = new ActivityBar(lunch);
        teaBar = new ActivityBar(tea);
        meetingBar = new ActivityBar(meeting);
        dinnerBar = new ActivityBar(dinner);
    }

    @Test
    public void testCompareTo() throws Exception {
        Assert.assertTrue(lunchBar.compareTo(meetingBar) < 0);
        Assert.assertTrue(meetingBar.compareTo(lunchBar) > 0);

        Assert.assertTrue(dinnerBar.compareTo(lunchBar) > 0);
        Assert.assertTrue(dinnerBar.compareTo(meetingBar) > 0);

        Assert.assertTrue(lunchBar.compareTo(teaBar) < 0);

        SortedSet<ActivityBar> activitySet = new TreeSet<>();

        activitySet.add(meetingBar);
        activitySet.add(teaBar);
        activitySet.add(dinnerBar);
        activitySet.add(lunchBar);

        List<ActivityBar> activityList = new CopyOnWriteArrayList<>(activitySet);

        Assert.assertSame(lunchBar, activityList.get(0));
        Assert.assertSame(teaBar, activityList.get(1));
        Assert.assertSame(meetingBar, activityList.get(2));
        Assert.assertSame(dinnerBar, activityList.get(3));
    }
}
