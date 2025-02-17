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

package androidx.compose.runtime.tooling

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.ReusableComposeNode
import androidx.compose.runtime.key
import androidx.compose.runtime.mock.View
import androidx.compose.runtime.mock.ViewApplier
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState

/**
 * These composables are kept in a separate file to test line offsets in traces. When adding new
 * composables, add to the end of the file to avoid breaking existing expectations
 */
@Composable
inline fun Wrapper(block: @Composable () -> Unit) {
    block()
}

@Composable
fun Subcompose(content: @Composable () -> Unit) {
    val context = rememberCompositionContext()
    val composition = remember(context) { Composition(ViewApplier(View()), context) }
    val currentContent = rememberUpdatedState(content)
    DisposableEffect(composition) {
        composition.setContent { currentContent.value() }
        onDispose { composition.dispose() }
    }
}

@Composable
fun Linear(content: @Composable () -> Unit) {
    ReusableComposeNode<View, ViewApplier>(
        factory = { View().also { it.name = "linear" } },
        update = {}
    ) {
        content()
    }
}

@Composable
inline fun InlineLinear(content: @Composable () -> Unit) {
    ReusableComposeNode<View, ViewApplier>(
        factory = { View().also { it.name = "linear" } },
        update = {}
    ) {
        content()
    }
}

@Composable
fun <T : Any> Repeated(of: Iterable<T>, block: @Composable (value: T) -> Unit) {
    for (value in of) {
        key(value) { block(value) }
    }
}

@Composable
@NonRestartableComposable
fun Text(value: String) {
    ReusableComposeNode<View, ViewApplier>(
        factory = { View().also { it.name = "text" } },
        update = { set(value) { text = it } }
    )
}

@Composable
fun ComposableWithDefaults(value: String = remember { "" }, block: @Composable (String) -> Unit) {
    block(value)
}
