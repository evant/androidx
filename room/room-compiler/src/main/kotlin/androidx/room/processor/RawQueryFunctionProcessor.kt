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

package androidx.room.processor

import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XNullability
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XVariableElement
import androidx.room.ext.RoomTypeNames
import androidx.room.ext.SupportDbTypeNames
import androidx.room.ext.isEntityElement
import androidx.room.parser.SqlParser
import androidx.room.processor.ProcessorErrors.RAW_QUERY_STRING_PARAMETER_REMOVED
import androidx.room.vo.MapInfo
import androidx.room.vo.RawQueryFunction

class RawQueryFunctionProcessor(
    baseContext: Context,
    val containing: XType,
    val executableElement: XMethodElement
) {
    val context = baseContext.fork(executableElement)

    fun process(): RawQueryFunction {
        val delegate = FunctionProcessorDelegate.createFor(context, containing, executableElement)
        val returnType = delegate.extractReturnType()

        context.checker.check(
            executableElement.hasAnnotation(RawQuery::class),
            executableElement,
            ProcessorErrors.MISSING_RAWQUERY_ANNOTATION
        )

        context.checker.notUnbound(
            returnType,
            executableElement,
            ProcessorErrors.CANNOT_USE_UNBOUND_GENERICS_IN_QUERY_FUNCTIONS
        )

        val returnsDeferredType = delegate.returnsDeferredType()
        val isSuspendFunction = delegate.executableElement.isSuspendFunction()
        context.checker.check(
            !isSuspendFunction || !returnsDeferredType,
            executableElement,
            ProcessorErrors.suspendReturnsDeferredType(returnType.rawType.typeName.toString())
        )

        if (!isSuspendFunction && !returnsDeferredType && !context.isAndroidOnlyTarget()) {
            // A blocking function that does not return a deferred return type is not allowed if the
            // target platforms include non-Android targets.
            context.logger.e(
                executableElement,
                ProcessorErrors.INVALID_BLOCKING_DAO_FUNCTION_NON_ANDROID
            )
            // TODO(b/332781418): Early return to avoid generating redundant code.
        }

        val observedTableNames = processObservedTables()
        val query = SqlParser.rawQueryForTables(observedTableNames)
        // build the query but don't calculate result info since we just guessed it.
        val resultBinder =
            delegate.findResultBinder(returnType, query) {
                @Suppress("DEPRECATION") // Due to MapInfo
                val annotation =
                    delegate.executableElement.getAnnotation(androidx.room.MapInfo::class)
                if (annotation != null) {
                    val keyColumn = annotation["keyColumn"]?.asString() ?: ""
                    val valueColumn = annotation["valueColumn"]?.asString() ?: ""
                    context.checker.check(
                        keyColumn.isNotEmpty() || valueColumn.isNotEmpty(),
                        executableElement,
                        ProcessorErrors.MAP_INFO_MUST_HAVE_AT_LEAST_ONE_COLUMN_PROVIDED
                    )
                    putData(MapInfo::class, MapInfo(keyColumn, valueColumn))
                }
            }

        val runtimeQueryParam = findRuntimeQueryParameter(delegate.extractParams())
        val inTransaction = executableElement.hasAnnotation(Transaction::class)
        val rawQueryFunction =
            RawQueryFunction(
                element = executableElement,
                observedTableNames = observedTableNames,
                returnType = returnType,
                runtimeQueryParam = runtimeQueryParam,
                inTransaction = inTransaction,
                queryResultBinder = resultBinder
            )
        // TODO: Lift this restriction, to allow for INSERT, UPDATE and DELETE raw statements.
        context.checker.check(
            rawQueryFunction.returnsValue,
            executableElement,
            ProcessorErrors.RAW_QUERY_BAD_RETURN_TYPE
        )
        return rawQueryFunction
    }

    private fun processObservedTables(): Set<String> {
        val annotation = executableElement.getAnnotation(RawQuery::class)
        val observedEntities = annotation?.get("observedEntities")?.asTypeList() ?: emptyList()
        return observedEntities
            .mapNotNull {
                it.typeElement.also { typeElement ->
                    if (typeElement == null) {
                        context.logger.e(executableElement, ProcessorErrors.NOT_ENTITY_OR_VIEW)
                    }
                }
            }
            .flatMap {
                if (it.isEntityElement()) {
                    val entity = EntityProcessor(context = context, element = it).process()
                    arrayListOf(entity.tableName)
                } else {
                    val pojo =
                        DataClassProcessor.createFor(
                                context = context,
                                element = it,
                                bindingScope = FieldProcessor.BindingScope.READ_FROM_STMT,
                                parent = null
                            )
                            .process()
                    val tableNames = pojo.accessedTableNames()
                    // if it is empty, report error as it does not make sense
                    if (tableNames.isEmpty()) {
                        context.logger.e(
                            executableElement,
                            ProcessorErrors.rawQueryBadEntity(
                                it.type.asTypeName().toString(context.codeLanguage)
                            )
                        )
                    }
                    tableNames
                }
            }
            .toSet()
    }

    private fun findRuntimeQueryParameter(
        extractParams: List<XVariableElement>
    ): RawQueryFunction.RuntimeQueryParameter? {
        if (extractParams.size == 1 && !executableElement.isVarArgs()) {
            val param = extractParams.first().asMemberOf(containing)
            val processingEnv = context.processingEnv
            if (param.nullability == XNullability.NULLABLE) {
                context.logger.e(
                    element = extractParams.first(),
                    msg =
                        ProcessorErrors.parameterCannotBeNullable(
                            parameterName = extractParams.first().name
                        )
                )
            }

            processingEnv.findType(RoomTypeNames.RAW_QUERY)?.let { rawQueryType ->
                if (rawQueryType.isAssignableFrom(param)) {
                    return RawQueryFunction.RuntimeQueryParameter(
                        paramName = extractParams[0].name,
                        typeName = rawQueryType.asTypeName()
                    )
                }
            }

            processingEnv.findType(SupportDbTypeNames.QUERY)?.let { supportQueryType ->
                if (supportQueryType.isAssignableFrom(param)) {
                    return RawQueryFunction.RuntimeQueryParameter(
                        paramName = extractParams[0].name,
                        typeName = supportQueryType.asTypeName()
                    )
                }
            }

            val isString = processingEnv.requireType(String::class).isAssignableFrom(param)
            if (isString) {
                // special error since this was initially allowed but removed in 1.1 beta1
                context.logger.e(executableElement, RAW_QUERY_STRING_PARAMETER_REMOVED)
                return null
            }
        }
        context.logger.e(executableElement, ProcessorErrors.RAW_QUERY_BAD_PARAMS)
        return null
    }
}
