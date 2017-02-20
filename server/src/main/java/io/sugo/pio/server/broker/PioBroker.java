/*
 * Licensed to Metamarkets Group Inc. (Metamarkets) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Metamarkets licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.sugo.pio.server.broker;

import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import io.sugo.pio.client.FilteredServerInventoryView;
import io.sugo.pio.curator.discovery.ServiceAnnouncer;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.guice.Self;
import io.sugo.pio.server.PioNode;

@ManageLifecycle
public class PioBroker {
    private final PioNode self;
    private final ServiceAnnouncer serviceAnnouncer;
    private volatile boolean started = false;

    @Inject
    public PioBroker(
            final FilteredServerInventoryView serverInventoryView,
            final @Self PioNode self,
            final ServiceAnnouncer serviceAnnouncer
    ) {
        this.self = self;
        this.serviceAnnouncer = serviceAnnouncer;
    }

    @LifecycleStart
    public void start() {
        synchronized (self) {
            if (started) {
                return;
            }
            serviceAnnouncer.announce(self);
            started = true;
        }
    }

    @LifecycleStop
    public void stop() {
        synchronized (self) {
            if (!started) {
                return;
            }
            serviceAnnouncer.unannounce(self);
            started = false;
        }
    }
}