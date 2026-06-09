/*
 * Copyright 2026 youngmonkeys.org
 *
 * Licensed under the ezyplatform, Version 1.0.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://youngmonkeys.org/licenses/ezyplatform-1.0.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.youngmonkeys.ezyplatform.data;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

/**
 * ConExpression (Cron Expression) — a recurring schedule expression.
 *
 * <h2>Expression format</h2>
 *
 * <p>Two forms are supported:
 * <pre>
 * 5 fields:  MINUTE  HOUR  DAY-OF-MONTH  MONTH  DAY-OF-WEEK
 * 6 fields:  SECOND  MINUTE  HOUR  DAY-OF-MONTH  MONTH  DAY-OF-WEEK
 * </pre>
 *
 * <h2>Field value ranges</h2>
 * <pre>
 * SECOND       0-59
 * MINUTE       0-59
 * HOUR         0-23
 * DAY-OF-MONTH 1-31
 * MONTH        1-12
 * DAY-OF-WEEK  0-6  (0 = Sunday, 1 = Monday, ..., 6 = Saturday)
 * </pre>
 *
 * <h2>Field syntax</h2>
 * <pre>
 * *          any value in the allowed range
 * n          exact value (e.g. 5)
 * n-m        range from n to m inclusive (e.g. 1-5)
 * *&#47;step     every step starting from min (e.g. *&#47;15 -> 0,15,30,45)
 * n/step     from n, step until max (e.g. 10/5 -> 10,15,20,...)
 * n-m/step   range n-m with step (e.g. 0-30/10 -> 0,10,20,30)
 * a,b,c      list of values (e.g. 1,15,30) — combinable with any syntax above
 * </pre>
 *
 * <h2>Common examples — 5 fields</h2>
 * <pre>
 * "* * * * *"          every minute
 * "0 * * * *"          start of every hour (minute 0)
 * "0 8 * * *"          every day at 08:00
 * "30 8 * * *"         every day at 08:30
 * "0 8,17 * * *"       every day at 08:00 and 17:00
 * "0 8-10 * * *"       every day at 08:00, 09:00, and 10:00
 * "0 8 1 * *"          1st of every month at 08:00
 * "0 8 1,15 * *"       1st and 15th of every month at 08:00
 * "0 8 * * 1"          every Monday at 08:00
 * "0 8 * * 1-5"        Monday through Friday at 08:00
 * "0 8 * * 0,6"        weekends (Saturday and Sunday) at 08:00
 * "0 0 1 1 *"          January 1st at 00:00 (once a year)
 * "0 0 1 * *"          1st of every month at 00:00
 * "*&#47;5 * * * *"         every 5 minutes
 * "*&#47;15 * * * *"        every 15 minutes
 * "*&#47;30 * * * *"        every 30 minutes
 * "0 *&#47;2 * * *"         every 2 hours starting at 00:00
 * "0 9-17&#47;2 * * *"      every 2 hours during business hours (9,11,13,15,17)
 * "0 8 * 1,7 *"        every day in January and July at 08:00
 * "0 8 * 3-6 *"        every day from March to June at 08:00
 * "0 8 * *&#47;3 *"        every quarter (months 1,4,7,10) at 08:00
 * </pre>
 *
 * <h2>Examples — 6 fields (with SECOND)</h2>
 * <pre>
 * "0 * * * * *"        start of every minute (second 0)
 * "30 * * * * *"       second 30 of every minute
 * "0&#47;30 * * * * *"     every 30 seconds
 * "0&#47;10 * * * * *"     every 10 seconds
 * "0 *&#47;5 * * * *"      every 5 minutes starting at minute 0
 * "0 0 8 * * *"        every day at 08:00:00
 * "0 30 8 * * 1-5"     Monday through Friday at 08:30:00
 * "0 0 0 1 * *"        1st of every month at 00:00:00
 * "0 0 0 * * 0"        every Sunday at midnight
 * </pre>
 *
 * <h2>List syntax (comma-separated)</h2>
 * <pre>
 * "0 8,12,18 * * *"    every day at 08:00, 12:00, and 18:00
 * "0 0 1,10,20 * *"    1st, 10th, and 20th of every month at 00:00
 * "0 0 * * 1,3,5"      Monday, Wednesday, and Friday at 00:00
 * "0,30 8 * * *"       every day at 08:00 and 08:30
 * "0 0 1 1,4,7,10 *"   start of each quarter (Jan 1, Apr 1, Jul 1, Oct 1) at 00:00
 * </pre>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // Parse and compute the next trigger time
 * ConExpression expr = ConExpression.parse("0 8 * * *");
 * LocalDateTime next = expr.nextTimeOf(LocalDateTime.now());
 *
 * // From epoch milliseconds
 * LocalDateTime next2 = expr.nextTimeOf(System.currentTimeMillis());
 *
 * // Using the Builder directly (advanced)
 * TreeSet<Integer> hours = new TreeSet<>(List.of(8, 17));
 * ConExpression custom = ConExpression.builder()
 *     .expression("custom")
 *     .seconds(new TreeSet<>(List.of(0)))
 *     .minutes(new TreeSet<>(List.of(0)))
 *     .hours(hours)
 *     .daysOfMonth(new TreeSet<>(
 *         IntStream.rangeClosed(1, 31).boxed().collect(Collectors.toList())))
 *     .months(new TreeSet<>(IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList())))
 *     .daysOfWeek(new TreeSet<>(IntStream.rangeClosed(0, 6).boxed().collect(Collectors.toList())))
 *     .build();
 * }</pre>
 *
 * <h2>Return value of nextTimeOf</h2>
 *
 * <p>Returns {@code null} if no trigger time is found within 4 years
 * (e.g. a calendar-impossible expression like "0 8 31 2 *" — February 31st).
 */
@Getter
public class CronExpression {
    private final String expression;
    private final TreeSet<Integer> seconds;
    private final TreeSet<Integer> minutes;
    private final TreeSet<Integer> hours;
    private final TreeSet<Integer> daysOfMonth;
    private final TreeSet<Integer> months;
    private final TreeSet<Integer> daysOfWeek;

    private CronExpression(Builder builder) {
        this.expression = builder.expression;
        this.seconds = builder.seconds;
        this.minutes = builder.minutes;
        this.hours = builder.hours;
        this.daysOfMonth = builder.daysOfMonth;
        this.months = builder.months;
        this.daysOfWeek = builder.daysOfWeek;
    }

    /**
     * Parses a cron expression string into a {@link CronExpression}.
     *
     * @param expression a whitespace-separated string with 5 or 6 fields
     * @return the parsed {@link CronExpression}
     * @throws IllegalArgumentException if the expression does not have 5 or 6 fields
     */
    public static CronExpression parse(String expression) {
        if (isBlank(expression)) {
            return null;
        }
        String[] parts = expression.trim().split("\\s+");
        if (parts.length == 5) {
            // minute hour day-of-month month day-of-week (no seconds)
            return builder()
                .expression(expression)
                .seconds(parseField("0", 0, 59))
                .minutes(parseField(parts[0], 0, 59))
                .hours(parseField(parts[1], 0, 23))
                .daysOfMonth(parseField(parts[2], 1, 31))
                .months(parseField(parts[3], 1, 12))
                .daysOfWeek(parseField(parts[4], 0, 6))
                .build();
        }
        if (parts.length == 6) {
            // second minute hour day-of-month month day-of-week
            return builder()
                .expression(expression)
                .seconds(parseField(parts[0], 0, 59))
                .minutes(parseField(parts[1], 0, 59))
                .hours(parseField(parts[2], 0, 23))
                .daysOfMonth(parseField(parts[3], 1, 31))
                .months(parseField(parts[4], 1, 12))
                .daysOfWeek(parseField(parts[5], 0, 6))
                .build();
        }
        throw new IllegalArgumentException(
            "Invalid con expression: " + expression
                + " (expected 5 or 6 fields)"
        );
    }

    private static TreeSet<Integer> parseField(
        String field,
        int min,
        int max
    ) {
        TreeSet<Integer> values = new TreeSet<>();
        for (String part : field.split(",")) {
            if ("*".equals(part)) {
                for (int i = min; i <= max; i++) {
                    values.add(i);
                }
            } else if (part.startsWith("*/")) {
                int step = Integer.parseInt(part.substring(2));
                for (int i = min; i <= max; i += step) {
                    values.add(i);
                }
            } else if (part.contains("/")) {
                String[] tokens = part.split("/");
                int step = Integer.parseInt(tokens[1]);
                if (tokens[0].contains("-")) {
                    String[] range = tokens[0].split("-");
                    int start = Integer.parseInt(range[0]);
                    int end = Integer.parseInt(range[1]);
                    for (int i = start; i <= end; i += step) {
                        values.add(i);
                    }
                } else {
                    int start = Integer.parseInt(tokens[0]);
                    for (int i = start; i <= max; i += step) {
                        values.add(i);
                    }
                }
            } else if (part.contains("-")) {
                String[] range = part.split("-");
                int start = Integer.parseInt(range[0]);
                int end = Integer.parseInt(range[1]);
                for (int i = start; i <= end; i++) {
                    values.add(i);
                }
            } else {
                values.add(Integer.parseInt(part));
            }
        }
        return values;
    }

    // java.time DayOfWeek: MONDAY=1..SUNDAY=7; cron uses 0=Sunday
    private static int toCronDayOfWeek(DayOfWeek dayOfWeek) {
        return dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.getValue();
    }

    /**
     * Returns the next trigger time after {@code time}.
     *
     * <p>Search starts from {@code time + 1 second}. Returns {@code null}
     * if no match is found within 4 years.
     *
     * @param time the reference time (typically {@code LocalDateTime.now()})
     * @return the next trigger time, or {@code null} if none exists
     */
    public LocalDateTime nextTimeOf(LocalDateTime time) {
        LocalDateTime candidate = time.plusSeconds(1).withNano(0);
        LocalDateTime limit = time.plusYears(4);

        while (candidate.isBefore(limit)) {
            if (!months.contains(candidate.getMonthValue())) {
                candidate = advanceToNextMonth(candidate);
                continue;
            }
            if (!daysOfMonth.contains(candidate.getDayOfMonth())
                || !daysOfWeek.contains(toCronDayOfWeek(candidate.getDayOfWeek()))) {
                candidate = candidate
                    .withHour(0).withMinute(0).withSecond(0)
                    .plusDays(1);
                continue;
            }
            if (!hours.contains(candidate.getHour())) {
                Integer next = hours.ceiling(candidate.getHour());
                candidate = next != null
                    ? candidate.withHour(next).withMinute(0).withSecond(0)
                    : candidate.withHour(0).withMinute(0).withSecond(0).plusDays(1);
                continue;
            }
            if (!minutes.contains(candidate.getMinute())) {
                Integer next = minutes.ceiling(candidate.getMinute());
                candidate = next != null
                    ? candidate.withMinute(next).withSecond(0)
                    : candidate.withMinute(0).withSecond(0).plusHours(1);
                continue;
            }
            if (!seconds.contains(candidate.getSecond())) {
                Integer next = seconds.ceiling(candidate.getSecond());
                candidate = next != null
                    ? candidate.withSecond(next)
                    : candidate.withSecond(0).plusMinutes(1);
                continue;
            }
            return candidate;
        }
        return null;
    }

    /**
     * Returns the next trigger time after the given epoch millisecond timestamp.
     *
     * <p>Converts {@code timestamp} to a {@link LocalDateTime} using
     * {@link ZoneId#systemDefault()}, computes the next trigger, then converts
     * the result back to epoch milliseconds.
     *
     * @param timestamp epoch milliseconds (e.g. {@code System.currentTimeMillis()})
     * @return the next trigger time as epoch milliseconds, or {@code -1} if none exists
     */
    public long nextTimeOf(long timestamp) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime candidate = LocalDateTime
            .ofInstant(Instant.ofEpochMilli(timestamp), zone)
            .plusSeconds(1)
            .withNano(0);
        LocalDateTime limit = LocalDateTime
            .ofInstant(Instant.ofEpochMilli(timestamp), zone)
            .plusYears(4);

        while (candidate.isBefore(limit)) {
            if (!months.contains(candidate.getMonthValue())) {
                candidate = advanceToNextMonth(candidate);
                continue;
            }
            if (!daysOfMonth.contains(candidate.getDayOfMonth())
                || !daysOfWeek.contains(toCronDayOfWeek(candidate.getDayOfWeek()))) {
                candidate = candidate
                    .withHour(0).withMinute(0).withSecond(0)
                    .plusDays(1);
                continue;
            }
            if (!hours.contains(candidate.getHour())) {
                Integer next = hours.ceiling(candidate.getHour());
                candidate = next != null
                    ? candidate.withHour(next).withMinute(0).withSecond(0)
                    : candidate.withHour(0).withMinute(0).withSecond(0).plusDays(1);
                continue;
            }
            if (!minutes.contains(candidate.getMinute())) {
                Integer next = minutes.ceiling(candidate.getMinute());
                candidate = next != null
                    ? candidate.withMinute(next).withSecond(0)
                    : candidate.withMinute(0).withSecond(0).plusHours(1);
                continue;
            }
            if (!seconds.contains(candidate.getSecond())) {
                Integer next = seconds.ceiling(candidate.getSecond());
                candidate = next != null
                    ? candidate.withSecond(next)
                    : candidate.withSecond(0).plusMinutes(1);
                continue;
            }
            return candidate.atZone(zone).toInstant().toEpochMilli();
        }
        return -1;
    }

    private LocalDateTime advanceToNextMonth(LocalDateTime candidate) {
        Integer nextMonth = months.higher(candidate.getMonthValue());
        if (nextMonth != null) {
            return candidate
                .withMonth(nextMonth)
                .withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0);
        }
        return candidate
            .withMonth(months.first())
            .withDayOfMonth(1)
            .withHour(0).withMinute(0).withSecond(0)
            .plusYears(1);
    }

    @Override
    public String toString() {
        boolean allSec = isAllRange(seconds, 0, 59);
        boolean allMin = isAllRange(minutes, 0, 59);
        boolean allHrs = isAllRange(hours, 0, 23);
        boolean allDom = isAllRange(daysOfMonth, 1, 31);
        boolean allMon = isAllRange(months, 1, 12);
        boolean allDow = isAllRange(daysOfWeek, 0, 6);
        boolean onlySec0 = isSingleValue(seconds);
        boolean onlyMin0 = isSingleValue(minutes);

        // every second
        if (allSec && allMin && allHrs && allDom && allMon && allDow) {
            return "every second";
        }

        // every N seconds
        if (allMin && allHrs && allDom && allMon && allDow) {
            Integer step = getStep(seconds, 60);
            if (step != null && seconds.first() == 0) {
                return "every " + plural(step, "second");
            }
        }

        // every minute / every N minutes
        if (onlySec0 && allHrs && allDom && allMon && allDow) {
            if (allMin) {
                return "every minute";
            }
            Integer step = getStep(minutes, 60);
            if (step != null && minutes.first() == 0) {
                return "every " + plural(step, "minute");
            }
        }

        // every hour / every N hours (optionally in a range)
        if (onlySec0 && onlyMin0 && allDom && allMon && allDow) {
            if (allHrs) {
                return "every hour";
            }
            Integer step = getStep(hours, 24);
            if (step != null) {
                if (hours.first() == 0) {
                    return "every " + plural(step, "hour");
                }
                return "every " + plural(step, "hour")
                    + " from " + String.format("%02d:00", hours.first())
                    + " to " + String.format("%02d:00", hours.last());
            }
        }

        // at TIME[, DAY-CONSTRAINT]
        List<String> times = buildTimeList(onlySec0);
        if (!times.isEmpty()) {
            String timePart = "at " + joinEnglish(times);
            String dayPart = buildDayConstraint(allDom, allMon, allDow);
            return timePart + ", " + dayPart;
        }

        return expression;
    }

    private List<String> buildTimeList(boolean onlySec0) {
        List<String> times = new ArrayList<>();
        boolean showSec = !onlySec0;
        int limit = showSec
            ? hours.size() * minutes.size() * seconds.size()
            : hours.size() * minutes.size();
        if (limit > 8) {
            return times;
        }
        if (showSec) {
            for (int h : hours) {
                for (int m : minutes) {
                    for (int s : seconds) {
                        times.add(String.format("%02d:%02d:%02d", h, m, s));
                    }
                }
            }
        } else {
            for (int h : hours) {
                for (int m : minutes) {
                    times.add(String.format("%02d:%02d", h, m));
                }
            }
        }
        return times;
    }

    private String buildDayConstraint(
        boolean allDom,
        boolean allMon,
        boolean allDow
    ) {
        TreeSet<Integer> weekdays = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5));
        TreeSet<Integer> weekends = new TreeSet<>(Arrays.asList(0, 6));

        if (allDom && allMon && allDow) {
            return "every day";
        }
        if (allDom && allMon) {
            if (daysOfWeek.equals(weekdays)) {
                return "every weekday";
            }
            if (daysOfWeek.equals(weekends)) {
                return "on weekends";
            }
            return describeDow();
        }
        if (allDow && allMon) {
            return "on the " + describeDom() + " of every month";
        }
        if (allDow && allDom) {
            return "every day in " + describeMonths();
        }
        if (allDow) {
            return "on the " + describeDom() + " of " + describeMonths();
        }
        if (allDom) {
            return describeDow() + " in " + describeMonths();
        }
        // both DOW and DOM constrained (AND semantics)
        return describeDow() + " on the " + describeDom() + " of " +
            (allMon ? "every month" : describeMonths());
    }

    private String describeDow() {
        if (daysOfWeek.size() == 1) {
            return "every " + getDayNameByIndex(daysOfWeek.first());
        }
        if (isConsecutiveRange(daysOfWeek, 7)) {
            return "every " + getDayNameByIndex(daysOfWeek.first())
                + " through " + getDayNameByIndex(daysOfWeek.last());
        }
        List<String> names = new ArrayList<>();
        for (int d : daysOfWeek) {
            names.add(getDayNameByIndex(d));
        }
        return "every " + joinEnglish(names);
    }

    private String describeDom() {
        if (daysOfMonth.size() == 1) {
            return ordinal(daysOfMonth.first());
        }
        if (isConsecutiveRange(daysOfMonth, 30)) {
            return ordinal(daysOfMonth.first()) +
                " through " + ordinal(daysOfMonth.last());
        }
        List<String> ords = new ArrayList<>();
        for (int d : daysOfMonth) {
            ords.add(ordinal(d));
        }
        return joinEnglish(ords);
    }

    private String describeMonths() {
        if (months.size() == 1) {
            return getMonthNameByIndex(months.first());
        }
        if (isConsecutiveRange(months, 12)) {
            return getMonthNameByIndex(months.first()) +
                " through " + getMonthNameByIndex(months.last());
        }
        List<String> names = new ArrayList<>();
        for (int m : months) {
            names.add(getMonthNameByIndex(m));
        }
        return joinEnglish(names);
    }

    private static boolean isAllRange(
        TreeSet<Integer> set,
        int min,
        int max
    ) {
        return set.size() == (max - min + 1)
            && set.first() == min
            && set.last() == max;
    }

    private static boolean isSingleValue(
        TreeSet<Integer> set
    ) {
        return set.size() == 1 && set.first() == 0;
    }

    private static boolean isConsecutiveRange(
        TreeSet<Integer> set,
        int maxValueCount
    ) {
        Integer step = getStep(set, maxValueCount);
        return step != null && step == 1;
    }

    private static Integer getStep(
        TreeSet<Integer> set,
        int maxValueCount
    ) {
        if (set.size() < 2) {
            return null;
        }
        int[] arr = set.stream().mapToInt(Integer::intValue).toArray();
        int step = arr[1] - arr[0];
        if (step <= 0 || maxValueCount % step != 0) {
            return null;
        }
        for (int i = 2; i < arr.length; i++) {
            if (arr[i] - arr[i - 1] != step) {
                return null;
            }
        }
        return step;
    }

    private static String plural(int n, String unit) {
        return n + " " + unit + (n == 1 ? "" : "s");
    }

    private static String ordinal(int n) {
        String suffix;
        if (n % 100 >= 11 && n % 100 <= 13) {
            suffix = "th";
        } else {
            switch (n % 10) {
                case 1: {
                    suffix = "st";
                    break;
                }
                case 2: {
                    suffix = "nd";
                    break;
                }
                case 3: {
                    suffix = "rd";
                    break;
                }
                default: {
                    suffix = "th";
                }
            }
        }
        return n + suffix;
    }

    private static String joinEnglish(List<String> items) {
        int size = items.size();
        if (size == 0) {
            return "";
        }
        if (size == 1) {
            return items.get(0);
        }
        if (size == 2) {
            return items.get(0) + " and " + items.get(1);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            sb.append(items.get(i)).append(", ");
        }
        sb.append("and ").append(items.get(size - 1));
        return sb.toString();
    }

    private static String getDayNameByIndex(
        int index
    ) {
        // DayOfWeek.of(): 1=Monday..7=Sunday; cron: 0=Sunday, 1=Monday..6=Saturday
        return DayOfWeek.of(index == 0 ? 7 : index).getDisplayName(
            TextStyle.FULL,
            Locale.ENGLISH
        );
    }

    private static String getMonthNameByIndex(
        int index
    ) {
        // Month.of(): 1=January..12=December — matches cron month range
        return Month.of(index).getDisplayName(
            TextStyle.FULL,
            Locale.ENGLISH
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<CronExpression> {
        private String expression;
        private TreeSet<Integer> seconds;
        private TreeSet<Integer> minutes;
        private TreeSet<Integer> hours;
        private TreeSet<Integer> daysOfMonth;
        private TreeSet<Integer> months;
        private TreeSet<Integer> daysOfWeek;

        public Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        public Builder seconds(TreeSet<Integer> seconds) {
            this.seconds = seconds;
            return this;
        }

        public Builder minutes(TreeSet<Integer> minutes) {
            this.minutes = minutes;
            return this;
        }

        public Builder hours(TreeSet<Integer> hours) {
            this.hours = hours;
            return this;
        }

        public Builder daysOfMonth(TreeSet<Integer> daysOfMonth) {
            this.daysOfMonth = daysOfMonth;
            return this;
        }

        public Builder months(TreeSet<Integer> months) {
            this.months = months;
            return this;
        }

        public Builder daysOfWeek(TreeSet<Integer> daysOfWeek) {
            this.daysOfWeek = daysOfWeek;
            return this;
        }

        @Override
        public CronExpression build() {
            return new CronExpression(this);
        }
    }
}
