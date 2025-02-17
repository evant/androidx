/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.core.os;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for {@link BuildCompat}.
 */
@SuppressWarnings("deprecation")
@RunWith(AndroidJUnit4.class)
@SmallTest
public class BuildCompatTest {
    @Test
    public void isAtLeastPreReleaseCodename() {
        assertTrue(BuildCompat.isAtLeastPreReleaseCodename("S", "S"));
        assertTrue(BuildCompat.isAtLeastPreReleaseCodename("S", "Tiramisu"));
        assertTrue(BuildCompat.isAtLeastPreReleaseCodename("Tiramisu", "Tiramisu"));
        assertFalse(BuildCompat.isAtLeastPreReleaseCodename("Tiramisu", "S"));

        assertTrue(BuildCompat.isAtLeastPreReleaseCodename("O", "OMR1"));
        assertFalse(BuildCompat.isAtLeastPreReleaseCodename("OMR1", "O"));

        assertTrue(BuildCompat.isAtLeastPreReleaseCodename("OMR1", "OMR1"));
        assertTrue(BuildCompat.isAtLeastPreReleaseCodename("OMR1", "OMR2"));
        assertFalse(BuildCompat.isAtLeastPreReleaseCodename("OMR2", "OMR1"));

        assertFalse(BuildCompat.isAtLeastPreReleaseCodename("S", "REL"));

        assertFalse(BuildCompat.isAtLeastPreReleaseCodename("RMR1", "REL"));

        assertFalse(BuildCompat.isAtLeastPreReleaseCodename("RMR1", "REL"));

        assertTrue(BuildCompat.isAtLeastPreReleaseCodename("VanillaIceCream", "VanillaIceCream"));
        assertTrue(BuildCompat.isAtLeastPreReleaseCodename("VanillaIceCream", "Baklava"));
        assertTrue(BuildCompat.isAtLeastPreReleaseCodename("Baklava", "Baklava"));
        assertFalse(BuildCompat.isAtLeastPreReleaseCodename("Baklava", "VanillaIceCream"));
    }

    @Test
    public void extensionConstants() {
        if (!BuildCompat.isAtLeastR()) {
            assertEquals(0, BuildCompat.R_EXTENSION_INT);
            assertEquals(0, BuildCompat.S_EXTENSION_INT);
        }
        if (BuildCompat.isAtLeastS()) {
            assertTrue(BuildCompat.R_EXTENSION_INT >= 1);
            assertTrue(BuildCompat.S_EXTENSION_INT >= 1);
        }
    }

    @SdkSuppress(minSdkVersion = 33)
    @Test
    public void isAtLeastT_byMinSdk() {
        assertTrue(BuildCompat.isAtLeastT());
    }

    @SdkSuppress(minSdkVersion = 34)
    @Test
    public void isAtLeastU_byMinSdk() {
        assertTrue(BuildCompat.isAtLeastU());
    }

    @SdkSuppress(minSdkVersion = 35)
    @Test
    public void isAtLeastV_byMinSdk() {
        assertTrue(BuildCompat.isAtLeastV());
    }

    @SdkSuppress(minSdkVersion = 36)
    @Test
    public void isAtLeastB_byMinSdk() {
        assertTrue(BuildCompat.isAtLeastB());
    }
}
