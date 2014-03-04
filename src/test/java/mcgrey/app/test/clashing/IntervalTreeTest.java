package mcgrey.app.test.clashing;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class IntervalTreeTest {
    @Test
    public void testQuery() throws Exception {
        List<IntervalTree.IntervalData<String>> intervals = new LinkedList<>();

        intervals.add(new IntervalTree.IntervalData<>(0, 4, "One"));
        intervals.add(new IntervalTree.IntervalData<>(2, 6, "Two"));
        intervals.add(new IntervalTree.IntervalData<>(6, 10, "Three"));
        intervals.add(new IntervalTree.IntervalData<>(-100, 100, "Four"));
        intervals.add(new IntervalTree.IntervalData<>(100, 120, "Five"));

        IntervalTree<String> tree = new IntervalTree<>(intervals);
        Assert.assertEquals(-100, tree.getStart());
        Assert.assertEquals(120, tree.getEnd());

        final IntervalTree.IntervalData<String> query = tree.query(2, 5);

        Assert.assertNotNull(query);

        final Set<String> values = query.getValues();
        final String[] actual = values.toArray(new String[values.size()]);

        Assert.assertArrayEquals(new String[]{"Four", "One", "Two"}, actual);
    }

    @Test
    public void testMiddle() {
        Assert.assertEquals(0, mcgrey.app.test.clashing.IntervalTree.IntervalData.middle(0, 0));
        Assert.assertEquals(0, mcgrey.app.test.clashing.IntervalTree.IntervalData.middle(-2, 2));
        Assert.assertEquals(1, mcgrey.app.test.clashing.IntervalTree.IntervalData.middle(0, 2));
        Assert.assertEquals(2, mcgrey.app.test.clashing.IntervalTree.IntervalData.middle(1, 3));
        Assert.assertEquals(-1, mcgrey.app.test.clashing.IntervalTree.IntervalData.middle(-2, 0));
        Assert.assertEquals(-2, mcgrey.app.test.clashing.IntervalTree.IntervalData.middle(-3, -1));
    }
}
