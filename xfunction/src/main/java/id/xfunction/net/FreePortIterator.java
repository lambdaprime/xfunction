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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.StandardProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.util.Iterator;

public class FreePortIterator implements Iterator<Integer> {

    public enum Protocol {
        TCP,
        UDP
    };

    private int currentPort;
    private Protocol proto;

    public FreePortIterator(int startPort, Protocol proto) {

        this.currentPort = startPort;
        this.proto = proto;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Integer next() {
        while (true) {
            if (proto == Protocol.UDP) {
                try (var ss =
                        DatagramChannel.open(StandardProtocolFamily.INET)
                                .bind(new InetSocketAddress(currentPort))) {
                    return currentPort++;
                } catch (IOException e) {
                }
            } else {
                try (var ss = new ServerSocket(currentPort)) {
                    return currentPort++;
                } catch (IOException e) {
                }
            }
            currentPort++;
        }
    }
}
