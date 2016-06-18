package de.petendi.ethereum.secure.proxy.model;

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

public enum AllowedCommand {
    eth_blockNumber,
    eth_call,
    eth_compileSolidity,
    eth_gasPrice,
    eth_getBalance,
    eth_getBlockByHash,
    eth_getBlockByNumber,
    eth_getBlockTransactionCountByHash,
    eth_getBlockTransactionCountByNumber,
    eth_getCode,
    eth_getCompilers,
    eth_getStorageAt,
    eth_getFilterChanges,
    eth_getFilterLogs,
    eth_getLogs,
    eth_getTransactionByBlockHashAndIndex,
    eth_getTransactionByBlockNumberAndIndex,
    eth_getTransactionByHash,
    eth_getTransactionCount,
    eth_getTransactionReceipt,
    eth_getUncleByBlockHashAndIndex,
    eth_getUncleByBlockNumberAndIndex,
    eth_getUncleCountByBlockHash,
    eth_getUncleCountByBlockNumber,
    eth_getWork,
    eth_estimateGas,
    eth_mining,
    eth_newBlockFilter,
    eth_newPendingTransactionFilter,
    eth_newFilter,
    eth_protocolVersion,
    eth_sendRawTransaction,
    eth_syncing,
    eth_uninstallFilter,
    net_version
}
