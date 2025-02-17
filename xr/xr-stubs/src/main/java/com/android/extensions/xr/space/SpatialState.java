/*
 * Copyright 2024 The Android Open Source Project
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

package com.android.extensions.xr.space;

import androidx.annotation.RestrictTo;

/**
 * An interface that represents an activity's spatial state.
 *
 * <p>An object of the class is effectively immutable. Once the object, which is a "snapshot" of the
 * activity's spatial state, is returned to the client, each getters will always return the same
 * value even if the activity's state later changes.
 *
 * @see androidx.xr.extensions.XrExtensions#registerSpatialStateCallback
 */
@SuppressWarnings({"unchecked", "deprecation", "all"})
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public class SpatialState {

    SpatialState() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Gets spatial bounds of the activity. When in full space mode, (infinity, infinity, infinity)
     * is returned.
     *
     * @see androidx.xr.extensions.space.Bounds
     */
    public com.android.extensions.xr.space.Bounds getBounds() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Gets spatial capabilities of the activity. Unlike other capabilities in Android, this may
     * dynamically change based on the current mode the activity is in, whether the activity is the
     * top one in its task, whether the task is the top visible one on the desktop, and so on.
     *
     * @see androidx.xr.extensions.space.SpatialCapabilities
     */
    public com.android.extensions.xr.space.SpatialCapabilities getSpatialCapabilities() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Gets the environment visibility of the activity.
     *
     * @see androidx.xr.extensions.environment.EnvironmentVisibilityState
     */
    public com.android.extensions.xr.environment.EnvironmentVisibilityState
            getEnvironmentVisibility() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Gets the passthrough visibility of the activity.
     *
     * @see androidx.xr.extensions.environment.PassthroughVisibilityState
     */
    public com.android.extensions.xr.environment.PassthroughVisibilityState
            getPassthroughVisibility() {
        throw new RuntimeException("Stub!");
    }

    /**
     * True if the scene node that is currently in use (if any) is the same as targetNode. When
     * targetNode is null, this API returns true when no scene node is currently in use (i.e. the
     * activity is not SPATIAL_UI_CAPABLE, the activity hasn't called attachSpatialScene API at all,
     * or the activity hasn't called it again since the last detachSpatialScene API call.)
     *
     * @see androidx.xr.extensions.attachSpatialScene
     */
    public boolean isActiveSceneNode(com.android.extensions.xr.node.Node targetNode) {
        throw new RuntimeException("Stub!");
    }

    /**
     * True if the window leash node that is currently in use (if any) is the same as targetNode.
     * When targetNode is null, this API returns true when no window leash node is currently in use
     * (i.e. the activity is not SPATIAL_UI_CAPABLE, the activity hasn't called attachSpatialScene
     * API at all, or the activity hasn't called it again since the last detachSpatialScene API
     * call.)
     *
     * @see androidx.xr.extensions.attachSpatialScene
     */
    public boolean isActiveWindowLeashNode(com.android.extensions.xr.node.Node targetNode) {
        throw new RuntimeException("Stub!");
    }

    /**
     * True if the environment node that is currently in use (if any) is the same as targetNode.
     * When targetNode is null, this API returns true when no environment node is currently in use
     * (i.e. the activity is not APP_ENVIRONMENTS_CAPABLE, the activity hasn't called
     * attachSpatialEnvironment API at all, or the activity hasn't called it again since the last
     * detachSpatialEnvironment API call.)
     *
     * <p>Note that as a special case, when isEnvironmentInherited() is true, the API returns false
     * for a null targetNode even if your activity hasn't called attachSpatialEnvironment yet.
     *
     * @see androidx.xr.extensions.attachSpatialEnvironment
     * @see androidx.xr.extensions.setFullSpaceModeWithEnvironmentInherited
     */
    public boolean isActiveEnvironmentNode(com.android.extensions.xr.node.Node targetNode) {
        throw new RuntimeException("Stub!");
    }

    /**
     * True if an activity-provided environment node is currently in use, and the node is one
     * inherited from a different activity.
     *
     * @see androidx.xr.extensions.attachSpatialEnvironment
     * @see androidx.xr.extensions.setFullSpaceModeWithEnvironmentInherited
     */
    public boolean isEnvironmentInherited() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Gets the main window size. (0, 0) is returned when the activity is not SPATIAL_UI_CAPABLE.
     *
     * <p>When the activity is not SPATIAL_UI_CAPABLE, use android.content.res.Configuration to
     * obtain the activity's size.
     *
     * @see androidx.xr.extensions.setMainWindowSize
     * @see android.content.res.Configuration
     */
    public android.util.Size getMainWindowSize() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Gets the main window's aspect ratio preference. 0.0f is returned when there's no preference
     * set via setPreferredAspectRatio API, or the activity is currently SPATIAL_UI_CAPABLE.
     *
     * <p>When SPATIAL_UI_CAPABLE, activities can set a preferred aspect ratio via ReformOptions,
     * but that reform options setting won't be reflected to the value returned from this API.
     *
     * @see androidx.xr.extensions.setPreferredAspectRatio
     * @see androidx.xr.extensions.node.ReformOptions
     */
    public float getPreferredAspectRatio() {
        throw new RuntimeException("Stub!");
    }

    /** equals() */
    public boolean equals(java.lang.Object other) {
        throw new RuntimeException("Stub!");
    }

    /** hashCode() */
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    /** toString() */
    public java.lang.String toString() {
        throw new RuntimeException("Stub!");
    }
}
