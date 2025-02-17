/*
 * Copyright (C) 2024 The Android Open Source Project
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

package androidx.xr.extensions.asset;

import androidx.annotation.RestrictTo;

/**
 * Token of a spatial asset cached in the SpaceFlinger.
 *
 * <p>A Node can reference an {@link AssetToken} such that the SpaceFlinger will render the asset
 * inside it.
 *
 * <p>Note that the app needs to keep track of the {@link AssetToken} so that it can continue using
 * it, and eventually, free it when it is no longer needed.
 *
 * @deprecated JXR Core doesn't need this anymore as it does the same with Split Engine.
 */
@Deprecated
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public interface AssetToken {}
