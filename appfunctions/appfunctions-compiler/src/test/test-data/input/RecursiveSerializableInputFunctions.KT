package com.testdata

import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionSerializable

class RecursiveSerializableInputFunctions {
    @AppFunction
    fun simpleFunction(appFunctionContext: AppFunctionContext, serializable: SampleSerializable) {}

    @AppFunction
    fun simpleFunctionSerializableList(
        appFunctionContext: AppFunctionContext,
        serializable: List<SampleSerializable>
    ) {}

    @AppFunction
    fun simpleFunctionNullableSerializableList(
        appFunctionContext: AppFunctionContext,
        serializable: List<SampleSerializable>?
    ) {}

    @AppFunction
    fun simpleFunctionNullableSerializable(
        appFunctionContext: AppFunctionContext,
        serializable: SampleSerializable?
    ) {}
}

@AppFunctionSerializable
class SampleSerializable(val nested: SampleSerializable?)