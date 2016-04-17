/*
 * Android SDK for Piwik
 *
 * @link https://github.com/piwik/piwik-android-sdk
 * @license https://github.com/piwik/piwik-sdk-android/blob/master/LICENSE BSD-3 Clause
 */

package org.piwik.sdk;

import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.piwik.sdk.testhelper.FullEnvTestRunner;
import org.piwik.sdk.testhelper.PiwikTestApplication;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@Config(emulateSdk = 18, manifest = Config.NONE)
@RunWith(FullEnvTestRunner.class)
public class PiwikApplicationTest {

    @Test
    public void testNewTracker() throws Exception {
        PiwikTestApplication app = (PiwikTestApplication) Robolectric.application;
        Tracker tracker = Piwik.getInstance(Robolectric.application).newTracker(app.getTrackerUrl(), app.getSiteId());
        assertNotNull(tracker);
        assertEquals(new URL(app.getTrackerUrl() + "/piwik.php"), tracker.getAPIUrl());
        assertEquals(app.getSiteId(), Integer.valueOf(tracker.getSiteId()));
    }

    @Test
    public void testOptions() throws Exception {
        Piwik piwik = Piwik.getInstance(Robolectric.application);

        piwik.setDryRun(true);
        piwik.setOptOut(true);
        piwik.setDebug(true);

        assertTrue(piwik.isDryRun());
        assertTrue(piwik.isOptOut());
        assertTrue(piwik.isDebug());

        piwik.setDryRun(false);
        piwik.setOptOut(false);
        piwik.setDebug(false);

        assertFalse(piwik.isDryRun());
        assertFalse(piwik.isOptOut());
        assertFalse(piwik.isDebug());
    }

    @Test
    public void testLowMemoryDispatch() throws Exception {
        PiwikTestApplication app = (PiwikTestApplication) Robolectric.application;
        app.getPiwik().setDryRun(true);
        Tracker tracker = app.getTracker();
        assertNotNull(tracker);
        tracker.setDispatchInterval(-1);
        tracker.track(TrackHelper.track().screen("test").build());
        Thread.sleep(50);
        assertTrue(tracker.getDispatcher().getDryRunOutput().isEmpty());
        app.onTrimMemory(Application.TRIM_MEMORY_UI_HIDDEN);
        Thread.sleep(50);
        assertFalse(tracker.getDispatcher().getDryRunOutput().isEmpty());
    }
}