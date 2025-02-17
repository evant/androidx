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

package androidx.wear.compose.material3

import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.focused
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ButtonDefaults.buttonColors
import androidx.wear.compose.material3.ButtonDefaults.filledTonalButtonColors
import androidx.wear.compose.material3.internal.Icons
import androidx.wear.compose.material3.internal.Strings.Companion.DatePickerDay
import androidx.wear.compose.material3.internal.Strings.Companion.DatePickerMonth
import androidx.wear.compose.material3.internal.Strings.Companion.DatePickerYear
import androidx.wear.compose.material3.internal.Strings.Companion.PickerConfirmButtonContentDescription
import androidx.wear.compose.material3.internal.Strings.Companion.PickerNextButtonContentDescription
import androidx.wear.compose.material3.internal.getString
import androidx.wear.compose.material3.tokens.DatePickerTokens
import androidx.wear.compose.materialcore.isLargeScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Full screen [DatePicker] with day, month, year.
 *
 * This component is designed to take most/all of the screen and utilizes large fonts.
 *
 * Example of a [DatePicker]:
 *
 * @sample androidx.wear.compose.material3.samples.DatePickerSample
 *
 * Example of a [DatePicker] shows the picker options in year-month-day order:
 *
 * @sample androidx.wear.compose.material3.samples.DatePickerYearMonthDaySample
 *
 * Example of a [DatePicker] with a minDate:
 *
 * @sample androidx.wear.compose.material3.samples.DatePickerFutureOnlySample
 * @param initialDate The initial value to be displayed in the DatePicker.
 * @param onDatePicked The callback that is called when the user confirms the date selection. It
 *   provides the selected date as [LocalDate]
 * @param modifier Modifier to be applied to the `Box` containing the UI elements.
 * @param minValidDate Optional minimum date that can be selected in the DatePicker (inclusive).
 * @param maxValidDate Optional maximum date that can be selected in the DatePicker (inclusive).
 * @param datePickerType The different [DatePickerType] supported by this [DatePicker].
 * @param colors [DatePickerColors] to be applied to the DatePicker.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
public fun DatePicker(
    initialDate: LocalDate,
    onDatePicked: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    minValidDate: LocalDate? = null,
    maxValidDate: LocalDate? = null,
    datePickerType: DatePickerType = DatePickerDefaults.datePickerType,
    colors: DatePickerColors = DatePickerDefaults.datePickerColors()
) {
    val inspectionMode = LocalInspectionMode.current
    val fullyDrawn = remember { Animatable(if (inspectionMode) 1f else 0f) }

    if (minValidDate != null && maxValidDate != null) {
        verifyDates(initialDate, minValidDate, maxValidDate)
    }

    val datePickerState =
        remember(initialDate) { DatePickerState(initialDate, minValidDate, maxValidDate) }

    val touchExplorationServicesEnabled by
        LocalTouchExplorationStateProvider.current.touchExplorationState()

    /** The current selected [Picker] index. */
    var selectedIndex by
        remember(touchExplorationServicesEnabled) {
            // When the date picker loads, none of the individual pickers are selected in talkback
            // mode,
            // otherwise first picker should be focused (depends on the picker ordering given by
            // datePickerType)
            val initiallySelectedIndex =
                if (touchExplorationServicesEnabled) {
                    null
                } else {
                    0
                }
            mutableStateOf(initiallySelectedIndex)
        }

    val isLargeScreen = isLargeScreen()
    val labelTextStyle =
        if (isLargeScreen) {
            DatePickerTokens.LabelLargeTypography.value
        } else {
            DatePickerTokens.LabelTypography.value
        }
    val optionTextStyle =
        if (isLargeScreen) {
            DatePickerTokens.ContentLargeTypography.value
        } else {
            DatePickerTokens.ContentTypography.value
        }
    val optionHeight = if (isLargeScreen) 48.dp else 36.dp

    val focusRequesterConfirmButton = remember { FocusRequester() }

    val yearString = getString(DatePickerYear)
    val monthString = getString(DatePickerMonth)
    val dayString = getString(DatePickerDay)

    LaunchedEffect(
        datePickerState.isMinYearSelected,
        datePickerState.isMaxYearSelected,
        datePickerState.yearState.isScrollInProgress,
        datePickerState.monthState.isScrollInProgress,
    ) {
        if (
            (datePickerState.isMinYearSelected || datePickerState.isMaxYearSelected) &&
                !datePickerState.yearState.isScrollInProgress &&
                !datePickerState.monthState.isScrollInProgress
        ) {
            datePickerState.adjustMonthOptionIfInvalid()
        }
    }
    LaunchedEffect(
        datePickerState.yearState.isScrollInProgress,
        datePickerState.monthState.isScrollInProgress,
        datePickerState.dayState.isScrollInProgress,
    ) {
        if (
            !datePickerState.yearState.isScrollInProgress &&
                !datePickerState.monthState.isScrollInProgress &&
                !datePickerState.dayState.isScrollInProgress &&
                datePickerState.isSelectedMonthValid
        ) {
            datePickerState.adjustDayOptionIfInvalid()
        }
    }

    val shortMonthNames = remember { getMonthNames("MMM") }
    val fullMonthNames = remember { getMonthNames("MMMM") }
    val yearContentDescription by
        remember(
            selectedIndex,
            datePickerState.selectedYear,
        ) {
            derivedStateOf {
                createDescriptionDatePicker(
                    selectedIndex,
                    datePickerState.selectedYear,
                    yearString,
                )
            }
        }
    val monthContentDescription by
        remember(
            selectedIndex,
            datePickerState.selectedMonth,
        ) {
            derivedStateOf {
                if (selectedIndex == null) {
                    monthString
                } else {
                    fullMonthNames[(datePickerState.selectedMonth - 1) % 12]
                }
            }
        }
    val dayContentDescription by
        remember(
            selectedIndex,
            datePickerState.selectedDay,
        ) {
            derivedStateOf {
                createDescriptionDatePicker(
                    selectedIndex,
                    datePickerState.selectedDay,
                    dayString,
                )
            }
        }

    val datePickerOptions = datePickerType.toDatePickerOptions()
    val confirmButtonIndex = datePickerOptions.size

    val onPickerSelected = { current: Int, next: Int ->
        if (selectedIndex != current) {
            selectedIndex = current
        } else {
            selectedIndex = next
            if (next == confirmButtonIndex) {
                focusRequesterConfirmButton.requestFocus()
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().alpha(fullyDrawn.value)) {
        val boxConstraints = this
        val heading =
            selectedIndex?.let {
                when (datePickerOptions.getOrNull(it)) {
                    DatePickerOption.Day -> dayString
                    DatePickerOption.Month -> monthString
                    DatePickerOption.Year -> yearString
                    else -> ""
                }
            } ?: ""
        val headingAnimationSpec: FiniteAnimationSpec<Float> =
            MaterialTheme.motionScheme.defaultEffectsSpec()
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(14.dp))
            AnimatedContent(
                targetState = heading,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter =
                            fadeIn(animationSpec = headingAnimationSpec.delayMillis(200)),
                        initialContentExit = fadeOut(animationSpec = headingAnimationSpec),
                        sizeTransform = null
                    )
                }
            ) { targetText ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = targetText,
                    color = colors.pickerLabelColor,
                    textAlign = TextAlign.Center,
                    style = labelTextStyle,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.height(if (isLargeScreen) 6.dp else 4.dp))
            FontScaleIndependent {
                val measurer = rememberTextMeasurer()
                val density = LocalDensity.current
                val (digitWidth, maxMonthWidth) =
                    remember(
                        density.density,
                        LocalConfiguration.current.screenWidthDp,
                    ) {
                        val mm =
                            measurer.measure(
                                "0123456789\n" + shortMonthNames.joinToString("\n"),
                                style = optionTextStyle,
                                density = density,
                            )

                        ((0..9).maxOf { mm.getBoundingBox(it).width }) to
                            ((1..12).maxOf { mm.getLineRight(it) - mm.getLineLeft(it) })
                    }

                // Add spaces on to allow room to grow
                val dayWidth =
                    with(LocalDensity.current) {
                        maxOf(
                            // Add 1dp buffer to compensate for potential conversion loss
                            (digitWidth * 2).toDp() + 1.dp,
                            minimumInteractiveComponentSize
                        )
                    }
                val monthYearWidth =
                    with(LocalDensity.current) {
                        maxOf(
                            // Add 1dp buffer to compensate for potential conversion loss
                            maxOf(maxMonthWidth.toDp(), (digitWidth * 4).toDp()) + 1.dp,
                            minimumInteractiveComponentSize
                        )
                    }

                Row(
                    modifier =
                        Modifier.fillMaxWidth().weight(1f).offset {
                            IntOffset(
                                getPickerGroupRowOffset(
                                        boxConstraints.maxWidth,
                                        dayWidth,
                                        monthYearWidth,
                                        monthYearWidth,
                                        touchExplorationServicesEnabled,
                                        selectedIndex,
                                    )
                                    .roundToPx(),
                                0
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    val spacing = if (isLargeScreen) 6.dp else 4.dp
                    // Pass a negative value as the selected picker index when none is selected.
                    PickerGroup(
                        selectedPickerIndex = selectedIndex ?: -1,
                        onPickerSelected = { selectedIndex = it },
                        autoCenter = true,
                        separator = { Spacer(Modifier.width(if (isLargeScreen) 12.dp else 8.dp)) },
                    ) {
                        datePickerOptions.forEachIndexed { index, datePickerOption ->
                            when (datePickerOption) {
                                DatePickerOption.Day ->
                                    pickerGroupItem(
                                        pickerState = datePickerState.dayState,
                                        modifier = Modifier.width(dayWidth).fillMaxHeight(),
                                        onSelected = { onPickerSelected(index, index + 1) },
                                        contentDescription = dayContentDescription,
                                        option =
                                            pickerTextOption(
                                                textStyle = optionTextStyle,
                                                indexToText = {
                                                    "%02d".format(datePickerState.dayValue(it))
                                                },
                                                optionHeight = optionHeight,
                                                selectedContentColor =
                                                    colors.activePickerContentColor,
                                                unselectedContentColor =
                                                    colors.inactivePickerContentColor,
                                                invalidContentColor =
                                                    colors.invalidPickerContentColor,
                                                isValid = {
                                                    datePickerState.isDayValid(
                                                        datePickerState.dayValue(it)
                                                    )
                                                }
                                            ),
                                        verticalSpacing = spacing,
                                    )
                                DatePickerOption.Month ->
                                    pickerGroupItem(
                                        pickerState = datePickerState.monthState,
                                        modifier = Modifier.width(monthYearWidth).fillMaxHeight(),
                                        onSelected = { onPickerSelected(index, index + 1) },
                                        contentDescription = monthContentDescription,
                                        option =
                                            pickerTextOption(
                                                textStyle = optionTextStyle,
                                                indexToText = {
                                                    shortMonthNames[
                                                        (datePickerState.monthValue(it) - 1) % 12]
                                                },
                                                optionHeight = optionHeight,
                                                selectedContentColor =
                                                    colors.activePickerContentColor,
                                                unselectedContentColor =
                                                    colors.inactivePickerContentColor,
                                                invalidContentColor =
                                                    colors.invalidPickerContentColor,
                                                isValid = {
                                                    datePickerState.isMonthValid(
                                                        datePickerState.monthValue(it)
                                                    )
                                                }
                                            ),
                                        verticalSpacing = spacing,
                                    )
                                DatePickerOption.Year ->
                                    pickerGroupItem(
                                        pickerState = datePickerState.yearState,
                                        modifier = Modifier.width(monthYearWidth).fillMaxHeight(),
                                        onSelected = { onPickerSelected(index, index + 1) },
                                        contentDescription = yearContentDescription,
                                        option =
                                            pickerTextOption(
                                                textStyle = optionTextStyle,
                                                indexToText = {
                                                    "%4d".format(datePickerState.yearValue(it))
                                                },
                                                optionHeight = optionHeight,
                                                selectedContentColor =
                                                    colors.activePickerContentColor,
                                                unselectedContentColor =
                                                    colors.inactivePickerContentColor,
                                                invalidContentColor =
                                                    colors.invalidPickerContentColor,
                                                isValid = {
                                                    datePickerState.isYearValid(
                                                        datePickerState.yearValue(it)
                                                    )
                                                }
                                            ),
                                        verticalSpacing = spacing,
                                    )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(if (isLargeScreen) 6.dp else 4.dp))
            EdgeButton(
                onClick = {
                    selectedIndex?.let { selectedIndex ->
                        if (selectedIndex >= 2) {
                            val pickedDate =
                                LocalDate.of(
                                    datePickerState.selectedYear,
                                    datePickerState.selectedMonth,
                                    datePickerState.selectedDay,
                                )
                            onDatePicked(pickedDate)
                        } else {
                            onPickerSelected(selectedIndex, selectedIndex + 1)
                        }
                    }
                },
                modifier =
                    Modifier.semantics { focused = (selectedIndex == confirmButtonIndex) }
                        .focusRequester(focusRequesterConfirmButton)
                        .focusable(),
                colors =
                    if (selectedIndex?.let { it >= 2 } == true) {
                        buttonColors(
                            contentColor = colors.confirmButtonContentColor,
                            containerColor = colors.confirmButtonContainerColor,
                        )
                    } else {
                        filledTonalButtonColors(
                            contentColor = colors.nextButtonContentColor,
                            containerColor = colors.nextButtonContainerColor,
                        )
                    },
                enabled =
                    if (selectedIndex?.let { it >= 2 } == true) {
                        datePickerState.isSelectedDayValid
                    } else {
                        true
                    },
            ) {
                // If none is selected (selectedIndex == null) we show 'next' instead of 'confirm'.
                val showConfirm = selectedIndex?.let { it >= 2 } == true
                Icon(
                    imageVector =
                        if (showConfirm) {
                            Icons.Check
                        } else {
                            Icons.AutoMirrored.KeyboardArrowRight
                        },
                    contentDescription =
                        if (showConfirm) {
                            getString(PickerConfirmButtonContentDescription)
                        } else {
                            // If none is selected, return the 'next' content description.
                            getString(PickerNextButtonContentDescription)
                        },
                    modifier = Modifier.size(24.dp).wrapContentSize(align = Alignment.Center),
                )
            }
        }
    }

    if (!inspectionMode) {
        LaunchedEffect(Unit) { fullyDrawn.animateTo(1f) }
    }
}

/** Specifies the types of columns to display in the DatePicker. */
@Immutable
@JvmInline
public value class DatePickerType internal constructor(internal val value: Int) {

    public companion object {
        public val DayMonthYear: DatePickerType = DatePickerType(0)
        public val MonthDayYear: DatePickerType = DatePickerType(1)
        public val YearMonthDay: DatePickerType = DatePickerType(2)
    }

    override fun toString(): String {
        return when (this) {
            DayMonthYear -> "DayMonthYear"
            MonthDayYear -> "MonthDayYear"
            YearMonthDay -> "YearMonthDay"
            else -> "Unknown"
        }
    }
}

/** Contains the default values used by [DatePicker] */
public object DatePickerDefaults {

    /** The default [DatePickerType] for [DatePicker] aligns with the current system date format. */
    public val datePickerType: DatePickerType
        @Composable
        get() {
            val formatOrder = DateFormat.getDateFormatOrder(LocalContext.current)
            return when (formatOrder[0]) {
                'M' -> DatePickerType.MonthDayYear
                'y' -> DatePickerType.YearMonthDay
                else -> DatePickerType.DayMonthYear
            }
        }

    /** Creates a [DatePickerColors] for a [DatePicker]. */
    @Composable
    public fun datePickerColors(): DatePickerColors =
        MaterialTheme.colorScheme.defaultDatePickerColors

    /**
     * Creates a [DatePickerColors] for a [DatePicker].
     *
     * @param activePickerContentColor The content color of the currently active picker section.
     * @param inactivePickerContentColor The content color of an inactive picker section.
     * @param invalidPickerContentColor The content color of invalid picker options. Picker options
     *   can be invalid when minDate or maxDate are specified for the [DatePicker].
     * @param pickerLabelColor The color of the picker label.
     * @param nextButtonContentColor The content color of the next button.
     * @param nextButtonContainerColor The container color of the next button.
     * @param confirmButtonContentColor The content color of the confirm button.
     * @param confirmButtonContainerColor The container color of the confirm button.
     */
    @Composable
    public fun datePickerColors(
        activePickerContentColor: Color = Color.Unspecified,
        inactivePickerContentColor: Color = Color.Unspecified,
        invalidPickerContentColor: Color = Color.Unspecified,
        pickerLabelColor: Color = Color.Unspecified,
        nextButtonContentColor: Color = Color.Unspecified,
        nextButtonContainerColor: Color = Color.Unspecified,
        confirmButtonContentColor: Color = Color.Unspecified,
        confirmButtonContainerColor: Color = Color.Unspecified,
    ): DatePickerColors =
        MaterialTheme.colorScheme.defaultDatePickerColors.copy(
            activePickerContentColor = activePickerContentColor,
            inactivePickerContentColor = inactivePickerContentColor,
            invalidPickerContentColor = invalidPickerContentColor,
            pickerLabelColor = pickerLabelColor,
            nextButtonContentColor = nextButtonContentColor,
            nextButtonContainerColor = nextButtonContainerColor,
            confirmButtonContentColor = confirmButtonContentColor,
            confirmButtonContainerColor = confirmButtonContainerColor,
        )

    private val ColorScheme.defaultDatePickerColors: DatePickerColors
        get() {
            return defaultDatePickerColorsCached
                ?: DatePickerColors(
                        activePickerContentColor = fromToken(DatePickerTokens.SelectedContentColor),
                        inactivePickerContentColor =
                            fromToken(DatePickerTokens.UnselectedContentColor),
                        invalidPickerContentColor =
                            fromToken(DatePickerTokens.InvalidContentColor)
                                .toDisabledColor(
                                    disabledAlpha = DatePickerTokens.InvalidContentOpacity
                                ),
                        pickerLabelColor = fromToken(DatePickerTokens.LabelColor),
                        nextButtonContentColor = fromToken(DatePickerTokens.NextButtonContentColor),
                        nextButtonContainerColor =
                            fromToken(DatePickerTokens.NextButtonContainerColor),
                        confirmButtonContentColor =
                            fromToken(DatePickerTokens.ConfirmButtonContentColor),
                        confirmButtonContainerColor =
                            fromToken(DatePickerTokens.ConfirmButtonContainerColor),
                    )
                    .also { defaultDatePickerColorsCached = it }
        }
}

/**
 * Colors for [DatePicker].
 *
 * @param activePickerContentColor The content color of the currently active picker section, that
 *   is, the section currently being changed, such as the day, month or year.
 * @param inactivePickerContentColor The content color of an inactive picker section.
 * @param invalidPickerContentColor The content color of invalid picker options. Picker options can
 *   be invalid when minDate or maxDate are specified for the [DatePicker].
 * @param pickerLabelColor The color of the picker label.
 * @param nextButtonContentColor The content color of the next button.
 * @param nextButtonContainerColor The container color of the next button.
 * @param confirmButtonContentColor The content color of the confirm button.
 * @param confirmButtonContainerColor The container color of the confirm button.
 */
@Immutable
public class DatePickerColors(
    public val activePickerContentColor: Color,
    public val inactivePickerContentColor: Color,
    public val invalidPickerContentColor: Color,
    public val pickerLabelColor: Color,
    public val nextButtonContentColor: Color,
    public val nextButtonContainerColor: Color,
    public val confirmButtonContentColor: Color,
    public val confirmButtonContainerColor: Color,
) {
    /**
     * Returns a copy of this DatePickerColors, optionally overriding some of the values.
     *
     * @param activePickerContentColor The content color of the currently active picker section,
     *   that is, the section currently being changed, such as the day, month or year.
     * @param inactivePickerContentColor The content color of an inactive picker section.
     * @param invalidPickerContentColor The content color of invalid picker options.
     * @param pickerLabelColor The color of the picker label.
     * @param nextButtonContentColor The content color of the next button.
     * @param nextButtonContainerColor The container color of the next button.
     * @param confirmButtonContentColor The content color of the confirm button.
     * @param confirmButtonContainerColor The container color of the confirm button.
     */
    public fun copy(
        activePickerContentColor: Color = this.activePickerContentColor,
        inactivePickerContentColor: Color = this.inactivePickerContentColor,
        invalidPickerContentColor: Color = this.invalidPickerContentColor,
        pickerLabelColor: Color = this.pickerLabelColor,
        nextButtonContentColor: Color = this.nextButtonContentColor,
        nextButtonContainerColor: Color = this.nextButtonContainerColor,
        confirmButtonContentColor: Color = this.confirmButtonContentColor,
        confirmButtonContainerColor: Color = this.confirmButtonContainerColor,
    ): DatePickerColors =
        DatePickerColors(
            activePickerContentColor =
                activePickerContentColor.takeOrElse { this.activePickerContentColor },
            inactivePickerContentColor =
                inactivePickerContentColor.takeOrElse { this.inactivePickerContentColor },
            invalidPickerContentColor =
                invalidPickerContentColor.takeOrElse { this.invalidPickerContentColor },
            pickerLabelColor = pickerLabelColor.takeOrElse { this.pickerLabelColor },
            nextButtonContentColor =
                nextButtonContentColor.takeOrElse { this.nextButtonContentColor },
            nextButtonContainerColor =
                nextButtonContainerColor.takeOrElse { this.nextButtonContainerColor },
            confirmButtonContentColor =
                confirmButtonContentColor.takeOrElse { this.confirmButtonContentColor },
            confirmButtonContainerColor =
                confirmButtonContainerColor.takeOrElse { this.confirmButtonContainerColor },
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is DatePickerColors) return false

        if (activePickerContentColor != other.activePickerContentColor) return false
        if (inactivePickerContentColor != other.inactivePickerContentColor) return false
        if (invalidPickerContentColor != other.invalidPickerContentColor) return false
        if (pickerLabelColor != other.pickerLabelColor) return false
        if (nextButtonContentColor != other.nextButtonContentColor) return false
        if (nextButtonContainerColor != other.nextButtonContainerColor) return false
        if (confirmButtonContentColor != other.confirmButtonContentColor) return false
        if (confirmButtonContainerColor != other.confirmButtonContainerColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = activePickerContentColor.hashCode()
        result = 31 * result + inactivePickerContentColor.hashCode()
        result = 31 * result + invalidPickerContentColor.hashCode()
        result = 31 * result + pickerLabelColor.hashCode()
        result = 31 * result + nextButtonContentColor.hashCode()
        result = 31 * result + nextButtonContainerColor.hashCode()
        result = 31 * result + confirmButtonContentColor.hashCode()
        result = 31 * result + confirmButtonContainerColor.hashCode()

        return result
    }
}

/** Represents the possible column options for the DatePicker. */
private enum class DatePickerOption {
    Day,
    Month,
    Year
}

private fun DatePickerType.toDatePickerOptions() =
    when (value) {
        DatePickerType.YearMonthDay.value ->
            arrayOf(DatePickerOption.Year, DatePickerOption.Month, DatePickerOption.Day)
        DatePickerType.MonthDayYear.value ->
            arrayOf(DatePickerOption.Month, DatePickerOption.Day, DatePickerOption.Year)
        else -> arrayOf(DatePickerOption.Day, DatePickerOption.Month, DatePickerOption.Year)
    }

@RequiresApi(Build.VERSION_CODES.O)
private fun verifyDates(
    date: LocalDate,
    minDate: LocalDate,
    maxDate: LocalDate,
) {
    require(maxDate >= minDate) { "maxDate should be greater than or equal to minDate" }
    require(date in minDate..maxDate) { "date should lie between minDate and maxDate" }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getMonthNames(pattern: String): List<String> {
    val monthFormatter = DateTimeFormatter.ofPattern(pattern)
    val months = 1..12
    return months.map { LocalDate.of(2022, it, 1).format(monthFormatter) }
}

private fun getPickerGroupRowOffset(
    rowWidth: Dp,
    dayPickerWidth: Dp,
    monthPickerWidth: Dp,
    yearPickerWidth: Dp,
    touchExplorationServicesEnabled: Boolean,
    selectedIndex: Int?,
): Dp {
    val currentOffset = (rowWidth - (dayPickerWidth + monthPickerWidth + yearPickerWidth)) / 2

    return if (touchExplorationServicesEnabled && selectedIndex == null) {
        ((rowWidth - dayPickerWidth) / 2) - currentOffset
    } else if (touchExplorationServicesEnabled && selectedIndex!! > 2) {
        ((rowWidth - yearPickerWidth) / 2) - (dayPickerWidth + monthPickerWidth + currentOffset)
    } else {
        0.dp
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private class DatePickerState(
    initialDate: LocalDate,
    initialDateMinYear: LocalDate?,
    initialDateMaxYear: LocalDate?,
) {
    // Year range 1900 - 2100 was suggested in b/277885199
    private val minDate = initialDateMinYear ?: LocalDate.of(1900, 1, 1)
    private val maxDate = initialDateMaxYear ?: LocalDate.of(2100, 12, 31)

    val yearState =
        PickerState(
            initialNumberOfOptions = (maxDate.year - minDate.year + 1),
            initiallySelectedIndex = initialDate.year - minDate.year,
            shouldRepeatOptions = false,
        )

    val monthState: PickerState =
        PickerState(
            initialNumberOfOptions = 12,
            initiallySelectedIndex = initialDate.monthValue - 1,
        )

    val dayState =
        PickerState(
            initialNumberOfOptions = initialDate.lengthOfMonth(),
            initiallySelectedIndex = initialDate.dayOfMonth - 1,
        )

    val selectedYear: Int
        get() = yearValue(yearState.selectedOptionIndex)

    val selectedMonth: Int
        get() = monthValue(monthState.selectedOptionIndex)

    val selectedDay: Int
        get() = dayValue(dayState.selectedOptionIndex)

    fun yearValue(yearOptionIndex: Int): Int = yearOptionIndex + minDate.year

    fun monthValue(monthOptionIndex: Int): Int = monthOptionIndex + 1

    fun dayValue(dayOptionIndex: Int): Int = dayOptionIndex + 1

    val isMinYearSelected: Boolean
        get() = minDate.year == selectedYear

    val isMaxYearSelected: Boolean
        get() = maxDate.year == selectedYear

    private val isMinMonthSelected: Boolean
        get() = isMinYearSelected && selectedMonth == minDate.monthValue

    private val isMaxMonthSelected: Boolean
        get() = isMaxYearSelected && selectedMonth == maxDate.monthValue

    fun isYearValid(year: Int) = year >= minDate.year && year <= maxDate.year

    val isSelectedMonthValid
        get() = isMonthValid(selectedMonth)

    fun isMonthValid(month: Int): Boolean =
        when {
            !isYearValid(selectedYear) -> false
            isMinYearSelected && month < minDate.monthValue -> false
            isMaxYearSelected && month > maxDate.monthValue -> false
            else -> true
        }

    val isSelectedDayValid: Boolean
        get() = isDayValid(selectedDay)

    fun isDayValid(day: Int): Boolean =
        when {
            !isSelectedMonthValid -> false
            isMinMonthSelected && day < minDate.dayOfMonth -> false
            isMaxMonthSelected && day > maxDate.dayOfMonth -> false
            else -> true
        }

    private fun lengthOfMonth(year: Int, month: Int): Int =
        LocalDate.of(year, month, 1).lengthOfMonth()

    /**
     * Adjusts the month options and scrolls to the appropriate month when the selected year
     * changes.
     */
    suspend fun adjustMonthOptionIfInvalid() {
        if (isSelectedMonthValid) return
        when {
            isMinYearSelected -> {
                val scrollToMonth =
                    if (minDate.monthValue - selectedMonth <= selectedMonth) {
                        minDate.monthValue
                    } else {
                        12
                    }
                monthState.animateScrollToOption(scrollToMonth - 1)
            }
            isMaxYearSelected -> {
                val scrollToMonth =
                    if (selectedMonth - maxDate.monthValue <= 12 - selectedMonth) {
                        maxDate.monthValue
                    } else {
                        1
                    }
                monthState.animateScrollToOption(scrollToMonth - 1)
            }
        }
    }

    /**
     * Adjusts the day options and scrolls to the appropriate day when the selected year or month
     * changes.
     */
    suspend fun adjustDayOptionIfInvalid() {
        val updatedNumberOfOptions = lengthOfMonth(selectedYear, selectedMonth)
        val scrollToDay =
            when {
                !isSelectedDayValid && isMinMonthSelected -> {
                    if (minDate.dayOfMonth - selectedDay <= selectedDay) {
                        minDate.dayOfMonth
                    } else {
                        updatedNumberOfOptions
                    }
                }
                !isSelectedDayValid && isMaxMonthSelected -> {
                    if (selectedDay - maxDate.dayOfMonth <= updatedNumberOfOptions - selectedDay) {
                        maxDate.dayOfMonth
                    } else {
                        1
                    }
                }
                selectedDay > updatedNumberOfOptions -> {
                    updatedNumberOfOptions
                }
                else -> null
            }
        scrollToDay?.let { dayState.animateScrollToOption(it - 1) }
        if (updatedNumberOfOptions != dayState.numberOfOptions) {
            dayState.numberOfOptions = updatedNumberOfOptions
        }
    }
}

private fun createDescriptionDatePicker(
    selectedIndex: Int?,
    selectedValue: Int,
    label: String,
): String = if (selectedIndex == null) label else "$label, $selectedValue"
