package com.example.demo.util.time

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

fun Instant.truncatedToMillis(): Instant = truncatedTo(ChronoUnit.MILLIS)
fun Instant.truncatedToSeconds(): Instant = truncatedTo(ChronoUnit.SECONDS)


fun Instant.toLocaleDateTimeUTC(): LocalDateTime = LocalDateTime.ofInstant(this, ZoneOffset.UTC)
fun Instant.toLocaleDateUTC(): LocalDate = LocalDate.ofInstant(this, ZoneOffset.UTC)