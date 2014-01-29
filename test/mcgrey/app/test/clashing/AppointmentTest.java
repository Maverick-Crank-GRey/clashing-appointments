package mcgrey.app.test.clashing;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.junit.Assert;

public class AppointmentTest {
    @org.junit.Test
    public void testClashed() throws Exception {
        Instant start = Instant.now();
        Appointment first = new Appointment(start, Duration.standardHours(1));
        Appointment second = new Appointment(start, Duration.standardHours(2));

        Appointment clashed;

        clashed = first.clashed(second);

        Assert.assertEquals((ReadableInstant) start, clashed.getInterval().getStart());
        Assert.assertEquals((ReadableInstant) start.plus(Duration.standardHours(2)), clashed.getInterval().getEnd());

        Appointment third = new Appointment(start.plus(Duration.standardHours(2)), Duration.standardHours(1));
        clashed = clashed.clashed(third);

        Assert.assertEquals((ReadableInstant) start, clashed.getInterval().getStart());
        Assert.assertEquals((ReadableInstant) start.plus(Duration.standardHours(3)), clashed.getInterval().getEnd());

        Appointment forth = new Appointment(start.minus(Duration.standardHours(2)), Duration.standardHours(1));

        clashed = clashed.clashed(forth);
        Assert.assertNull(clashed);
    }
}
