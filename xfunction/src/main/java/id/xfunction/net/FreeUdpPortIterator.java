/*
 * Copyright 2022 lambdaprime
 * 
 * Website: https://github.com/lambdaprime/xfunction
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.xfunction.net;

import id.xfunction.function.ThrowingConsumer;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.util.Iterator;
import java.util.Optional;

/** Iterates over free UDP ports available in the system and binds to first found. */
public class FreeUdpPortIterator implements Iterator<DatagramChannel> {

    private int currentPort;
    private Optional<InetAddress> address = Optional.empty();
    private Optional<ThrowingConsumer<DatagramSocket, IOException>> socketConfigurator =
            Optional.empty();

    public FreeUdpPortIterator(int startPort) {
        currentPort = startPort;
    }

    /**
     * Searches for ports available only on a particular network interface
     *
     * @param address address assigned to the network interface over which ports to iterate
     */
    public FreeUdpPortIterator withNetworkInterfaceAddress(InetAddress address) {
        this.address = Optional.of(address);
        return this;
    }

    /**
     * After port is found it will be binded already by the {@link FreeUdpPortIterator} so that no
     * other component will steal it. It creates a problem that socket options which can be changed
     * only before socket is connected are not available anymore.
     *
     * <p>When socket configurator is set it is called before socket bind. It allows users to set
     * socket options as required.
     */
    public FreeUdpPortIterator withSocketConfigurator(
            ThrowingConsumer<DatagramSocket, IOException> socketConfigurator) {
        this.socketConfigurator = Optional.of(socketConfigurator);
        return this;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    /**
     * Find next port which is available and bind to it.
     *
     * @return {@link DatagramChannel} which is attached to found port.
     */
    @Override
    public DatagramChannel next() {
        while (true) {
            try {
                DatagramChannel dataChannel = DatagramChannel.open(StandardProtocolFamily.INET);
                if (socketConfigurator.isPresent())
                    socketConfigurator.get().accept(dataChannel.socket());
                if (address.isPresent())
                    dataChannel.bind(new InetSocketAddress(address.get(), currentPort));
                else dataChannel.bind(new InetSocketAddress(currentPort));
                currentPort++;
                return dataChannel;
            } catch (IOException e) {
                // port is not available, try next one
            }
            currentPort++;
        }
    }
}
