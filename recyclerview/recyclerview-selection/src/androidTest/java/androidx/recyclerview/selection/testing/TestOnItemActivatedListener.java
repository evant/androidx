/*
 * Copyright 2017 The Android Open Source Project
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

package androidx.recyclerview.selection.testing;

import static org.junit.Assert.assertEquals;

import android.view.MotionEvent;

import androidx.recyclerview.selection.ItemDetailsLookup.ItemDetails;
import androidx.recyclerview.selection.OnItemActivatedListener;

import org.jspecify.annotations.NonNull;

public final class TestOnItemActivatedListener<K> implements OnItemActivatedListener<K> {

    private boolean mReturnValue = true;
    private ItemDetails<K> mActivated;

    @Override
    public boolean onItemActivated(@NonNull ItemDetails<K> item, @NonNull MotionEvent e) {
        mActivated = item;
        return mReturnValue;
    }

    public void setReturnValue(boolean value) {
        mReturnValue = value;
    }

    public void assertActivated(ItemDetails<K> expected) {
        assertEquals(expected, mActivated);
    }
}
