/*
 * Copyright 2018 The Android Open Source Project
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

package androidx.recyclerview.widget;

import androidx.annotation.RestrictTo;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configuration object for {@link ListAdapter}, {@link AsyncListDiffer}, and similar
 * background-thread list diffing adapter logic.
 * <p>
 * At minimum, defines item diffing behavior with a {@link DiffUtil.ItemCallback}, used to compute
 * item differences to pass to a RecyclerView adapter.
 *
 * @param <T> Type of items in the lists, and being compared.
 */
public final class AsyncDifferConfig<T> {
    private final @Nullable Executor mMainThreadExecutor;
    private final @NonNull Executor mBackgroundThreadExecutor;
    private final DiffUtil.@NonNull ItemCallback<T> mDiffCallback;

    @SuppressWarnings("WeakerAccess") /* synthetic access */
    AsyncDifferConfig(
            @Nullable Executor mainThreadExecutor,
            @NonNull Executor backgroundThreadExecutor,
            DiffUtil.@NonNull ItemCallback<T> diffCallback) {
        mMainThreadExecutor = mainThreadExecutor;
        mBackgroundThreadExecutor = backgroundThreadExecutor;
        mDiffCallback = diffCallback;
    }

    @SuppressWarnings("WeakerAccess")
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public @Nullable Executor getMainThreadExecutor() {
        return mMainThreadExecutor;
    }

    @SuppressWarnings("WeakerAccess")
    public @NonNull Executor getBackgroundThreadExecutor() {
        return mBackgroundThreadExecutor;
    }

    @SuppressWarnings("WeakerAccess")
    public DiffUtil.@NonNull ItemCallback<T> getDiffCallback() {
        return mDiffCallback;
    }

    /**
     * Builder class for {@link AsyncDifferConfig}.
     *
     * @param <T>
     */
    public static final class Builder<T> {
        private @Nullable Executor mMainThreadExecutor;
        private Executor mBackgroundThreadExecutor;
        private final DiffUtil.ItemCallback<T> mDiffCallback;

        public Builder(DiffUtil.@NonNull ItemCallback<T> diffCallback) {
            mDiffCallback = diffCallback;
        }

        /**
         * If provided, defines the main thread executor used to dispatch adapter update
         * notifications on the main thread.
         * <p>
         * If not provided or null, it will default to the main thread.
         *
         * @param executor The executor which can run tasks in the UI thread.
         * @return this
         *
         */
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        public @NonNull Builder<T> setMainThreadExecutor(@Nullable Executor executor) {
            mMainThreadExecutor = executor;
            return this;
        }

        /**
         * If provided, defines the background executor used to calculate the diff between an old
         * and a new list.
         * <p>
         * If not provided or null, defaults to two thread pool executor, shared by all
         * ListAdapterConfigs.
         *
         * @param executor The background executor to run list diffing.
         * @return this
         */
        @SuppressWarnings({"unused", "WeakerAccess"})
        public @NonNull Builder<T> setBackgroundThreadExecutor(@Nullable Executor executor) {
            mBackgroundThreadExecutor = executor;
            return this;
        }

        /**
         * Creates a {@link AsyncListDiffer} with the given parameters.
         *
         * @return A new AsyncDifferConfig.
         */
        public @NonNull AsyncDifferConfig<T> build() {
            if (mBackgroundThreadExecutor == null) {
                synchronized (sExecutorLock) {
                    if (sDiffExecutor == null) {
                        sDiffExecutor = Executors.newFixedThreadPool(2);
                    }
                }
                mBackgroundThreadExecutor = sDiffExecutor;
            }
            return new AsyncDifferConfig<>(
                    mMainThreadExecutor,
                    mBackgroundThreadExecutor,
                    mDiffCallback);
        }

        // TODO: remove the below once supportlib has its own appropriate executors
        private static final Object sExecutorLock = new Object();
        private static Executor sDiffExecutor = null;
    }
}
