/*
 * Copyright 2022 The Android Open Source Project
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

package androidx.camera.core.imagecapture;

import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageInfo;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.ImmutableImageInfo;
import androidx.camera.core.SettableImageProxy;
import androidx.camera.core.processing.Operation;
import androidx.camera.core.processing.Packet;

import org.jspecify.annotations.NonNull;

/**
 * Produces a {@link ImageProxy} as in-memory capture result.
 *
 * <p>Updates the input {@link ImageProxy}'s metadata based on the info in the {@link Packet}.
 * The metadata in the {@link Packet} should be correct at this stage. The quirks should be handled
 * in the {@link ProcessingInput2Packet} processor, and the transformation info should be updated
 * by upstream processors.
 */
public class JpegImage2Result implements Operation<Packet<ImageProxy>, ImageProxy> {

    @Override
    public @NonNull ImageProxy apply(@NonNull Packet<ImageProxy> input)
            throws ImageCaptureException {
        ImageProxy image = input.getData();

        ImageInfo imageInfo = ImmutableImageInfo.create(
                image.getImageInfo().getTagBundle(),
                image.getImageInfo().getTimestamp(),
                input.getRotationDegrees(),
                input.getSensorToBufferTransform(),
                image.getImageInfo().getFlashState());

        final ImageProxy imageWithUpdatedInfo = new SettableImageProxy(image,
                input.getSize(), imageInfo);
        imageWithUpdatedInfo.setCropRect(input.getCropRect());
        return imageWithUpdatedInfo;
    }
}
