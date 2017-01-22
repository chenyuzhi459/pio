//package io.sugo.pio.ports.quickfix;
//
//import javax.swing.*;
//
//
///**
// * A quick fix that can be used to fix a meta data error.
// */
//public interface QuickFix extends Comparable<QuickFix> {
//
//    public static final int MAX_RATING = 10;
//    public static final int MIN_RATING = 1;
//
//    /**
//     * Get an action to display the quick fix in a menu. The actions actionPerformed() method must
//     * call {@link #apply()} on this object.
//     */
//    public Action getAction();
//
//    /**
//     * Applies the quick fix. May require GUI interaction.
//     *
//     * @throws PortException
//     */
//    public void apply();
//
//    /**
//     * Returns true if the fix requires user interaction. Quick fixes that can be applied
//     * non-interactively can be used to repair a whole process setup.
//     */
//    public boolean isInteractive();
//
//    /**
//     * Returns a number between {@link #MIN_RATING} and {@link #MAX_RATING} that rates the quick fix
//     * with respect to the presumed quality of the obtained solution. Quick fixes with larger rating
//     * will be listed first.
//     */
//    public int getRating();
//
//}
