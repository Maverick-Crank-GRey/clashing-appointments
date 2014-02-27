package mcgrey.app.test.clashing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

import java.security.InvalidParameterException;
import java.util.*;

/**
 * An interval tree is an ordered tree data structure to hold intervals.
 * Specifically, it allows one to efficiently find all intervals that overlap
 * with any given interval or point.
 * <p/>
 * http://en.wikipedia.org/wiki/Interval_tree
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class IntervalTree<O> {

    private Interval<O> root = null;
    private long start;
    private long end;

    private static final Ordering<IntervalData<?>> byMiddle = new Ordering<IntervalData<?>>() {
        public int compare(IntervalData<?> left, IntervalData<?> right) {
            if (left.middle < right.middle)
                return -1;
            if (left.middle > right.middle)
                return 1;
            return 0;
        }
    };

    private static final Comparator<IntervalData<?>> startComparator = new Comparator<IntervalData<?>>() {

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(IntervalData<?> left, IntervalData<?> right) {
            // Compare start first
            if (left.start < right.start)
                return -1;
            if (right.start < left.start)
                return 1;
            return 0;
        }
    };

    private static final Ordering<IntervalData<?>> byStart = Ordering.from(startComparator);

    private static final Comparator<IntervalData<?>> endComparator = new Comparator<IntervalData<?>>() {

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(IntervalData<?> left, IntervalData<?> right) {
            // Compare end first
            if (left.end < right.end)
                return -1;
            if (right.end < left.end)
                return 1;
            return 0;
        }
    };

    private static final Ordering<IntervalData<?>> byEnd = Ordering.from(endComparator);

    /**
     * Create interval tree from list of IntervalData objects;
     *
     * @param intervals is a list of IntervalData objects
     */
    public IntervalTree(List<IntervalData<O>> intervals) {
        if (intervals.size() <= 0)
            return;

        final List<IntervalData<O>> copy = byMiddle.sortedCopy(intervals);
        root = createFromList(copy);
        start = byStart.min(intervals).start;
        end = byEnd.max(intervals).end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    protected static <O> Interval<O> createFromList(List<IntervalData<O>> intervals) {
        Interval<O> newInterval = new Interval<>();
        if (intervals.size() == 1) {
            IntervalData<O> middle = intervals.get(0);
            newInterval.center = IntervalData.middle(middle.start, middle.end);
            newInterval.add(middle);
        } else {
            int half = intervals.size() / 2;
            IntervalData<O> middle = intervals.get(half);
            newInterval.center = IntervalData.middle(middle.start, middle.end);
            List<IntervalData<O>> leftIntervals = new ArrayList<>();
            List<IntervalData<O>> rightIntervals = new ArrayList<>();
            for (IntervalData<O> interval : intervals) {
                if (interval.end < newInterval.center) {
                    leftIntervals.add(interval);
                } else if (interval.start > newInterval.center) {
                    rightIntervals.add(interval);
                } else {
                    newInterval.add(interval);
                }
            }
            if (leftIntervals.size() > 0)
                newInterval.left = createFromList(leftIntervals);
            if (rightIntervals.size() > 0)
                newInterval.right = createFromList(rightIntervals);
        }
        return newInterval;
    }

    /**
     * Stabbing query
     *
     * @param index to query for.
     * @return data at index.
     */
    public IntervalData<O> query(long index) {
        return root.query(index);
    }

    /**
     * Range query
     *
     * @param start of range to query for.
     * @param end   of range to query for.
     * @return data for range.
     */
    public IntervalData<O> query(long start, long end) {
        return root.query(start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(IntervalTreePrinter.getString(this));
        return builder.toString();
    }

    protected static class IntervalTreePrinter {

        public static <O> String getString(IntervalTree<O> tree) {
            if (tree.root == null)
                return "Tree has no nodes.";
            return getString(tree.root, "", true);
        }

        private static <O> String getString(Interval<O> interval, String prefix, boolean isTail) {
            StringBuilder builder = new StringBuilder();

            builder.append(prefix).append(isTail ? "└── " : "├── ").append(interval.toString()).append("\n");
            List<Interval<O>> children = new ArrayList<>();
            if (interval.left != null)
                children.add(interval.left);
            if (interval.right != null)
                children.add(interval.right);
            if (children.size() > 0) {
                for (int i = 0; i < children.size() - 1; i++) {
                    builder.append(getString(children.get(i), prefix + (isTail ? "    " : "│   "), false));
                }
                if (children.size() > 0) {
                    builder.append(getString(children.get(children.size() - 1), prefix + (isTail ? "    " : "│   "),
                            true));
                }
            }

            return builder.toString();
        }
    }

    public static final class Interval<O> {

        private long center = Long.MIN_VALUE;
        private Interval<O> left = null;
        private Interval<O> right = null;
        private List<IntervalData<O>> overlap = new ArrayList<>(); // startComparator

        private void add(IntervalData<O> data) {
            overlap.add(data);
            Collections.sort(overlap, startComparator);
        }

        /**
         * Stabbing query
         *
         * @param index to query for.
         * @return data at index.
         */
        public IntervalData<O> query(long index) {
            IntervalData<O> results = null;
            if (index < center) {
                // overlap is sorted by start point
                for (IntervalData<O> data : overlap) {
                    if (data.start > index)
                        break;

                    IntervalData<O> temp = data.query(index);
                    if (results == null && temp != null)
                        results = temp;
                    else if (temp != null)
                        results.combined(temp);
                }
            } else if (index >= center) {
                // overlapEnd is sorted by end point
                List<IntervalData<O>> overlapEnd = new ArrayList<>();
                Collections.sort(overlapEnd, endComparator);
                overlapEnd.addAll(overlap);
                for (IntervalData<O> data : overlapEnd) {
                    if (data.end < index)
                        break;

                    IntervalData<O> temp = data.query(index);
                    if (results == null && temp != null)
                        results = temp;
                    else if (temp != null)
                        results.combined(temp);
                }
            }
            if (index < center) {
                if (left != null) {
                    IntervalData<O> temp = left.query(index);
                    if (results == null && temp != null)
                        results = temp;
                    else if (temp != null)
                        results.combined(temp);
                }
            } else if (index >= center) {
                if (right != null) {
                    IntervalData<O> temp = right.query(index);
                    if (results == null && temp != null)
                        results = temp;
                    else if (temp != null)
                        results.combined(temp);
                }
            }
            return results;
        }

        /**
         * Range query
         *
         * @param start of range to query for.
         * @param end   of range to query for.
         * @return data for range.
         */
        public IntervalData<O> query(long start, long end) {
            IntervalData<O> results = null;
            for (IntervalData<O> data : overlap) {
                if (data.start > end)
                    break;
                IntervalData<O> temp = data.query(start, end);
                if (results == null && temp != null)
                    results = temp;
                else if (temp != null)
                    results.combined(temp);
            }
            if (left != null && start < center) {
                IntervalData<O> temp = left.query(start, end);
                if (temp != null && results == null)
                    results = temp;
                else if (temp != null)
                    results.combined(temp);
            }
            if (right != null && end >= center) {
                IntervalData<O> temp = right.query(start, end);
                if (temp != null && results == null)
                    results = temp;
                else if (temp != null)
                    results.combined(temp);
            }
            return results;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Center=").append(center);
            builder.append(" Set=").append(overlap);
            return builder.toString();
        }
    }

    /**
     * Data structure representing an interval.
     */
    public static final class IntervalData<O> implements Comparable<IntervalData<O>> {

        private final long middle;
        private long start = Long.MIN_VALUE;
        private long end = Long.MAX_VALUE;
        private Set<O> set = new TreeSet<>(); // Sorted

        /**
         * Interval data using O as it's unique identifier
         *
         * @param object Object which defines the interval data
         */
        public IntervalData(long index, O object) {
            this.start = index;
            this.end = index;
            this.middle = index;
            this.set.add(object);
        }

        /**
         * Interval data using O as it's unique identifier
         *
         * @param object Object which defines the interval data
         */
        public IntervalData(long start, long end, O object) {
            this.start = start;
            this.end = end;
            this.middle = middle(start, end);
            this.set.add(object);
        }

        static long middle(long start, long end) {
            return (start + end) / 2;
        }

        /**
         * Interval data set which should all be unique
         *
         * @param set of interval data objects
         */
        public IntervalData(long start, long end, Set<O> set) {
            this.start = start;
            this.end = end;
            this.middle = middle(start, end);
            this.set = set;

            // Make sure they are unique
            Iterator<O> iter = set.iterator();
            while (iter.hasNext()) {
                O obj1 = iter.next();
                O obj2 = null;
                if (iter.hasNext())
                    obj2 = iter.next();
                if (obj1.equals(obj2))
                    throw new InvalidParameterException("Each interval data in the list must be unique.");
            }
        }

        public Set<O> getValues() {
            return ImmutableSet.copyOf(set);
        }

        /**
         * Clear the indices.
         */
        public void clear() {
            this.start = Long.MIN_VALUE;
            this.end = Long.MAX_VALUE;
            this.set.clear();
        }

        /**
         * Combined this IntervalData with data.
         *
         * @param data to combined with.
         * @return Data which represents the combination.
         */
        public IntervalData<O> combined(IntervalData<O> data) {
            if (data.start < this.start)
                this.start = data.start;
            if (data.end > this.end)
                this.end = data.end;
            this.set.addAll(data.set);
            return this;
        }

        /**
         * Deep copy of data.
         *
         * @return deep copy.
         */
        public IntervalData<O> copy() {
            Set<O> listCopy = new TreeSet<>();
            listCopy.addAll(set);
            return new IntervalData<>(start, end, listCopy);
        }

        /**
         * Query inside this data object.
         *
         * @param index of range to query for.
         * @return Data queried for or NULL if it doesn't match the query.
         */
        public IntervalData<O> query(long index) {
            if (index >= this.start && index <= this.end) {
                return copy();
            }
            return null;
        }

        /**
         * Query inside this data object.
         *
         * @param start of range to query for.
         * @param end   of range to query for.
         * @return Data queried for or NULL if it doesn't match the query.
         */
        public IntervalData<O> query(long start, long end) {
            if (end < this.start || start > this.end) {
                // Ignore
            } else {
                return copy();
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IntervalData))
                return false;
            @SuppressWarnings("unchecked")
            IntervalData<O> data = (IntervalData<O>) obj;
            if (this.start == data.start && this.end == data.end) {
                if (this.set.size() != data.set.size())
                    return false;
                for (O o : set) {
                    if (!data.set.contains(o))
                        return false;
                }
                return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(IntervalData<O> d) {
            if (this.end < d.end)
                return -1;
            if (d.end < this.end)
                return 1;
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(start).append("->").append(end);
            builder.append(" set=").append(set);
            return builder.toString();
        }
    }
}