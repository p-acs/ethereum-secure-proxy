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


import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import de.petendi.seccoco.InitializationException;
import de.petendi.seccoco.Seccoco;
import de.petendi.seccoco.SeccocoFactory;
import de.petendi.seccoco.argument.ArgumentList;
import org.kohsuke.args4j.CmdLineException;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.Console;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;


@Configuration
@ComponentScan({"de.petendi.ethereum.secure.proxy.controller"})
@EnableAutoConfiguration
@EnableScheduling
public class Application {

    private static Seccoco seccoco = null;
    private static JsonRpcHttpClient jsonRpcHttpClient = null;
    private static CmdLineResult cmdLineResult;

    public static void main(String[] args) {

        try {
            cmdLineResult = new CmdLineResult();
            cmdLineResult.parseArguments(args);
        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            System.out.println("Usage:");
            cmdLineResult.printExample();
            System.exit(-1);
            return;
        }

        File workingDirectory = new File(System.getProperty("user.home"));
        if (cmdLineResult.getWorkingDirectory().length() > 0) {
            workingDirectory = new File(cmdLineResult.getWorkingDirectory()).getAbsoluteFile();
        }

        ArgumentList argumentList = new ArgumentList();
        argumentList.setWorkingDirectory(workingDirectory);
        File certificate = new File(workingDirectory, "seccoco-secured/cert.p12");
        if (certificate.exists()) {
            char[] passwd = null;
            if (cmdLineResult.getPassword() != null) {
                System.out.println("Using password from commandline argument - DON'T DO THIS IN PRODUCTION !!");
                passwd = cmdLineResult.getPassword().toCharArray();
            } else {
                Console console = System.console();
                if (console != null) {
                    passwd = console.readPassword("[%s]", "Enter application password:");
                } else {
                    System.out.print("No suitable console found for entering password");
                    System.exit(-1);
                }
            }
            argumentList.setTokenPassword(passwd);
        }
        try {
            SeccocoFactory seccocoFactory = new SeccocoFactory("seccoco-secured", argumentList);
            seccoco = seccocoFactory.create();
        } catch (InitializationException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        try {
            System.out.println("Connecting to Ethereum RPC at " + cmdLineResult.getUrl());
            URL url = new URL(cmdLineResult.getUrl());
            jsonRpcHttpClient = new JsonRpcHttpClient(url);
            HashMap<String,String> header = new HashMap<String,String>();
            header.put("Content-Type","application/json");
            jsonRpcHttpClient.invoke("eth_protocolVersion", null,Object.class,header);
            System.out.println("Connection succeeded");
        } catch (Throwable e) {
            System.out.println("Connection failed: " + e.getMessage());
            System.exit(-1);
        }
        SpringApplication app = new SpringApplication(Application.class);
        app.setBanner(new Banner() {
            @Override
            public void printBanner(Environment environment, Class<?> aClass, PrintStream printStream) {
                //send the Spring Boot banner to /dev/null
            }
        });
        app.run(new String[]{});
    }


    @Bean
    public Seccoco seccoco() {
        return seccoco;
    }


    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                container.setPort(cmdLineResult.getPort());
            }
        };
    }

    @Bean
    public JsonRpcHttpClient rpcClient() throws Throwable {
        return jsonRpcHttpClient;
    }
}
