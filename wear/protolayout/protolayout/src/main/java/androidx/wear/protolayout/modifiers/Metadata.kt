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
@file:RestrictTo(Scope.LIBRARY_GROUP)

package androidx.wear.protolayout.modifiers

import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope
import androidx.wear.protolayout.ModifiersBuilders.ElementMetadata

/**
 * Applies additional metadata about an element. This is meant to be used by libraries building
 * higher-level components. This can be used to track component metadata.
 */
fun LayoutModifier.tag(tagData: ByteArray): LayoutModifier =
    this then BaseMetadataElement(tagData = tagData)

/**
 * Applies additional metadata about an element. This is meant to be used by libraries building
 * higher-level components. This can be used to track component metadata.
 */
fun LayoutModifier.tag(tag: String): LayoutModifier = tag(tag.toByteArray())

internal class BaseMetadataElement(val tagData: ByteArray) : LayoutModifier.Element {
    fun mergeTo(initial: ElementMetadata.Builder?): ElementMetadata.Builder =
        (initial ?: ElementMetadata.Builder()).setTagData(tagData)
}
