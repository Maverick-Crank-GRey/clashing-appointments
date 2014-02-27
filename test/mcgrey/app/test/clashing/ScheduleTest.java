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
    private ActivityBar breakfastBar;
    private ActivityBar lunchBar;
    private ActivityBar teaBar;
    private ActivityBar meetingBar;
    private ActivityBar dinnerBar;

    @Before
    public void setUp() {
        final Activity breakfast = new Activity(new Instant(-10), new Instant(10), "Breakfast");
        final Activity lunch = new Activity(new Instant(0), new Instant(99), "Lunch");
        final Activity tea = new Activity(new Instant(0), new Instant(15), "Tea");
        final Activity meeting = new Activity(new Instant(50), new Instant(199), "Meeting");
        final Activity dinner = new Activity(new Instant(300), new Instant(400), "Dinner");

        breakfastBar = new ActivityBar(breakfast);
        lunchBar = new ActivityBar(lunch);
        teaBar = new ActivityBar(tea);
        meetingBar = new ActivityBar(meeting);
        dinnerBar = new ActivityBar(dinner);
    }

    @Test
    public void testGetSlotsBy() {
        final Activity breakfast = new Activity(new Instant(-10), new Instant(10), "Breakfast");
        final Activity lunch = new Activity(new Instant(0), new Instant(99), "Lunch");
        final Activity tea = new Activity(new Instant(0), new Instant(15), "Tea");
        final Activity meeting = new Activity(new Instant(50), new Instant(199), "Meeting");
        final Activity dinner = new Activity(new Instant(300), new Instant(400), "Dinner");

        final ArrayList<Activity> activities = Lists.newArrayList(breakfast, lunch, tea, meeting, dinner);
        Schedule schedule = new Schedule(activities);

        final List<TimeSlot> slotsBy10 = schedule.getSlotsBy(new Duration(10));
        Assert.assertNotNull("The time slots must be created.", slotsBy10);
    }

    @Test
    public void testCreateSlot() throws Exception {
        final TimeSlot slot = Schedule.createTimeSlot(new Interval(0, 99));
        Assert.assertNotNull("The time slot must be created.", slot);

        final TimeSlot sameSlot = Schedule.createTimeSlot(new Interval(0, 99));
        Assert.assertSame("It must be the same slot since it is defined with the same interval.", slot, sameSlot);

        final TimeSlot newSlot = Schedule.createTimeSlot(new Interval(100, 199));
        Assert.assertNotSame("It must be another slot since it is defined with the different interval.", slot, newSlot);
    }

    @Test
    public void testCalculate_BLT_BLMD_MD() {
        final TimeSlot slot0 = new TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot0.add(breakfastBar);
        slot0.add(lunchBar);
        slot0.add(teaBar);

        final TimeSlot slot1 = new TimeSlot(new Interval(new Instant(0), new Instant(99)));
        slot1.add(breakfastBar);
        slot1.add(lunchBar);
        slot1.add(meetingBar);
        slot1.add(dinnerBar);

        final TimeSlot slot2 = new TimeSlot(new Interval(new Instant(100), new Instant(199)));
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
        final TimeSlot slot0 = new TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot0.add(breakfastBar);

        final TimeSlot slot1 = new TimeSlot(new Interval(new Instant(0), new Instant(99)));
        slot1.add(breakfastBar);
        slot1.add(lunchBar);
        slot1.add(teaBar);
        slot1.add(meetingBar);

        final TimeSlot slot2 = new TimeSlot(new Interval(new Instant(100), new Instant(199)));
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
        final TimeSlot slot0 = new TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot0.add(breakfastBar);
        slot0.add(teaBar);

        final TimeSlot slot1 = new TimeSlot(new Interval(new Instant(0), new Instant(99)));
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
        final TimeSlot slot0 = new TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot0.add(teaBar);

        final TimeSlot slot1 = new TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot1.add(breakfastBar);
        slot1.add(teaBar);

        final TimeSlot slot2 = new TimeSlot(new Interval(new Instant(0), new Instant(99)));
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
        final TimeSlot slot0 = new TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot0.add(teaBar);

        final TimeSlot slot1 = new TimeSlot(new Interval(new Instant(-100), new Instant(-1)));
        slot1.add(breakfastBar);
        slot1.add(teaBar);

        final TimeSlot slot2 = new TimeSlot(new Interval(new Instant(0), new Instant(99)));
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
