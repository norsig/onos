/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.onlab.onos.cli.net;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onlab.onos.net.ConnectPoint;
import org.onlab.onos.net.DeviceId;
import org.onlab.onos.net.PortNumber;
import org.onlab.onos.net.flow.TrafficSelector;
import org.onlab.onos.net.flow.TrafficTreatment;
import org.onlab.onos.net.intent.Intent;
import org.onlab.onos.net.intent.IntentService;
import org.onlab.onos.net.intent.PointToPointIntentWithBandwidthConstraint;
import org.onlab.onos.net.resource.BandwidthResourceRequest;

import static org.onlab.onos.net.DeviceId.deviceId;
import static org.onlab.onos.net.PortNumber.portNumber;
import static org.onlab.onos.net.flow.DefaultTrafficTreatment.builder;

/**
 * Installs point-to-point connectivity intents.
 */
@Command(scope = "onos", name = "add-point-intent-bw",
         description = "Installs point-to-point connectivity intent with bandwidth constraint")
public class AddPointToPointIntentWithBandwidthConstraintCommand extends ConnectivityIntentCommand {

    @Argument(index = 0, name = "ingressDevice",
              description = "Ingress Device/Port Description",
              required = true, multiValued = false)
    String ingressDeviceString = null;

    @Argument(index = 1, name = "egressDevice",
              description = "Egress Device/Port Description",
              required = true, multiValued = false)
    String egressDeviceString = null;

    @Argument(index = 2, name = "bandwidth",
            description = "Bandwidth",
            required = true, multiValued = false)
    String bandwidthString = null;

    @Override
    protected void execute() {
        IntentService service = get(IntentService.class);

        DeviceId ingressDeviceId = deviceId(getDeviceId(ingressDeviceString));
        PortNumber ingressPortNumber = portNumber(getPortNumber(ingressDeviceString));
        ConnectPoint ingress = new ConnectPoint(ingressDeviceId, ingressPortNumber);

        DeviceId egressDeviceId = deviceId(getDeviceId(egressDeviceString));
        PortNumber egressPortNumber = portNumber(getPortNumber(egressDeviceString));
        ConnectPoint egress = new ConnectPoint(egressDeviceId, egressPortNumber);

        long bandwidth = Long.parseLong(bandwidthString);

        TrafficSelector selector = buildTrafficSelector();
        TrafficTreatment treatment = builder().build();

        Intent intent = new PointToPointIntentWithBandwidthConstraint(
                appId(), selector, treatment,
                ingress, egress, new BandwidthResourceRequest(bandwidth));
        service.submit(intent);
    }

    /**
     * Extracts the port number portion of the ConnectPoint.
     *
     * @param deviceString string representing the device/port
     * @return port number as a string, empty string if the port is not found
     */
    private String getPortNumber(String deviceString) {
        int slash = deviceString.indexOf('/');
        if (slash <= 0) {
            return "";
        }
        return deviceString.substring(slash + 1, deviceString.length());
    }

    /**
     * Extracts the device ID portion of the ConnectPoint.
     *
     * @param deviceString string representing the device/port
     * @return device ID string
     */
    private String getDeviceId(String deviceString) {
        int slash = deviceString.indexOf('/');
        if (slash <= 0) {
            return "";
        }
        return deviceString.substring(0, slash);
    }
}
