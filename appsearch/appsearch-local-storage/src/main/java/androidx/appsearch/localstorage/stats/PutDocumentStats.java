/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.appsearch.localstorage.stats;

import androidx.annotation.RestrictTo;
import androidx.appsearch.annotation.CanIgnoreReturnValue;
import androidx.appsearch.app.AppSearchResult;
import androidx.core.util.Preconditions;

import org.jspecify.annotations.NonNull;

/**
 * A class for holding detailed stats to log for each individual document put by a
 * {@link androidx.appsearch.app.AppSearchSession#putAsync} call.
 *
 * @exportToFramework:hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public final class PutDocumentStats {
    private final @NonNull String mPackageName;
    private final @NonNull String mDatabase;
    /**
     * The status code returned by {@link AppSearchResult#getResultCode()} for the call or
     * internal state.
     */
    @AppSearchResult.ResultCode
    private final int mStatusCode;
    private final int mTotalLatencyMillis;

    /** Time used to generate a document proto from a Bundle. */
    private final int mGenerateDocumentProtoLatencyMillis;

    /** Time used to rewrite types and namespaces in the document. */
    private final int mRewriteDocumentTypesLatencyMillis;

    /** Overall time used for the native function call. */
    private final int mNativeLatencyMillis;

    /** Time used to store the document. */
    private final int mNativeDocumentStoreLatencyMillis;

    /** Time used to index the document. It doesn't include the time to merge indices. */
    private final int mNativeIndexLatencyMillis;

    /** Time used to merge the indices. */
    private final int mNativeIndexMergeLatencyMillis;

    /** Document size in bytes. */
    private final int mNativeDocumentSizeBytes;

    /** Number of tokens added to the index. */
    private final int mNativeNumTokensIndexed;

    /**
     * Time used to index all indexable string terms in the document. It does not include the
     * time to merge indices.
     */
    private final int mNativeTermIndexLatencyMillis;

    /** Time used to index all indexable integers in the document. */
    private final int mNativeIntegerIndexLatencyMillis;

    /** Time used to index all qualified id join strings in the document. */
    private final int mNativeQualifiedIdJoinIndexLatencyMillis;

    /** Time used to sort and merge the lite index's hit buffer. */
    private final int mNativeLiteIndexSortLatencyMillis;

    PutDocumentStats(@NonNull Builder builder) {
        Preconditions.checkNotNull(builder);
        mPackageName = builder.mPackageName;
        mDatabase = builder.mDatabase;
        mStatusCode = builder.mStatusCode;
        mTotalLatencyMillis = builder.mTotalLatencyMillis;
        mGenerateDocumentProtoLatencyMillis = builder.mGenerateDocumentProtoLatencyMillis;
        mRewriteDocumentTypesLatencyMillis = builder.mRewriteDocumentTypesLatencyMillis;
        mNativeLatencyMillis = builder.mNativeLatencyMillis;
        mNativeDocumentStoreLatencyMillis = builder.mNativeDocumentStoreLatencyMillis;
        mNativeIndexLatencyMillis = builder.mNativeIndexLatencyMillis;
        mNativeIndexMergeLatencyMillis = builder.mNativeIndexMergeLatencyMillis;
        mNativeDocumentSizeBytes = builder.mNativeDocumentSizeBytes;
        mNativeNumTokensIndexed = builder.mNativeNumTokensIndexed;
        mNativeTermIndexLatencyMillis = builder.mNativeTermIndexLatencyMillis;
        mNativeIntegerIndexLatencyMillis = builder.mNativeIntegerIndexLatencyMillis;
        mNativeQualifiedIdJoinIndexLatencyMillis = builder.mNativeQualifiedIdJoinIndexLatencyMillis;
        mNativeLiteIndexSortLatencyMillis = builder.mNativeLiteIndexSortLatencyMillis;
    }

    /** Returns calling package name. */
    public @NonNull String getPackageName() {
        return mPackageName;
    }

    /** Returns calling database name. */
    public @NonNull String getDatabase() {
        return mDatabase;
    }

    /** Returns status code for this putDocument. */
    @AppSearchResult.ResultCode
    public int getStatusCode() {
        return mStatusCode;
    }

    /** Returns total latency of this putDocument in millis. */
    public int getTotalLatencyMillis() {
        return mTotalLatencyMillis;
    }

    /** Returns time spent on generating document proto, in milliseconds. */
    public int getGenerateDocumentProtoLatencyMillis() {
        return mGenerateDocumentProtoLatencyMillis;
    }

    /** Returns time spent on rewriting types and namespaces in document, in milliseconds. */
    public int getRewriteDocumentTypesLatencyMillis() {
        return mRewriteDocumentTypesLatencyMillis;
    }

    /** Returns time spent in native, in milliseconds. */
    public int getNativeLatencyMillis() {
        return mNativeLatencyMillis;
    }

    /** Returns time spent on document store, in milliseconds. */
    public int getNativeDocumentStoreLatencyMillis() {
        return mNativeDocumentStoreLatencyMillis;
    }

    /** Returns time spent on indexing, in milliseconds. */
    public int getNativeIndexLatencyMillis() {
        return mNativeIndexLatencyMillis;
    }

    /** Returns time spent on merging indices, in milliseconds. */
    public int getNativeIndexMergeLatencyMillis() {
        return mNativeIndexMergeLatencyMillis;
    }

    /** Returns document size, in bytes. */
    public int getNativeDocumentSizeBytes() {
        return mNativeDocumentSizeBytes;
    }

    /** Returns number of tokens indexed. */
    public int getNativeNumTokensIndexed() {
        return mNativeNumTokensIndexed;
    }

    /** Returns time spent on term indexing, in milliseconds. */
    public int getNativeTermIndexLatencyMillis() {
        return mNativeTermIndexLatencyMillis;
    }

    /** Returns time spent on integer indexing, in milliseconds. */
    public int getNativeIntegerIndexLatencyMillis() {
        return mNativeIntegerIndexLatencyMillis;
    }

    /** Returns time spent on qualified id join indexing, in milliseconds. */
    public int getNativeQualifiedIdJoinIndexLatencyMillis() {
        return mNativeQualifiedIdJoinIndexLatencyMillis;
    }

    /** Returns time spent sorting and merging the lite index, in milliseconds. */
    public int getNativeLiteIndexSortLatencyMillis() {
        return mNativeLiteIndexSortLatencyMillis;
    }

    /** Builder for {@link PutDocumentStats}. */
    public static class Builder {
        final @NonNull String mPackageName;
        final @NonNull String mDatabase;
        @AppSearchResult.ResultCode
        int mStatusCode;
        int mTotalLatencyMillis;
        int mGenerateDocumentProtoLatencyMillis;
        int mRewriteDocumentTypesLatencyMillis;
        int mNativeLatencyMillis;
        int mNativeDocumentStoreLatencyMillis;
        int mNativeIndexLatencyMillis;
        int mNativeIndexMergeLatencyMillis;
        int mNativeDocumentSizeBytes;
        int mNativeNumTokensIndexed;
        int mNativeTermIndexLatencyMillis;
        int mNativeIntegerIndexLatencyMillis;
        int mNativeQualifiedIdJoinIndexLatencyMillis;
        int mNativeLiteIndexSortLatencyMillis;

        /** Builder for {@link PutDocumentStats} */
        public Builder(@NonNull String packageName, @NonNull String database) {
            mPackageName = Preconditions.checkNotNull(packageName);
            mDatabase = Preconditions.checkNotNull(database);
        }

        /** Sets the status code. */
        @CanIgnoreReturnValue
        public @NonNull Builder setStatusCode(@AppSearchResult.ResultCode int statusCode) {
            mStatusCode = statusCode;
            return this;
        }

        /** Sets total latency in millis. */
        @CanIgnoreReturnValue
        public @NonNull Builder setTotalLatencyMillis(int totalLatencyMillis) {
            mTotalLatencyMillis = totalLatencyMillis;
            return this;
        }

        /** Sets how much time we spend for generating document proto, in milliseconds. */
        @CanIgnoreReturnValue
        public @NonNull Builder setGenerateDocumentProtoLatencyMillis(
                int generateDocumentProtoLatencyMillis) {
            mGenerateDocumentProtoLatencyMillis = generateDocumentProtoLatencyMillis;
            return this;
        }

        /**
         * Sets how much time we spend for rewriting types and namespaces in document, in
         * milliseconds.
         */
        @CanIgnoreReturnValue
        public @NonNull Builder setRewriteDocumentTypesLatencyMillis(
                int rewriteDocumentTypesLatencyMillis) {
            mRewriteDocumentTypesLatencyMillis = rewriteDocumentTypesLatencyMillis;
            return this;
        }

        /** Sets the native latency, in milliseconds. */
        @CanIgnoreReturnValue
        public @NonNull Builder setNativeLatencyMillis(int nativeLatencyMillis) {
            mNativeLatencyMillis = nativeLatencyMillis;
            return this;
        }

        /** Sets how much time we spend on document store, in milliseconds. */
        @CanIgnoreReturnValue
        public @NonNull Builder setNativeDocumentStoreLatencyMillis(
                int nativeDocumentStoreLatencyMillis) {
            mNativeDocumentStoreLatencyMillis = nativeDocumentStoreLatencyMillis;
            return this;
        }

        /** Sets the native index latency, in milliseconds. */
        @CanIgnoreReturnValue
        public @NonNull Builder setNativeIndexLatencyMillis(int nativeIndexLatencyMillis) {
            mNativeIndexLatencyMillis = nativeIndexLatencyMillis;
            return this;
        }

        /** Sets how much time we spend on merging indices, in milliseconds. */
        @CanIgnoreReturnValue
        public @NonNull Builder setNativeIndexMergeLatencyMillis(
                int nativeIndexMergeLatencyMillis) {
            mNativeIndexMergeLatencyMillis = nativeIndexMergeLatencyMillis;
            return this;
        }

        /** Sets document size, in bytes. */
        @CanIgnoreReturnValue
        public @NonNull Builder setNativeDocumentSizeBytes(int nativeDocumentSizeBytes) {
            mNativeDocumentSizeBytes = nativeDocumentSizeBytes;
            return this;
        }

        /** Sets number of tokens indexed in native. */
        @CanIgnoreReturnValue
        public @NonNull Builder setNativeNumTokensIndexed(int nativeNumTokensIndexed) {
            mNativeNumTokensIndexed = nativeNumTokensIndexed;
            return this;
        }

        /** Sets the native term indexing time, in millis. */
        @CanIgnoreReturnValue
        public @NonNull Builder setNativeTermIndexLatencyMillis(int nativeTermIndexLatencyMillis) {
            mNativeTermIndexLatencyMillis = nativeTermIndexLatencyMillis;
            return this;
        }

        /** Sets the native integer indexing time, in millis. */
        @CanIgnoreReturnValue
        public @NonNull Builder setNativeIntegerIndexLatencyMillis(
                int nativeIntegerIndexLatencyMillis) {
            mNativeIntegerIndexLatencyMillis = nativeIntegerIndexLatencyMillis;
            return this;
        }

        /** Sets the native qualified id indexing time, in millis. */
        @CanIgnoreReturnValue
        public @NonNull Builder setNativeQualifiedIdJoinIndexLatencyMillis(
                int nativeQualifiedIdJoinIndexLatencyMillis) {
            mNativeQualifiedIdJoinIndexLatencyMillis = nativeQualifiedIdJoinIndexLatencyMillis;
            return this;
        }

        /** Sets the native lite index sort latency, in millis. */
        @CanIgnoreReturnValue
        public @NonNull Builder setNativeLiteIndexSortLatencyMillis(
                int nativeLiteIndexSortLatencyMillis) {
            mNativeLiteIndexSortLatencyMillis = nativeLiteIndexSortLatencyMillis;
            return this;
        }

        /**
         * Creates a new {@link PutDocumentStats} object from the contents of this
         * {@link Builder} instance.
         */
        public @NonNull PutDocumentStats build() {
            return new PutDocumentStats(/* builder= */ this);
        }
    }
}
