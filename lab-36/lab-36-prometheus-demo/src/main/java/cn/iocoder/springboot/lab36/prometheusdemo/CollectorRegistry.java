//package cn.iocoder.springboot.lab36.prometheusdemo;
//
//import io.prometheus.client.Collector;
//import io.prometheus.client.Collector.Describable;
//import io.prometheus.client.Collector.MetricFamilySamples;
//import io.prometheus.client.Collector.MetricFamilySamples.Sample;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.NoSuchElementException;
//import java.util.Set;
//
//public class CollectorRegistry {
//    public static final io.prometheus.client.CollectorRegistry defaultRegistry = new io.prometheus.client.CollectorRegistry(true);
//    private final Object namesCollectorsLock;
//    private final Map<Collector, List<String>> collectorsToNames;
//    private final Map<String, Collector> namesToCollectors;
//    private final boolean autoDescribe;
//
//    public CollectorRegistry() {
//        this(false);
//    }
//
//    public CollectorRegistry(boolean autoDescribe) {
//        this.namesCollectorsLock = new Object();
//        this.collectorsToNames = new HashMap();
//        this.namesToCollectors = new HashMap();
//        this.autoDescribe = autoDescribe;
//    }
//
//    public void register(Collector m) {
//        List<String> names = this.collectorNames(m);
//        synchronized(this.namesCollectorsLock) {
//            Iterator var4 = names.iterator();
//
//            String name;
//            while(var4.hasNext()) {
//                name = (String)var4.next();
//                if (this.namesToCollectors.containsKey(name)) {
//                    throw new IllegalArgumentException("Collector already registered that provides name: " + name);
//                }
//            }
//
//            var4 = names.iterator();
//
//            while(var4.hasNext()) {
//                name = (String)var4.next();
//                this.namesToCollectors.put(name, m);
//            }
//
//            this.collectorsToNames.put(m, names);
//        }
//    }
//
//    public void unregister(Collector m) {
//        synchronized(this.namesCollectorsLock) {
//            List<String> names = (List)this.collectorsToNames.remove(m);
//            Iterator var4 = names.iterator();
//
//            while(var4.hasNext()) {
//                String name = (String)var4.next();
//                this.namesToCollectors.remove(name);
//            }
//
//        }
//    }
//
//    public void clear() {
//        synchronized(this.namesCollectorsLock) {
//            this.collectorsToNames.clear();
//            this.namesToCollectors.clear();
//        }
//    }
//
//    private Set<Collector> collectors() {
//        synchronized(this.namesCollectorsLock) {
//            return new HashSet(this.collectorsToNames.keySet());
//        }
//    }
//
//    private List<String> collectorNames(Collector m) {
//        List mfs;
//        if (m instanceof Describable) {
//            mfs = ((Describable)m).describe();
//        } else if (this.autoDescribe) {
//            mfs = m.collect();
//        } else {
//            mfs = Collections.emptyList();
//        }
//
//        List<String> names = new ArrayList();
//        Iterator var4 = mfs.iterator();
//
//        while(var4.hasNext()) {
//            MetricFamilySamples family = (MetricFamilySamples)var4.next();
//            switch(family.type) {
//                case SUMMARY:
//                    names.add(family.name + "_count");
//                    names.add(family.name + "_sum");
//                    names.add(family.name);
//                    break;
//                case HISTOGRAM:
//                    names.add(family.name + "_count");
//                    names.add(family.name + "_sum");
//                    names.add(family.name + "_bucket");
//                    names.add(family.name);
//                    break;
//                default:
//                    names.add(family.name);
//            }
//        }
//
//        return names;
//    }
//
//    public Enumeration<MetricFamilySamples> metricFamilySamples() {
//        return new MetricFamilySamplesEnumeration();
//    }
//
//    public Enumeration<MetricFamilySamples> filteredMetricFamilySamples(Set<String> includedNames) {
//        return new io.prometheus.client.CollectorRegistry.MetricFamilySamplesEnumeration(includedNames);
//    }
//
//    public Double getSampleValue(String name) {
//        return this.getSampleValue(name, new String[0], new String[0]);
//    }
//
//    public Double getSampleValue(String name, String[] labelNames, String[] labelValues) {
//        Iterator var4 = Collections.list(this.metricFamilySamples()).iterator();
//
//        while(var4.hasNext()) {
//            MetricFamilySamples metricFamilySamples = (MetricFamilySamples)var4.next();
//            Iterator var6 = metricFamilySamples.samples.iterator();
//
//            while(var6.hasNext()) {
//                Sample sample = (Sample)var6.next();
//                if (sample.name.equals(name) && Arrays.equals(sample.labelNames.toArray(), labelNames) && Arrays.equals(sample.labelValues.toArray(), labelValues)) {
//                    return sample.value;
//                }
//            }
//        }
//
//        return null;
//    }
//
//    class MetricFamilySamplesEnumeration implements Enumeration<MetricFamilySamples> {
//        private final Iterator<Collector> collectorIter;
//        private Iterator<MetricFamilySamples> metricFamilySamples;
//        private MetricFamilySamples next;
//        private Set<String> includedNames;
//
//        MetricFamilySamplesEnumeration(Set<String> includedNames) {
//            this.includedNames = includedNames;
//            this.collectorIter = this.includedCollectorIterator(includedNames);
//            this.findNextElement();
//        }
//
//        private Iterator<Collector> includedCollectorIterator(Set<String> includedNames) {
//            if (includedNames.isEmpty()) {
//                return CollectorRegistry.this.collectors().iterator();
//            } else {
//                HashSet<Collector> collectors = new HashSet();
//                synchronized(CollectorRegistry.this.namesCollectorsLock) {
//                    Iterator var4 = CollectorRegistry.this.namesToCollectors.entrySet().iterator();
//
//                    while(var4.hasNext()) {
//                        Entry<String, Collector> entry = (Entry)var4.next();
//                        if (includedNames.contains(entry.getKey())) {
//                            collectors.add(entry.getValue());
//                        }
//                    }
//
//                    return collectors.iterator();
//                }
//            }
//        }
//
//        MetricFamilySamplesEnumeration() {
//            this(Collections.emptySet());
//        }
//
//        private void findNextElement() {
//            this.next = null;
//
//            while(this.metricFamilySamples != null && this.metricFamilySamples.hasNext()) {
//                this.next = this.filter((MetricFamilySamples)this.metricFamilySamples.next());
//                if (this.next != null) {
//                    return;
//                }
//            }
//
//            if (this.next == null) {
//                while(this.collectorIter.hasNext()) {
//                    this.metricFamilySamples = ((Collector)this.collectorIter.next()).collect().iterator();
//
//                    while(this.metricFamilySamples.hasNext()) {
//                        this.next = this.filter((MetricFamilySamples)this.metricFamilySamples.next());
//                        if (this.next != null) {
//                            return;
//                        }
//                    }
//                }
//            }
//
//        }
//
//        private MetricFamilySamples filter(MetricFamilySamples next) {
//            if (this.includedNames.isEmpty()) {
//                return next;
//            } else {
//                Iterator it = next.samples.iterator();
//
//                while(it.hasNext()) {
//                    if (!this.includedNames.contains(((Sample)it.next()).name)) {
//                        it.remove();
//                    }
//                }
//
//                if (next.samples.size() == 0) {
//                    return null;
//                } else {
//                    return next;
//                }
//            }
//        }
//
//        public MetricFamilySamples nextElement() {
//            MetricFamilySamples current = this.next;
//            if (current == null) {
//                throw new NoSuchElementException();
//            } else {
//                this.findNextElement();
//                return current;
//            }
//        }
//
//        public boolean hasMoreElements() {
//            return this.next != null;
//        }
//    }
//}
//
