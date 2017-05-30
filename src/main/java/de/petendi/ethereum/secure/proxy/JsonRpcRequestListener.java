package de.petendi.ethereum.secure.proxy;

/*-
 * #%L
 * Ethereum Secure Proxy
 * %%
 * Copyright (C) 2016 P-ACS UG (haftungsbeschränkt)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.JsonRpcClient;

class JsonRpcRequestListener implements JsonRpcClient.RequestListener {

    private static final String ID = "id";

    @Override
    public void onBeforeRequestSent(JsonRpcClient jsonRpcClient, ObjectNode objectNode) {
        int id = Integer.valueOf(objectNode.get(ID).asText());
        objectNode.remove(ID);
        objectNode.put(ID, id);
    }

    @Override
    public void onBeforeResponseProcessed(JsonRpcClient jsonRpcClient, ObjectNode objectNode) {
        //ignore
    }
}
