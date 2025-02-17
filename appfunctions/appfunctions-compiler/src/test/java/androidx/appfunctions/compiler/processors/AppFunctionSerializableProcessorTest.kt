/*
 * Copyright 2025 The Android Open Source Project
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

package androidx.appfunctions.compiler.processors

import androidx.appfunctions.compiler.AppFunctionCompiler
import androidx.appfunctions.compiler.testings.CompilationTestHelper
import com.google.common.truth.Truth.assertThat
import java.io.File
import org.junit.Before
import org.junit.Test

class AppFunctionSerializableProcessorTest {

    private lateinit var compilationTestHelper: CompilationTestHelper

    @Before
    fun setup() {
        compilationTestHelper =
            CompilationTestHelper(
                testFileSrcDir = File("src/test/test-data/entities"),
                goldenFileSrcDir = File("src/test/test-data/output"), // unused
                symbolProcessorProviders = listOf(AppFunctionCompiler.Provider())
            )
    }

    @Test
    fun testResolveAppFunctionSerializables_validProperties_success() {
        val report =
            compilationTestHelper.compileAll(
                sourceFileNames = listOf("EntityWithValidProperties.KT")
            )
        assertThat(report.isSuccess).isTrue()
    }

    @Test
    fun testResolveAppFunctionSerializables_nonPropertyParameter_fails() {
        val report =
            compilationTestHelper.compileAll(
                sourceFileNames = listOf("EntityWithNonPropertyParameter.KT")
            )
        compilationTestHelper.assertErrorWithMessage(
            report,
            "All parameters in @AppFunctionSerializable primary constructor must have getters"
        )
    }

    @Test
    fun testResolveAppFunctionSerializables_invalidPropertyType_fails() {
        val report =
            compilationTestHelper.compileAll(
                sourceFileNames = listOf("EntityWithInvalidParameterType.KT")
            )
        compilationTestHelper.assertErrorWithMessage(
            report,
            "AppFunctionSerializable properties must be one of the following types:\n" +
                "kotlin.Int,kotlin.Long,kotlin.Float,kotlin.Double,kotlin.Boolean,kotlin.String," +
                "kotlin.IntArray,kotlin.LongArray,kotlin.FloatArray,kotlin.DoubleArray," +
                "kotlin.BooleanArray,kotlin.collections.List<kotlin.String>, an @AppFunctionSerializable or a list of @AppFunctionSerializable\n" +
                "but found kotlin.Any"
        )
    }

    @Test
    fun testResolveAppFunctionSerializables_invalidPropertyListType_fails() {
        val report =
            compilationTestHelper.compileAll(
                sourceFileNames = listOf("EntityWithInvalidListParameterType.KT")
            )
        compilationTestHelper.assertErrorWithMessage(
            report,
            "AppFunctionSerializable properties must be one of the following types:\n" +
                "kotlin.Int,kotlin.Long,kotlin.Float,kotlin.Double,kotlin.Boolean,kotlin.String," +
                "kotlin.IntArray,kotlin.LongArray,kotlin.FloatArray,kotlin.DoubleArray," +
                "kotlin.BooleanArray,kotlin.collections.List<kotlin.String>, an @AppFunctionSerializable or a list of @AppFunctionSerializable\n" +
                "but found kotlin.collections.List<kotlin.Any>"
        )
    }
}
