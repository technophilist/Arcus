package com.example.arcus.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * A string that contains the current hour in the following format - "hh a". It also ensures that
 * the length of the string will be consistent regardless of whether the current hour is a single
 * digit or not.
 *
 * Examples :
 *  - "10 AM" -> "10 AM"
 *  - "01 AM" -> " 1 AM" --> notice how the string gets padded with an empty space at the front]
 */
val LocalDateTime.hourStringInTwelveHourFormat: String
    get() {
        //h - Hour in am/pm (1-12)
        //m - Minute in hour
        //a - Am/pm marker
        val dateTimeFormatter = DateTimeFormatter.ofPattern("hh a")
        // Add empty characters to the start of the string if the hour is a single digit number
        // to ensure that the length of the hour text remains constant, regardless of whether
        // the hour text is a single digit or not.
        return format(dateTimeFormatter).let {
            if (it.startsWith("0")) it.replaceFirst('0', ' ')
            else it
        }
    }