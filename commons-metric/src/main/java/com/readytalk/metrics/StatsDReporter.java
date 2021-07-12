package com.readytalk.metrics;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metered;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

public class StatsDReporter extends ScheduledReporter {
    private static final Logger LOG = LoggerFactory.getLogger(StatsDReporter.class);

    private final StatsD statsD;
    private final String tags;

    private StatsDReporter(final MetricRegistry registry,
                           final StatsD statsD,
                           final String tags,
                           final TimeUnit rateUnit,
                           final TimeUnit durationUnit,
                           final MetricFilter filter) {
        super(registry, "okcheon-statsd-reporter", filter, rateUnit, durationUnit);
        this.statsD = statsD;
        this.tags = tags;
    }

    public static Builder forRegistry(final MetricRegistry registry) {
        return new Builder(registry);
    }

    @Override
    //Metrics 3.0 interface specifies the raw Gauge type
    public void report(final SortedMap<String, Gauge> gauges,
                       final SortedMap<String, Counter> counters,
                       final SortedMap<String, Histogram> histograms,
                       final SortedMap<String, Meter> meters,
                       final SortedMap<String, Timer> timers) {

        try {
            statsD.connect();

            gauges.forEach(this::reportGauge);
            counters.forEach(this::reportCounter);
            histograms.forEach(this::reportHistogram);
            meters.forEach(this::reportMetered);
            timers.forEach(this::reportTimer);
        } catch (IOException e) {
            LOG.warn("Unable to report to StatsD. StatsD: {}", statsD, e);
        } finally {
            try {
                statsD.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    private void reportTimer(final String name, final Timer timer) {
        final Snapshot snapshot = timer.getSnapshot();

        statsD.send(withPostfix(name, "m=max"), formatNumber(convertDuration(snapshot.getMax())));
        statsD.send(withPostfix(name, "m=mean"), formatNumber(convertDuration(snapshot.getMean())));
        statsD.send(withPostfix(name, "m=min"), formatNumber(convertDuration(snapshot.getMin())));
        statsD.send(withPostfix(name, "m=stddev"), formatNumber(convertDuration(snapshot.getStdDev())));
        statsD.send(withPostfix(name, "m=p50"), formatNumber(convertDuration(snapshot.getMedian())));
        statsD.send(withPostfix(name, "m=p95"), formatNumber(convertDuration(snapshot.get95thPercentile())));
        statsD.send(withPostfix(name, "m=p99"), formatNumber(convertDuration(snapshot.get99thPercentile())));

        reportMetered(name, timer);
    }

    private void reportMetered(final String name, final Metered meter) {
        statsD.send(withPostfix(name, "m=samples"), formatNumber(meter.getCount()));
        statsD.send(withPostfix(name, "m=m1_rate"), formatNumber(convertRate(meter.getOneMinuteRate())));
        statsD.send(withPostfix(name, "m=m5_rate"), formatNumber(convertRate(meter.getFiveMinuteRate())));
        statsD.send(withPostfix(name, "m=m15_rate"), formatNumber(convertRate(meter.getFifteenMinuteRate())));
        statsD.send(withPostfix(name, "m=mean_rate"), formatNumber(convertRate(meter.getMeanRate())));
    }

    private void reportHistogram(final String name, final Histogram histogram) {
        final Snapshot snapshot = histogram.getSnapshot();
        statsD.send(withPostfix(name, "m=samples"), formatNumber(histogram.getCount()));
        statsD.send(withPostfix(name, "m=max"), formatNumber(snapshot.getMax()));
        statsD.send(withPostfix(name, "m=mean"), formatNumber(snapshot.getMean()));
        statsD.send(withPostfix(name, "m=min"), formatNumber(snapshot.getMin()));
        statsD.send(withPostfix(name, "m=stddev"), formatNumber(snapshot.getStdDev()));
        statsD.send(withPostfix(name, "m=p50"), formatNumber(snapshot.getMedian()));
        statsD.send(withPostfix(name, "m=p95"), formatNumber(snapshot.get95thPercentile()));
        statsD.send(withPostfix(name, "m=p99"), formatNumber(snapshot.get99thPercentile()));
    }

    private void reportCounter(final String name, final Counter counter) {
        statsD.send(withPostfix(name), formatNumber(counter.getCount()));
    }

    @SuppressWarnings("rawtypes") //Metrics 3.0 passes us the raw Gauge type
    private void reportGauge(final String name, final Gauge gauge) {
        final String value = format(gauge.getValue());
        if (value != null) {
            statsD.send(withPostfix(name), value);
        }
    }

    @Nullable
    private String format(final Object o) {
        if (o instanceof Float) {
            return formatNumber(((Float) o).doubleValue());
        } else if (o instanceof Double) {
            return formatNumber((Double) o);
        } else if (o instanceof Byte) {
            return formatNumber(((Byte) o).longValue());
        } else if (o instanceof Short) {
            return formatNumber(((Short) o).longValue());
        } else if (o instanceof Integer) {
            return formatNumber(((Integer) o).longValue());
        } else if (o instanceof Long) {
            return formatNumber((Long) o);
        } else if (o instanceof BigInteger) {
            return formatNumber((BigInteger) o);
        } else if (o instanceof BigDecimal) {
            return formatNumber(((BigDecimal) o).doubleValue());
        }
        return null;
    }

    private String withPostfix(final String name) {
        return name + ',' + tags;
    }

    private String withPostfix(final String name, final String tag) {
        return name + ',' + tags + ',' + tag;
    }

    private String formatNumber(final BigInteger n) {
        return String.valueOf(n);
    }

    private String formatNumber(final long n) {
        return Long.toString(n);
    }

    private String formatNumber(final double v) {
        return String.format(Locale.US, "%2.2f", v);
    }

    @NotThreadSafe
    public static final class Builder {

        private final MetricRegistry registry;
        private String tags;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;

        private Builder(final MetricRegistry registry) {
            this.registry = registry;
            this.tags = "";
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
        }

        public Builder withTag(@Nullable final String tag) {
            this.tags = tags.isEmpty() ? tag : tags + ',' + tag;
            return this;
        }

        public Builder convertRatesTo(final TimeUnit _rateUnit) {
            this.rateUnit = _rateUnit;
            return this;
        }

        public Builder convertDurationsTo(final TimeUnit _durationUnit) {
            this.durationUnit = _durationUnit;
            return this;
        }

        public Builder filter(final MetricFilter _filter) {
            this.filter = _filter;
            return this;
        }

        public StatsDReporter build(final String host, final int port) {
            return build(new StatsD(host, port));
        }

        public StatsDReporter build(final StatsD statsD) {
            return new StatsDReporter(registry, statsD, tags, rateUnit, durationUnit, filter);
        }
    }
}
