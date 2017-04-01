package com.ism.rmeia.day.strategy;


import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Created by wx on 2016/10/20.
 * add time test
 */
public class AbstractStrategyTest {

    @Test
    public void testAfter() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,14);
        calendar.set(Calendar.MINUTE,44);
        assertFalse(AbstractStrategy.after(calendar.getTimeInMillis(), 14, 45));
        assertFalse(AbstractStrategy.after(calendar.getTimeInMillis(), 15, 0));

        assertTrue(AbstractStrategy.after(calendar.getTimeInMillis(), 14, 0));
        assertTrue(AbstractStrategy.after(calendar.getTimeInMillis(), 13, 59));
    }
}