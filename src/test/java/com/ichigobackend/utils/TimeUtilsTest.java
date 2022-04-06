package com.ichigobackend.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TimeUtilsTest {

	@Test
	void shouldParseTimeToISOString() {
		// given
		LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

		// when
		String expected = TimeUtils.timeToISOString(now);

		// then
		assertThat(expected).isEqualTo("2022-04-06T00:00:00Z");
	}

	@Test
	void shouldNotParseTimeToISOString() {
		// given
		// when
		String expected = TimeUtils.timeToISOString(null);

		// then
		assertThat(expected).isNull();
	}

	@Test
	void shouldParseToMidnight() {
		// given
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nowMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

		// when
		LocalDateTime expected = TimeUtils.toMidnight(now);

		// then
		assertThat(expected).isEqualTo(nowMidnight);
	}

	@Test
	void shouldNotParseToMidnight() {
		// given
		// when
		LocalDateTime expected = TimeUtils.toMidnight(null);

		// then
		assertThat(expected).isNull();
	}

	@Test
	void shouldParseISOStringToTime() {
		// given
		String timestamp = "2022-04-06T12:00:00Z";
		LocalDateTime timestampDateTime = LocalDateTime.of(2022, 4, 6, 12, 0, 0);

		// when
		LocalDateTime expected = TimeUtils.ISOStringToTime(timestamp);

		// then
		assertThat(expected).isEqualTo(timestampDateTime);
	}

	@Test
	void shouldNotParseISOStringToTime_ThrowIllegalArgumentException() {
		// given
		String timestampWrongFormat = "2022-04-06Txx:xx:xx";

		// when
		// then
		assertThatThrownBy(() -> TimeUtils.ISOStringToTime(timestampWrongFormat))
				.isInstanceOf(IllegalArgumentException.class);
	}

}
