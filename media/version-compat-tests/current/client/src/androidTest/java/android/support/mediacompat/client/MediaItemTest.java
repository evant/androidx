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
package android.support.mediacompat.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.os.Parcel;

import androidx.test.filters.SmallTest;

import org.junit.Test;

/** Test {@link android.support.v4.media.MediaBrowserCompat.MediaItem}. */
@SuppressWarnings("deprecation")
public class MediaItemTest {
    private static final String DESCRIPTION = "test_description";
    private static final String MEDIA_ID = "test_media_id";
    private static final String TITLE = "test_title";
    private static final String SUBTITLE = "test_subtitle";

    @Test
    @SmallTest
    public void testBrowsableMediaItem() {
        android.support.v4.media.MediaDescriptionCompat description =
                new android.support.v4.media.MediaDescriptionCompat.Builder()
                        .setDescription(DESCRIPTION)
                        .setMediaId(MEDIA_ID)
                        .setTitle(TITLE)
                        .setSubtitle(SUBTITLE)
                        .build();
        android.support.v4.media.MediaBrowserCompat.MediaItem mediaItem =
                new android.support.v4.media.MediaBrowserCompat.MediaItem(
                        description,
                        android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);

        assertEquals(description.toString(), mediaItem.getDescription().toString());
        assertEquals(MEDIA_ID, mediaItem.getMediaId());
        assertEquals(
                android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE,
                mediaItem.getFlags());
        assertTrue(mediaItem.isBrowsable());
        assertFalse(mediaItem.isPlayable());
        assertEquals(0, mediaItem.describeContents());

        // Test writeToParcel
        Parcel p = Parcel.obtain();
        mediaItem.writeToParcel(p, 0);
        p.setDataPosition(0);
        assertEquals(mediaItem.getFlags(), p.readInt());
        assertEquals(
                description.toString(),
                android.support.v4.media.MediaDescriptionCompat.CREATOR
                        .createFromParcel(p)
                        .toString());
        p.recycle();
    }

    @Test
    @SmallTest
    public void testPlayableMediaItem() {
        android.support.v4.media.MediaDescriptionCompat description =
                new android.support.v4.media.MediaDescriptionCompat.Builder()
                        .setDescription(DESCRIPTION)
                        .setMediaId(MEDIA_ID)
                        .setTitle(TITLE)
                        .setSubtitle(SUBTITLE)
                        .build();
        android.support.v4.media.MediaBrowserCompat.MediaItem mediaItem =
                new android.support.v4.media.MediaBrowserCompat.MediaItem(
                        description,
                        android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

        assertEquals(description.toString(), mediaItem.getDescription().toString());
        assertEquals(MEDIA_ID, mediaItem.getMediaId());
        assertEquals(
                android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE,
                mediaItem.getFlags());
        assertFalse(mediaItem.isBrowsable());
        assertTrue(mediaItem.isPlayable());
        assertEquals(0, mediaItem.describeContents());

        // Test writeToParcel
        Parcel p = Parcel.obtain();
        mediaItem.writeToParcel(p, 0);
        p.setDataPosition(0);
        assertEquals(mediaItem.getFlags(), p.readInt());
        assertEquals(
                description.toString(),
                android.support.v4.media.MediaDescriptionCompat.CREATOR
                        .createFromParcel(p)
                        .toString());
        p.recycle();
    }
}
