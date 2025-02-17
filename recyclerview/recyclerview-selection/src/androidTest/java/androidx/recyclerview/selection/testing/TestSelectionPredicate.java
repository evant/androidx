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

import androidx.recyclerview.selection.SelectionTracker.SelectionPredicate;

import org.jspecify.annotations.NonNull;

public final class TestSelectionPredicate<K> extends SelectionPredicate<K> {

    private final boolean mMultiSelect;

    private boolean mValue;

    public TestSelectionPredicate(boolean multiSelect) {
        mMultiSelect = multiSelect;
    }

    public TestSelectionPredicate() {
        this(true);
    }

    public void setReturnValue(boolean value) {
        mValue = value;
    }

    @Override
    public boolean canSetStateForKey(@NonNull K key, boolean nextState) {
        return mValue;
    }

    @Override
    public boolean canSetStateAtPosition(int position, boolean nextState) {
        return mValue;
    }

    @Override
    public boolean canSelectMultiple() {
        return mMultiSelect;
    }
}
