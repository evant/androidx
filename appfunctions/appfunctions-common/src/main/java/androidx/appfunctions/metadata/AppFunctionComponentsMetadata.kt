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

package androidx.appfunctions.metadata

import androidx.annotation.RestrictTo
import androidx.appsearch.annotation.Document
import java.util.Objects

/** Represent the reusable components in a function specification. */
public class AppFunctionComponentsMetadata
@JvmOverloads
constructor(
    /**
     * The list of data types that can be reused across the schema. For example, a Person type can
     * be defined here and referenced in multiple places. See {@link
     * AppFunctionReferenceTypeMetadata#referenceDataType}.
     */
    public val dataTypes: List<AppFunctionDataTypeMetadata> = emptyList(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppFunctionComponentsMetadata

        return dataTypes == other.dataTypes
    }

    override fun hashCode(): Int {
        return Objects.hashCode(dataTypes)
    }

    override fun toString(): String {
        return "AppFunctionComponentsMetadata(dataTypes=$dataTypes)"
    }
}

/** Represents the persistent storage format of [AppFunctionComponentsMetadata]. */
@Document
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public data class AppFunctionComponentsMetadataDocument(
    @Document.Namespace public val namespace: String = APP_FUNCTION_NAMESPACE,
    @Document.Id public val id: String = APP_FUNCTION_ID_EMPTY,
    /** The list of data types that ban be reusable across the schema. */
    @Document.DocumentProperty public val dataTypes: List<AppFunctionDataTypeMetadata>,
)
