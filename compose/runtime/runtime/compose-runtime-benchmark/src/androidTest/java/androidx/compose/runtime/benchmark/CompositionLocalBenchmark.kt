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

package androidx.compose.runtime.benchmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

val local = compositionLocalOf { 0 }

@LargeTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class CompositionLocalBenchmark : ComposeBenchmarkBase() {

    @Test
    @Ignore // Only used for overhead comparison, not to be tracked.
    fun compositionLocal_compose_depth_1_1() = runBlockingTestWithFrameClock {
        measureCompose {
            CompositionLocalProvider(local provides 100) { DepthOf(1) { local.current } }
        }
    }

    @Test
    @Ignore // Only used for overhead comparison, not to be tracked.
    fun compositionLocal_compose_depth_1_10() = runBlockingTestWithFrameClock {
        measureCompose {
            CompositionLocalProvider(local provides 100) {
                DepthOf(1) { repeat(10) { local.current } }
            }
        }
    }

    @Test
    @Ignore // Only used for overhead comparison, not to be tracked.
    fun compositionLocal_compose_depth_1_100() = runBlockingTestWithFrameClock {
        measureCompose {
            CompositionLocalProvider(local provides 100) {
                DepthOf(1) { repeat(100) { local.current } }
            }
        }
    }

    @Test
    @Ignore // Only used for overhead comparison, not to be tracked.
    fun compositionLocal_compose_depth_100_1() = runBlockingTestWithFrameClock {
        measureCompose {
            CompositionLocalProvider(local provides 100) { DepthOf(100) { local.current } }
        }
    }

    @Test
    @Ignore // Only used for overhead comparison, not to be tracked.
    fun compositionLocal_compose_depth_100_10() = runBlockingTestWithFrameClock {
        measureCompose {
            CompositionLocalProvider(local provides 100) {
                DepthOf(100) { repeat(10) { local.current } }
            }
        }
    }

    @Test
    @Ignore // Only used for overhead comparison, not to be tracked.
    fun compositionLocal_compose_depth_100_100() = runBlockingTestWithFrameClock {
        measureCompose {
            CompositionLocalProvider(local provides 100) {
                DepthOf(100) { repeat(100) { local.current } }
            }
        }
    }

    @Test
    @Ignore // Only used for overhead comparison, not to be tracked.
    fun compositionLocal_compose_nested_providers_10() = runBlockingTestWithFrameClock {
        val local = staticCompositionLocalOf { 0 }
        measureCompose { NestedProviders(10) { local.current } }
    }

    @Test
    @Ignore // Only used for overhead comparison, not to be tracked.
    fun compositionLocal_compose_nested_providers_100() = runBlockingTestWithFrameClock {
        val local = staticCompositionLocalOf { 0 }
        measureCompose { NestedProviders(100) { local.current } }
    }

    @Test
    @Ignore // Only used for overhead comparison, not to be tracked.
    fun compositionLocal_compose_nested_providers_1000() = runBlockingTestWithFrameClock {
        val local = staticCompositionLocalOf { 0 }
        measureCompose { NestedProviders(1000) { local.current } }
    }
}

@Composable
fun DepthOf(count: Int, content: @Composable () -> Unit) {
    if (count > 0) DepthOf(count - 1, content) else content()
}

@Composable
fun NestedProviders(count: Int, content: @Composable () -> Unit) {
    if (count > 0) {
        CompositionLocalProvider(staticCompositionLocalOf { 0 } provides 0) {
            NestedProviders(count = count - 1, content)
        }
    } else content()
}
