/*
 * Copyright 2023 The Android Open Source Project
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

package androidx.camera.video.internal.workaround;

import static java.util.Objects.requireNonNull;

import android.util.Size;

import androidx.camera.core.impl.EncoderProfilesProvider;
import androidx.camera.core.impl.EncoderProfilesProxy;
import androidx.camera.core.impl.EncoderProfilesProxy.ImmutableEncoderProfilesProxy;
import androidx.camera.core.impl.EncoderProfilesProxy.VideoProfileProxy;
import androidx.camera.core.impl.Quirks;
import androidx.camera.video.internal.compat.quirk.StretchedVideoResolutionQuirk;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation that changes matching video resolution of qualities to values supported by
 * camera.
 *
 * @see StretchedVideoResolutionQuirk
 */
public class QualityResolutionModifiedEncoderProfilesProvider implements EncoderProfilesProvider {

    private final @NonNull EncoderProfilesProvider mProvider;
    private final @NonNull Quirks mQuirks;
    private final @NonNull Map<Integer, EncoderProfilesProxy> mEncoderProfilesCache =
            new HashMap<>();

    public QualityResolutionModifiedEncoderProfilesProvider(
            @NonNull EncoderProfilesProvider provider, @NonNull Quirks quirks) {
        mProvider = provider;
        mQuirks = quirks;
    }

    @Override
    public boolean hasProfile(int quality) {
        if (!mProvider.hasProfile(quality)) {
            return false;
        }

        return getProfilesInternal(quality) != null;
    }

    @Override
    public @Nullable EncoderProfilesProxy getAll(int quality) {
        return getProfilesInternal(quality);
    }

    private @Nullable EncoderProfilesProxy getProfilesInternal(int quality) {
        if (mEncoderProfilesCache.containsKey(quality)) {
            return mEncoderProfilesCache.get(quality);
        }


        EncoderProfilesProxy profiles = null;
        if (mProvider.hasProfile(quality)) {
            EncoderProfilesProxy baseProfiles = requireNonNull(mProvider.getAll(quality));

            // Apply alternative resolution if existed, leave keep the encoder profiles unchanged.
            Size alternativeResolution = getAlternativeResolution(quality);
            profiles = (alternativeResolution == null) ? baseProfiles : createNewEncoderProfiles(
                    baseProfiles, alternativeResolution);
        }
        mEncoderProfilesCache.put(quality, profiles);

        return profiles;
    }

    private @Nullable Size getAlternativeResolution(int quality) {
        for (StretchedVideoResolutionQuirk quirk : mQuirks.getAll(
                StretchedVideoResolutionQuirk.class)) {
            if (quirk != null) {
                return quirk.getAlternativeResolution(quality);
            }
        }

        return null;
    }

    private @Nullable EncoderProfilesProxy createNewEncoderProfiles(
            @NonNull EncoderProfilesProxy baseProfiles, @NonNull Size resolution) {
        // Apply input resolution to video profiles.
        List<VideoProfileProxy> newVideoProfiles = new ArrayList<>();
        for (VideoProfileProxy videoProfile : baseProfiles.getVideoProfiles()) {
            VideoProfileProxy newVideoProfile = generateVideoProfile(videoProfile, resolution);
            newVideoProfiles.add(newVideoProfile);
        }

        return newVideoProfiles.isEmpty() ? null : ImmutableEncoderProfilesProxy.create(
                baseProfiles.getDefaultDurationSeconds(),
                baseProfiles.getRecommendedFileFormat(),
                baseProfiles.getAudioProfiles(),
                newVideoProfiles
        );
    }

    private static @NonNull VideoProfileProxy generateVideoProfile(
            @NonNull VideoProfileProxy baseProfile, @NonNull Size resolution) {
        return VideoProfileProxy.create(
                baseProfile.getCodec(),
                baseProfile.getMediaType(),
                baseProfile.getBitrate(),
                baseProfile.getFrameRate(),
                resolution.getWidth(),
                resolution.getHeight(),
                baseProfile.getProfile(),
                baseProfile.getBitDepth(),
                baseProfile.getChromaSubsampling(),
                baseProfile.getHdrFormat()
        );
    }
}
