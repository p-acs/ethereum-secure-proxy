package de.petendi.ethereum.secure.proxy.controller;

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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import de.petendi.ethereum.secure.proxy.model.AllowedCommand;
import de.petendi.ethereum.secure.proxy.model.WrappedRequest;
import de.petendi.ethereum.secure.proxy.model.WrappedResponse;
import de.petendi.seccoco.IO;
import de.petendi.seccoco.Seccoco;
import de.petendi.seccoco.model.EncryptedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/secure")
public class SecureController {

    private Seccoco seccoco;
    private JsonRpcHttpClient rpcClient;

    @Autowired
    public SecureController(Seccoco seccoco, JsonRpcHttpClient rpcClient) {
        this.seccoco = seccoco;
        this.rpcClient = rpcClient;
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/{fingerprint}")
    public ResponseEntity<EncryptedMessage> post(@PathVariable("fingerprint") String fingerPrint, @RequestBody EncryptedMessage encryptedMessage) {

        IO.UnencryptedResponse unencryptedResponse = new IO.UnencryptedResponse() {
            @Override
            public byte[] getUnencryptedResponse(byte[] bytes, String s, String s1) {
                return SecureController.this.dispatch(bytes).getBytes();
            }
        };
        try {
            EncryptedMessage encrypted = seccoco.io().dispatch(fingerPrint, encryptedMessage, unencryptedResponse);
            return new ResponseEntity<EncryptedMessage>(encrypted, HttpStatus.OK);
        } catch (IO.RequestException e) {
            HttpStatus status;
            if (e instanceof IO.CertificateNotFoundException) {
                status = HttpStatus.FORBIDDEN;
            } else if (e instanceof IO.SignatureCheckFailedException) {
                status = HttpStatus.UNAUTHORIZED;
            } else if (e instanceof IO.InvalidInputException) {
                status = HttpStatus.BAD_REQUEST;
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            return new ResponseEntity<EncryptedMessage>(status);
        }
    }

    private String dispatch(byte[] bytes) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        WrappedResponse wrappedResponse = new WrappedResponse();
        try {
            WrappedRequest wrappedRequest = objectMapper.readValue(bytes, WrappedRequest.class);
            AllowedCommand allowedCommand = AllowedCommand.valueOf(wrappedRequest.getCommand());
            Object response = rpcClient.invoke(allowedCommand.toString(), wrappedRequest.getParameters(), Object.class);
            wrappedResponse.setResponse(response);
            wrappedResponse.setSuccess(true);
        } catch (Throwable e) {
            wrappedResponse.setSuccess(false);
            wrappedResponse.setErrorMessage(e.getMessage());
        }
        try {
            return objectMapper.writeValueAsString(wrappedResponse);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
