package io.sugo.pio.curator.discovery;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.sugo.pio.server.PioNode;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Map;

/**
 * Uses the Curator Service Discovery recipe to announce services.
 */
public class CuratorServiceAnnouncer implements ServiceAnnouncer
{
    private final ServiceDiscovery<Void> discovery;
    private final Map<String, ServiceInstance<Void>> instanceMap = Maps.newHashMap();
    private final Object monitor = new Object();

    @Inject
    public CuratorServiceAnnouncer(
            ServiceDiscovery<Void> discovery
    )
    {
        this.discovery = discovery;
    }

    @Override
    public void announce(PioNode service)
    {
        final String serviceName = CuratorServiceUtils.makeCanonicalServiceName(service.getServiceName());

        final ServiceInstance<Void> instance;
        synchronized (monitor) {
            if (instanceMap.containsKey(serviceName)) {
                return;
            } else {
                try {
                    instance = ServiceInstance.<Void>builder()
                            .name(serviceName)
                            .address(service.getHost())
                            .port(service.getPort())
                            .build();
                }
                catch (Exception e) {
                    throw Throwables.propagate(e);
                }

                instanceMap.put(serviceName, instance);
            }
        }

        try {
            discovery.registerService(instance);
        }
        catch (Exception e) {
            synchronized (monitor) {
                instanceMap.remove(serviceName);
            }
        }
    }

    @Override
    public void unannounce(PioNode service)
    {
        final String serviceName = CuratorServiceUtils.makeCanonicalServiceName(service.getServiceName());
        final ServiceInstance<Void> instance;

        synchronized (monitor) {
            instance = instanceMap.get(serviceName);
            if (instance == null) {
                return;
            }
        }

        try {
            discovery.unregisterService(instance);
        }
        catch (Exception e) {
        }
        finally {
            synchronized (monitor) {
                instanceMap.remove(serviceName);
            }
        }
    }
}
