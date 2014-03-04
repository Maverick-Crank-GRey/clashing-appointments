package mcgrey.app.test.clashing;

import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: maverick
 * Date: 2/25/14
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleTest {
    private Schedule.ActivityBar breakfastBar;
    private Schedule.ActivityBar lunchBar;
    private Schedule.ActivityBar teaBar;
    private Schedule.ActivityBar meetingBar;
    private Schedule.ActivityBar dinnerBar;

    @Before
    public void setUp() {
        breakfastBar = new Schedule.ActivityBar(new Activity(new Instant(-10), new Instant(9), "Breakfast"));
        lunchBar = new Schedule.ActivityBar(new Activity(new Instant(0), new Instant(99), "Lunch"));
        teaBar = new Schedule.ActivityBar(new Activity(new Instant(0), new Instant(15), "Tea"));
        meetingBar = new Schedule.ActivityBar(new Activity(new Instant(50), new Instant(199), "Meeting"));
        dinnerBar = new Schedule.ActivityBar(new Activity(new Instant(300), new Instant(400), "Dinner"));
    }

    @Test
    public void testGetSlotsBy() {
        final Activity breakfast = new Activity(new Instant(-10), new Instant(9), "Breakfast");
        final Activity lunch = new Activity(new Instant(0), new Instant(99), "Lunch");
        final Activity tea = new Activity(new Instant(0), new Instant(15), "Tea");
        final Activity meeting = new Activity(new Instant(50), new Instant(199), "Meeting");
        final Activity dinner = new Activity(new Instant(200), new Instant(299), "Dinner");

        final ArrayList<Activity> activities = Lists.newArrayList(breakfast, lunch, tea, meeting, dinner);
        Schedule schedule = new Schedule(activities);

        final Schedule.ScheduleLayout layout = schedule.getSlotsBy(new Duration(10));

        final List<Schedule.TimeSlot> slots = layout.getTimeSlots();
        Assert.assertNotNull("The time slots must be created.", slots);

        final List<Schedule.ActivityBar> activityBars = layout.getActivityBars();
        for (Schedule.ActivityBar bar : activityBars) {
            if (bar.getName().equalsIgnoreCase("Meeting")) {
                Assert.assertEquals(3, bar.getDenominator());
                Assert.assertEquals(2, bar.getNominator());
            }
        }

    }

    @Test
    public void testCreateSlot() throws Exception {
        final Schedule.TimeSlot slot = Schedule.createTimeSlot(new Interval(0, 99));
        Assert.assertNotNull("The time slot must be created.", slot);

        final Schedule.TimeSlot sameSlot = Schedule.createTimeSlot(new Interval(0, 99));
        Assert.assertSame("It must be the same slot since it is defined with the same interval.", slot, sameSlot);

        final Schedule.TimeSlot newSlot = Schedule.createTimeSlot(new Interval(100, 199));
        Assert.assertNotSame("It must be another slot since it is defined with the different interval.", slot, newSlot);
    }

    @Test
    public void testCalculate_BLT_BLMD_MD() {
        final Schedule.TimeSlot slot0 = new Schedule.TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot0.add(breakfastBar);
        slot0.add(lunchBar);
        slot0.add(teaBar);

        final Schedule.TimeSlot slot1 = new Schedule.TimeSlot(new Interval(new Instant(0), new Instant(99)));
        slot1.add(breakfastBar);
        slot1.add(lunchBar);
        slot1.add(meetingBar);
        slot1.add(dinnerBar);

        final Schedule.TimeSlot slot2 = new Schedule.TimeSlot(new Interval(new Instant(100), new Instant(199)));
        slot2.add(meetingBar);
        slot2.add(dinnerBar);

        Schedule.calculate(Lists.newArrayList(slot0, slot1));

        Assert.assertEquals(1, breakfastBar.getNominator());
        Assert.assertEquals(4, breakfastBar.getDenominator());

        Assert.assertEquals(1, lunchBar.getNominator());
        Assert.assertEquals(4, lunchBar.getDenominator());

        Assert.assertEquals(2, teaBar.getNominator());
        Assert.assertEquals(4, teaBar.getDenominator());

        Assert.assertEquals(1, meetingBar.getNominator());
        Assert.assertEquals(4, meetingBar.getDenominator());

        Assert.assertEquals(1, dinnerBar.getNominator());
        Assert.assertEquals(4, dinnerBar.getDenominator());
    }

    @Test
    public void testCalculate_B_BLTM_M() {
        final Schedule.TimeSlot slot0 = new Schedule.TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot0.add(breakfastBar);

        final Schedule.TimeSlot slot1 = new Schedule.TimeSlot(new Interval(new Instant(0), new Instant(99)));
        slot1.add(breakfastBar);
        slot1.add(lunchBar);
        slot1.add(teaBar);
        slot1.add(meetingBar);

        final Schedule.TimeSlot slot2 = new Schedule.TimeSlot(new Interval(new Instant(100), new Instant(199)));
        slot2.add(meetingBar);

        Schedule.calculate(Lists.newArrayList(slot0, slot1));

        Assert.assertEquals(1, breakfastBar.getNominator());
        Assert.assertEquals(4, breakfastBar.getDenominator());

        Assert.assertEquals(1, lunchBar.getNominator());
        Assert.assertEquals(4, lunchBar.getDenominator());

        Assert.assertEquals(1, teaBar.getNominator());
        Assert.assertEquals(4, teaBar.getDenominator());

        Assert.assertEquals(1, meetingBar.getNominator());
        Assert.assertEquals(4, meetingBar.getDenominator());
    }

    @Test
    public void testCalculate_BT_BLM() {
        final Schedule.TimeSlot slot0 = new Schedule.TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot0.add(breakfastBar);
        slot0.add(teaBar);

        final Schedule.TimeSlot slot1 = new Schedule.TimeSlot(new Interval(new Instant(0), new Instant(99)));
        slot1.add(breakfastBar);
        slot1.add(lunchBar);
        slot1.add(meetingBar);

        Schedule.calculate(Lists.newArrayList(slot0, slot1));

        Assert.assertEquals(1, breakfastBar.getNominator());
        Assert.assertEquals(3, breakfastBar.getDenominator());

        Assert.assertEquals(2, teaBar.getNominator());
        Assert.assertEquals(3, teaBar.getDenominator());

        Assert.assertEquals(1, lunchBar.getNominator());
        Assert.assertEquals(3, lunchBar.getDenominator());

        Assert.assertEquals(1, meetingBar.getNominator());
        Assert.assertEquals(3, meetingBar.getDenominator());
    }

    @Test
    public void testCalculate_T_BT_BLM() {
        final Schedule.TimeSlot slot0 = new Schedule.TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot0.add(teaBar);

        final Schedule.TimeSlot slot1 = new Schedule.TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot1.add(breakfastBar);
        slot1.add(teaBar);

        final Schedule.TimeSlot slot2 = new Schedule.TimeSlot(new Interval(new Instant(0), new Instant(99)));
        slot2.add(breakfastBar);
        slot2.add(lunchBar);
        slot2.add(meetingBar);

        Schedule.calculate(Lists.newArrayList(slot0, slot1, slot2));

        Assert.assertEquals(1, breakfastBar.getNominator());
        Assert.assertEquals(3, breakfastBar.getDenominator());

        Assert.assertEquals(2, teaBar.getNominator());
        Assert.assertEquals(3, teaBar.getDenominator());

        Assert.assertEquals(1, lunchBar.getNominator());
        Assert.assertEquals(3, lunchBar.getDenominator());

        Assert.assertEquals(1, meetingBar.getNominator());
        Assert.assertEquals(3, meetingBar.getDenominator());
    }

    @Test
    public void testCalculate_T_BT_BTM() {
        final Schedule.TimeSlot slot0 = new Schedule.TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot0.add(teaBar);

        final Schedule.TimeSlot slot1 = new Schedule.TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot1.add(breakfastBar);
        slot1.add(teaBar);

        final Schedule.TimeSlot slot2 = new Schedule.TimeSlot(new Interval(new Instant(0), new Instant(99)));
        slot2.add(breakfastBar);
        slot2.add(teaBar);
        slot2.add(meetingBar);

        Schedule.calculate(Lists.newArrayList(slot0, slot1, slot2));

        Assert.assertEquals(1, breakfastBar.getNominator());
        Assert.assertEquals(3, breakfastBar.getDenominator());

        Assert.assertEquals(1, teaBar.getNominator());
        Assert.assertEquals(3, teaBar.getDenominator());

        Assert.assertEquals(1, meetingBar.getNominator());
        Assert.assertEquals(3, meetingBar.getDenominator());
    }
}
