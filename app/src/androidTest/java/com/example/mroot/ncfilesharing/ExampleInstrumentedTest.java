package com.example.mroot.ncfilesharing;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import nc.NCUtils;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.

        Log.i("unit test", "this is a test!");
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.mroot.ncfilesharing", appContext.getPackageName());
        System.out.println("device  test run");
        byte[] a = {(byte) 243, 112, 11, 45};
        byte[] b = {12, 111, (byte) 190, 88};
        byte[] c = NCUtils.mul(a, b);
        for (int i = 0; i < c.length; ++i) {
            System.out.println(c[i]+"");
        }
//        System.out.println("device  test run");
    }
}
