/*****************************************************************************
 * Copyright 2011-2012 INRIA
 * Copyright 2011-2012 Universidade Nova de Lisboa
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
 *****************************************************************************/
package swift.client.proto;

import sys.net.api.rpc.RpcHandle;
import sys.net.api.rpc.RpcHandler;

/**
 * Client request to get the latest known committed versions at the server.
 * 
 * 
 * @author mzawirski
 */
public class LatestKnownClockRequest extends ClientRequest {

    /**
     * Constructor for Kryo serialization.
     */
    LatestKnownClockRequest() {
    }

    public LatestKnownClockRequest(String clientId) {
        super(clientId);
    }

    @Override
    public void deliverTo(RpcHandle conn, RpcHandler handler) {
        ((BaseServer) handler).onReceive(conn, this);
    }
}