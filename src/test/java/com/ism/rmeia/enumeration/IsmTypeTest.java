package com.ism.rmeia.enumeration;

import org.junit.Test;

import junit.framework.Assert;

/**
 * 
 * @author wx
 *
 */
public class IsmTypeTest {

    @Test
    public void testIsUIsmTrue() {
        
        Assert.assertTrue(IsmType.isUIsm("160826900000000"));
    }
    @Test
    public void testIsUIsmFalse() {
        
        Assert.assertFalse(IsmType.isUIsm("1608269"));
        Assert.assertFalse(IsmType.isUIsm("160826812345678"));
    }
}
