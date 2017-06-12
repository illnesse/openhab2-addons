/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.hyperion.internal;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jmdns.ServiceInfo;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.io.transport.mdns.discovery.MDNSDiscoveryParticipant;
import org.openhab.binding.hyperion.HyperionBindingConstants;

/**
 * The {@link HyperionDiscoveryParticipant} class is responsible for listening
 * to MDNS responses and discovering Hyperion.ng servers.
 *
 * @author Daniel Walters - Initial contribution
 */
public class HyperionDiscoveryParticipant implements MDNSDiscoveryParticipant {

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return Collections.singleton(HyperionBindingConstants.THING_TYPE_SERVER_NG);
    }

    @Override
    public String getServiceType() {
        return "_hyperiond-json._tcp.local.";
    }

    @Override
    public DiscoveryResult createResult(ServiceInfo service) {

        // return null if the service info is invalid / not fully formed
        if (service.getHostAddresses().length == 0) {
            return null;
        }

        final Map<String, Object> properties = new HashMap<>(2);
        String host = service.getHostAddresses()[0];
        BigDecimal port = new BigDecimal(service.getPort());

        properties.put(HyperionBindingConstants.HOST, host);
        properties.put(HyperionBindingConstants.PORT, port);

        String longName = service.getName();
        int pos = longName.indexOf("@");
        if (pos < 0 || pos >= longName.length()) {
            return null;
        }

        String friendlyName = longName.substring(0, pos);
        ThingUID uid = getThingUID(service);

        final DiscoveryResult result = DiscoveryResultBuilder.create(uid)
                .withThingType(HyperionBindingConstants.THING_TYPE_SERVER_NG).withProperties(properties)
                .withLabel(friendlyName).build();
        return result;
    }

    @Override
    public ThingUID getThingUID(ServiceInfo service) {

        // TODO: is there a better way to create a unique uid?
        String longName = service.getName();
        int hashCode = longName.hashCode();
        String uid = Integer.toUnsignedString(hashCode);
        return new ThingUID(HyperionBindingConstants.THING_TYPE_SERVER_NG, uid);
    }

}
