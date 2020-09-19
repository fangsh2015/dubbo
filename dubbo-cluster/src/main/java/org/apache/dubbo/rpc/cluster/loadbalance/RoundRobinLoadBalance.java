/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.cluster.loadbalance;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Round robin load balance.
 * 轮训负载均衡
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {
    public static final String NAME = "roundrobin";

    /**
     * 刷新的周期 ms
     * 避免有服务挂掉，重新计算权重
     */
    private static final int RECYCLE_PERIOD = 60000;

    protected static class WeightedRoundRobin {
        /**
         * 服务的权重
         */
        private int weight;
        /**
         * 当前权重
         */
        private AtomicLong current = new AtomicLong(0);
        /**
         * 最后更新时间
         */
        private long lastUpdate;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
            current.set(0);
        }

        /**
         * 当提供者被选择时，当前权重值加上服务权重
         * @return
         */
        public long increaseCurrent() {
            return current.addAndGet(weight);
        }

        /**
         * 当提供者被选中时，当前权重值减去所有的权重
         * @param total
         */
        public void sel(int total) {
            current.addAndGet(-1 * total);
        }

        public long getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
    }

    private ConcurrentMap<String, ConcurrentMap<String, WeightedRoundRobin>> methodWeightMap = new ConcurrentHashMap<String, ConcurrentMap<String, WeightedRoundRobin>>();

    /**
     * get invoker addr list cached for specified invocation
     * <p>
     * <b>for unit test only</b>
     *
     * @param invokers
     * @param invocation
     * @return
     */
    protected <T> Collection<String> getInvokerAddrList(List<Invoker<T>> invokers, Invocation invocation) {
        String key = invokers.get(0).getUrl().getServiceKey() + "." + invocation.getMethodName();
        Map<String, WeightedRoundRobin> map = methodWeightMap.get(key);
        if (map != null) {
            return map.keySet();
        }
        return null;
    }

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        String key = invokers.get(0).getUrl().getServiceKey() + "." + invocation.getMethodName();
        // 服务总权重缓存
        ConcurrentMap<String, WeightedRoundRobin> map = methodWeightMap.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
        // 服务总权重
        int totalWeight = 0;
        // 当前最大权重（long的最小值）
        long maxCurrent = Long.MIN_VALUE;
        // 当前时间
        long now = System.currentTimeMillis();
        // 选中的服务
        Invoker<T> selectedInvoker = null;
        // 选中服务的负载统计信息
        WeightedRoundRobin selectedWRR = null;
        for (Invoker<T> invoker : invokers) {
            String identifyString = invoker.getUrl().toIdentityString();
            // 计算服务的权重
            int weight = getWeight(invoker, invocation);
            // 获取缓存中的服务权重信息，如果缓存为空，则从新设置
            WeightedRoundRobin weightedRoundRobin = map.computeIfAbsent(identifyString, k -> {
                WeightedRoundRobin wrr = new WeightedRoundRobin();
                wrr.setWeight(weight);
                return wrr;
            });

            // 缓存中的权重与当前计算的权重不同，则更新缓存中的服务权重
            if (weight != weightedRoundRobin.getWeight()) {
                //weight changed
                weightedRoundRobin.setWeight(weight);
            }
            long cur = weightedRoundRobin.increaseCurrent();
            weightedRoundRobin.setLastUpdate(now);
            // 服务当前权重大于最大权重，则更新最大权重，并且选中当前的服务
            if (cur > maxCurrent) {
                maxCurrent = cur;
                selectedInvoker = invoker;
                selectedWRR = weightedRoundRobin;
            }
            // 总权重相加
            totalWeight += weight;
        }
        // 更新服务权重缓存（有服务挂了， 或者新添加了服务）
        if (invokers.size() != map.size()) {
            map.entrySet().removeIf(item -> now - item.getValue().getLastUpdate() > RECYCLE_PERIOD);
        }
        // 如果已经选中了服务， 则将权重的服务的权重-总权重
        // 采用这种方式，在兼顾轮训的同时，还考虑到了不同服务的权重的问题，权重越大，则其轮训的概率也就相应的增加了
        if (selectedInvoker != null) {
            selectedWRR.sel(totalWeight);
            return selectedInvoker;
        }
        // should not happen here
        // 没有选中服务，则返回服务列表中的第一个服务
        return invokers.get(0);
    }

}
