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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.util.Iterator;
import java.util.Optional;

/** Iterates over free UDP ports available in the system. */
public class FreeUdpPortIterator implements Iterator<DatagramChannel> {

    private int currentPort;
    private Optional<InetAddress> address = Optional.empty();

    public FreeUdpPortIterator(int startPort) {
        this(startPort, Optional.empty());
    }

    public FreeUdpPortIterator(int startPort, Optional<InetAddress> address) {
        this.currentPort = startPort;
        this.address = address;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    /**
     * Find next port which is available on all system network interfaces and return a {@link
     * DatagramChannel} attached to it.
     */
    @Override
    public DatagramChannel next() {
        while (true) {
            try {
                var dataChannel = DatagramChannel.open(StandardProtocolFamily.INET);
                if (address.isPresent())
                    dataChannel.bind(new InetSocketAddress(address.get(), currentPort));
                else dataChannel.bind(new InetSocketAddress(currentPort));
                currentPort++;
                return dataChannel;
            } catch (IOException e) {
            }
            currentPort++;
        }
    }
}
