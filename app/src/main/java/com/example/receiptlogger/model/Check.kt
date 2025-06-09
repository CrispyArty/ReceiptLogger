/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.receiptlogger.model

import android.icu.util.TimeZone
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

data class CheckItem(
    val name: String,
    val count: Float,
    val itemPrice: Float,
    var totalPrice: Float,
)

data class Check(
    var description: String,
    val items: List<CheckItem>,
    var totalPrice: Float,
    var date: LocalDateTime = LocalDateTime.now()
)
