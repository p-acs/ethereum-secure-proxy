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

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

public class CmdLineResult {

    @Option(name = "--pw", hidden = true)
    private String password = null;

    @Option(name = "--wd", usage = "The working directory", metaVar = "(default user home directory)")
    private String workingDirectory = "";

    @Option(name = "--port", usage = "The port the proxy is listening on", metaVar = "(default 8080)")
    private int port = 8080;

    @Option(name = "--url", required = true, usage = "The url to the Ethereum RPC endpoint", metaVar = "required")
    private String url;

    @Option(name = "--additionalHeaders", required = false, usage = "Comma separated list of HTTP headers to be included", metaVar = "optional")
    private String additionalHeaders;

    @Option(name = "--exposeWhisper", required = false, usage = "Expose whisper (shh) RPC commands", metaVar = "optional")
    private boolean exposeWhisper = false;


    void parseArguments(String[] arguments) throws CmdLineException {
        CmdLineParser parser = new CmdLineParser(this);
        parser.parseArgument(arguments);
    }

    void printExample() {
        CmdLineParser parser = new CmdLineParser(this, ParserProperties.defaults().withShowDefaults(false));
        parser.printUsage(System.out);
    }


    public String getPassword() {
        return password;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public int getPort() {
        return port;
    }

    public String getUrl() {
        return url;
    }

    public String getAdditionalHeaders() {
        return additionalHeaders;
    }

    public boolean isExposeWhisper() {
        return exposeWhisper;
    }
}
