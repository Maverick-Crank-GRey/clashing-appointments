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
        final IntervalTree.IntervalData<String> one = new IntervalTree.IntervalData<>(0, 4, "One");
        final IntervalTree.IntervalData<String> two = new IntervalTree.IntervalData<>(2, 6, "Two");
        final IntervalTree.IntervalData<String> three = new IntervalTree.IntervalData<>(6, 10, "Three");

        intervals.add(one);
        intervals.add(two);
        intervals.add(three);

        IntervalTree<String> tree = new IntervalTree<>(intervals);

        final IntervalTree.IntervalData<String> query = tree.query(2, 5);

        Assert.assertNotNull(query);

        final Set<String> values = query.getValues();
        final String[] actual = values.toArray(new String[values.size()]);

        Assert.assertArrayEquals(new String[]{"One", "Two"}, actual);
    }
}
