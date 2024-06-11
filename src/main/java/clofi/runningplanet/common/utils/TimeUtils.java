package clofi.runningplanet.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class TimeUtils {

	public static LocalDateTime getStartOfDay(LocalDate localDate) {
		return LocalDateTime.of(localDate, LocalTime.MIN);
	}

	public static LocalDateTime getEndOfDay(LocalDate localDate) {
		return LocalDateTime.of(localDate, LocalTime.MAX.withNano(999999000));
	}

}
