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

package androidx.wear.watchface.style.data;

import android.annotation.SuppressLint;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RestrictTo;
import androidx.versionedparcelable.ParcelField;
import androidx.versionedparcelable.ParcelUtils;
import androidx.versionedparcelable.VersionedParcelable;
import androidx.versionedparcelable.VersionedParcelize;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wire format for {@link androidx.wear.watchface.style.UserStyleSetting}.
 *
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@VersionedParcelize
@SuppressLint("BanParcelableUsage") // TODO(b/169214666): Remove Parcelable
public class UserStyleSettingWireFormat implements VersionedParcelable, Parcelable {
    /** Identifier for the element, must be unique. */
    @ParcelField(1)
    public @NonNull String mId = "";

    /** Localized human readable name for the element, used in the userStyle selection UI. */
    @ParcelField(2)
    public @NonNull CharSequence mDisplayName = "";

    /** Localized description string displayed under the displayName. */
    @ParcelField(3)
    public @NonNull CharSequence mDescription = "";

    /** Icon for use in the style selection UI. */
    @ParcelField(4)
    public @Nullable Icon mIcon = null;

    /** The default option index, used if nothing has been selected within the options list. */
    @ParcelField(5)
    public int mDefaultOptionIndex;

    /** Used by the style configuration UI. Describes which rendering layers this style affects. */
    @ParcelField(6)
    public @NonNull List<Integer> mAffectsLayers;

    /**
     * List of options for this UserStyleCategory. Depending on the type of UserStyleCategory this
     * may be an exhaustive list, or just examples to populate a ListView in case the
     * UserStyleCategory isn't supported by the UI (e.g. a new WatchFace with an old Companion).
     *
     * <p>OptionWireFormat can't change because VersionedParcelable has a design flaw, if the format
     * changes the reader can't determine the correct size of the list and data afterwards
     * (including elements of this list) will get corrupted.
     */
    @ParcelField(100)
    public @NonNull List<OptionWireFormat> mOptions = new ArrayList<>();

    /**
     * Flattened list of child settings for each option. Note this ID must always be a list of
     * Integers.
     */
    @ParcelField(101)
    public @Nullable List<Integer> mOptionChildIndices = null;

    /** Contains OnWatchFaceData. */
    @ParcelField(102)
    public @Nullable Bundle mOnWatchFaceEditorBundle = null;

    /**
     * Per option OnWatchFaceData. Ideally this would be in OptionWireFormat, but
     * VersionedParcellable doesn't support us adding that in a backwards compatible way.
     */
    @ParcelField(103)
    public @Nullable List<Bundle> mPerOptionOnWatchFaceEditorBundles = new ArrayList<>();

    // Field 104 is reserved.

    UserStyleSettingWireFormat() {}

    /**
     * @deprecated use a constructor with List<Bundle> perOptionOnWatchFaceEditorBundles.
     */
    @Deprecated
    public UserStyleSettingWireFormat(
            @NonNull String id,
            @NonNull CharSequence displayName,
            @NonNull CharSequence description,
            @Nullable Icon icon,
            @NonNull List<OptionWireFormat> options,
            int defaultOptionIndex,
            @NonNull List<Integer> affectsLayers) {
        mId = id;
        mDisplayName = displayName;
        mDescription = description;
        mIcon = icon;
        mOptions = options;
        mDefaultOptionIndex = defaultOptionIndex;
        mAffectsLayers = affectsLayers;
    }

    public UserStyleSettingWireFormat(
            @NonNull String id,
            @NonNull CharSequence displayName,
            @NonNull CharSequence description,
            @Nullable Icon icon,
            @NonNull List<OptionWireFormat> options,
            int defaultOptionIndex,
            @NonNull List<Integer> affectsLayers,
            @Nullable Bundle onWatchFaceEditorBundle,
            @Nullable List<Bundle> perOptionOnWatchFaceEditorBundles) {
        mId = id;
        mDisplayName = displayName;
        mDescription = description;
        mIcon = icon;
        mOptions = options;
        mDefaultOptionIndex = defaultOptionIndex;
        mAffectsLayers = affectsLayers;
        mOnWatchFaceEditorBundle = onWatchFaceEditorBundle;
        mPerOptionOnWatchFaceEditorBundles = perOptionOnWatchFaceEditorBundles;
    }

    /** Serializes this UserStyleCategoryWireFormat to the specified {@link Parcel}. */
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeParcelable(ParcelUtils.toParcelable(this), flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<UserStyleSettingWireFormat> CREATOR =
            new Parcelable.Creator<UserStyleSettingWireFormat>() {
                @SuppressWarnings("deprecation")
                @Override
                public UserStyleSettingWireFormat createFromParcel(Parcel source) {
                    return ParcelUtils.fromParcelable(
                            source.readParcelable(getClass().getClassLoader()));
                }

                @Override
                public UserStyleSettingWireFormat[] newArray(int size) {
                    return new UserStyleSettingWireFormat[size];
                }
            };
}
