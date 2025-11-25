package kotlin.time;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.time.ComparableTimeMark;
import kotlin.time.Duration;
import kotlin.time.TimeSource;

@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\b'\u0018\u00002\u00020\u0001:\u0001\u000bB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010\t\u001a\u00020\nH$R\u0014\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\f"}, d2 = {"Lkotlin/time/AbstractLongTimeSource;", "Lkotlin/time/TimeSource$WithComparableMarks;", "unit", "Lkotlin/time/DurationUnit;", "(Lkotlin/time/DurationUnit;)V", "getUnit", "()Lkotlin/time/DurationUnit;", "markNow", "Lkotlin/time/ComparableTimeMark;", "read", "", "LongTimeMark", "kotlin-stdlib"}, k = 1, mv = {1, 8, 0}, xi = 48)
/* compiled from: TimeSources.kt */
public abstract class AbstractLongTimeSource implements TimeSource.WithComparableMarks {
    private final DurationUnit unit;

    /* access modifiers changed from: protected */
    public abstract long read();

    public AbstractLongTimeSource(DurationUnit unit2) {
        Intrinsics.checkNotNullParameter(unit2, "unit");
        this.unit = unit2;
    }

    /* access modifiers changed from: protected */
    public final DurationUnit getUnit() {
        return this.unit;
    }

    @Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u000e\n\u0000\b\u0002\u0018\u00002\u00020\u0001B \u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007ø\u0001\u0000¢\u0006\u0002\u0010\bJ\u0015\u0010\n\u001a\u00020\u0007H\u0000ø\u0001\u0001ø\u0001\u0000¢\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\r\u001a\u00020\u0007H\u0016ø\u0001\u0001ø\u0001\u0000¢\u0006\u0004\b\u000e\u0010\fJ\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0002J\b\u0010\u0013\u001a\u00020\u0014H\u0016J\u001e\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0001H\u0002ø\u0001\u0001ø\u0001\u0000¢\u0006\u0004\b\u0016\u0010\u0017J\u001b\u0010\u0018\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u0007H\u0002ø\u0001\u0000¢\u0006\u0004\b\u001a\u0010\u001bJ\b\u0010\u001c\u001a\u00020\u001dH\u0016R\u0016\u0010\u0006\u001a\u00020\u0007X\u0004ø\u0001\u0000ø\u0001\u0001¢\u0006\u0004\n\u0002\u0010\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000\u0002\b\n\u0002\b\u0019\n\u0002\b!¨\u0006\u001e"}, d2 = {"Lkotlin/time/AbstractLongTimeSource$LongTimeMark;", "Lkotlin/time/ComparableTimeMark;", "startedAt", "", "timeSource", "Lkotlin/time/AbstractLongTimeSource;", "offset", "Lkotlin/time/Duration;", "(JLkotlin/time/AbstractLongTimeSource;JLkotlin/jvm/internal/DefaultConstructorMarker;)V", "J", "effectiveDuration", "effectiveDuration-UwyO8pc$kotlin_stdlib", "()J", "elapsedNow", "elapsedNow-UwyO8pc", "equals", "", "other", "", "hashCode", "", "minus", "minus-UwyO8pc", "(Lkotlin/time/ComparableTimeMark;)J", "plus", "duration", "plus-LRDsOJo", "(J)Lkotlin/time/ComparableTimeMark;", "toString", "", "kotlin-stdlib"}, k = 1, mv = {1, 8, 0}, xi = 48)
    /* compiled from: TimeSources.kt */
    private static final class LongTimeMark implements ComparableTimeMark {
        private final long offset;
        private final long startedAt;
        private final AbstractLongTimeSource timeSource;

        public /* synthetic */ LongTimeMark(long j, AbstractLongTimeSource abstractLongTimeSource, long j2, DefaultConstructorMarker defaultConstructorMarker) {
            this(j, abstractLongTimeSource, j2);
        }

        private LongTimeMark(long startedAt2, AbstractLongTimeSource timeSource2, long offset2) {
            Intrinsics.checkNotNullParameter(timeSource2, "timeSource");
            this.startedAt = startedAt2;
            this.timeSource = timeSource2;
            this.offset = offset2;
        }

        public int compareTo(ComparableTimeMark other) {
            return ComparableTimeMark.DefaultImpls.compareTo(this, other);
        }

        public boolean hasNotPassedNow() {
            return ComparableTimeMark.DefaultImpls.hasNotPassedNow(this);
        }

        public boolean hasPassedNow() {
            return ComparableTimeMark.DefaultImpls.hasPassedNow(this);
        }

        /* renamed from: minus-LRDsOJo  reason: not valid java name */
        public ComparableTimeMark m1437minusLRDsOJo(long duration) {
            return ComparableTimeMark.DefaultImpls.m1448minusLRDsOJo(this, duration);
        }

        /* renamed from: elapsedNow-UwyO8pc  reason: not valid java name */
        public long m1435elapsedNowUwyO8pc() {
            return Duration.m1485isInfiniteimpl(this.offset) ? Duration.m1505unaryMinusUwyO8pc(this.offset) : Duration.m1488minusLRDsOJo(DurationKt.toDuration(this.timeSource.read() - this.startedAt, this.timeSource.getUnit()), this.offset);
        }

        /* renamed from: plus-LRDsOJo  reason: not valid java name */
        public ComparableTimeMark m1440plusLRDsOJo(long duration) {
            return new LongTimeMark(this.startedAt, this.timeSource, Duration.m1489plusLRDsOJo(this.offset, duration), (DefaultConstructorMarker) null);
        }

        /* renamed from: minus-UwyO8pc  reason: not valid java name */
        public long m1438minusUwyO8pc(ComparableTimeMark other) {
            Intrinsics.checkNotNullParameter(other, "other");
            if (!(other instanceof LongTimeMark) || !Intrinsics.areEqual((Object) this.timeSource, (Object) ((LongTimeMark) other).timeSource)) {
                throw new IllegalArgumentException("Subtracting or comparing time marks from different time sources is not possible: " + this + " and " + other);
            } else if (Duration.m1458equalsimpl0(this.offset, ((LongTimeMark) other).offset) && Duration.m1485isInfiniteimpl(this.offset)) {
                return Duration.Companion.m1555getZEROUwyO8pc();
            } else {
                long offsetDiff = Duration.m1488minusLRDsOJo(this.offset, ((LongTimeMark) other).offset);
                long startedAtDiff = DurationKt.toDuration(this.startedAt - ((LongTimeMark) other).startedAt, this.timeSource.getUnit());
                return Duration.m1458equalsimpl0(startedAtDiff, Duration.m1505unaryMinusUwyO8pc(offsetDiff)) ? Duration.Companion.m1555getZEROUwyO8pc() : Duration.m1489plusLRDsOJo(startedAtDiff, offsetDiff);
            }
        }

        public boolean equals(Object other) {
            return (other instanceof LongTimeMark) && Intrinsics.areEqual((Object) this.timeSource, (Object) ((LongTimeMark) other).timeSource) && Duration.m1458equalsimpl0(m1438minusUwyO8pc((ComparableTimeMark) other), Duration.Companion.m1555getZEROUwyO8pc());
        }

        /* renamed from: effectiveDuration-UwyO8pc$kotlin_stdlib  reason: not valid java name */
        public final long m1434effectiveDurationUwyO8pc$kotlin_stdlib() {
            if (Duration.m1485isInfiniteimpl(this.offset)) {
                return this.offset;
            }
            DurationUnit unit = this.timeSource.getUnit();
            if (unit.compareTo(DurationUnit.MILLISECONDS) >= 0) {
                return Duration.m1489plusLRDsOJo(DurationKt.toDuration(this.startedAt, unit), this.offset);
            }
            long scale = DurationUnitKt.convertDurationUnit(1, DurationUnit.MILLISECONDS, unit);
            long startedAtMillis = this.startedAt / scale;
            long startedAtRem = this.startedAt % scale;
            long arg0$iv = this.offset;
            long offsetSeconds = Duration.m1474getInWholeSecondsimpl(arg0$iv);
            int offsetNanoseconds = Duration.m1476getNanosecondsComponentimpl(arg0$iv);
            int offsetMillis = offsetNanoseconds / DurationKt.NANOS_IN_MILLIS;
            int offsetRemNanos = offsetNanoseconds % DurationKt.NANOS_IN_MILLIS;
            long j = scale;
            long scale2 = DurationKt.toDuration(startedAtRem, unit);
            Duration.Companion companion = Duration.Companion;
            DurationUnit durationUnit = unit;
            long j2 = startedAtRem;
            long r1 = Duration.m1489plusLRDsOJo(scale2, DurationKt.toDuration(offsetRemNanos, DurationUnit.NANOSECONDS));
            Duration.Companion companion2 = Duration.Companion;
            long r12 = Duration.m1489plusLRDsOJo(r1, DurationKt.toDuration(((long) offsetMillis) + startedAtMillis, DurationUnit.MILLISECONDS));
            Duration.Companion companion3 = Duration.Companion;
            return Duration.m1489plusLRDsOJo(r12, DurationKt.toDuration(offsetSeconds, DurationUnit.SECONDS));
        }

        public int hashCode() {
            return Duration.m1481hashCodeimpl(m1434effectiveDurationUwyO8pc$kotlin_stdlib());
        }

        public String toString() {
            return "LongTimeMark(" + this.startedAt + DurationUnitKt.shortName(this.timeSource.getUnit()) + " + " + Duration.m1502toStringimpl(this.offset) + " (=" + Duration.m1502toStringimpl(m1434effectiveDurationUwyO8pc$kotlin_stdlib()) + "), " + this.timeSource + ')';
        }
    }

    public ComparableTimeMark markNow() {
        return new LongTimeMark(read(), this, Duration.Companion.m1555getZEROUwyO8pc(), (DefaultConstructorMarker) null);
    }
}
