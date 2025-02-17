/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.wear.watchface.complications.rendering.utils;

import static androidx.wear.watchface.complications.rendering.utils.LayoutUtils.getCentralSquare;

import android.graphics.Rect;
import android.support.wearable.complications.ComplicationData;

import androidx.annotation.RestrictTo;

import org.jspecify.annotations.NonNull;

/**
 * Layout helper for {@link ComplicationData#TYPE_ICON}.
 *
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class IconLayoutHelper extends LayoutHelper {

    @Override
    public void getIconBounds(@NonNull Rect outRect) {
        getBounds(outRect);
        getCentralSquare(outRect, outRect);
    }
}
