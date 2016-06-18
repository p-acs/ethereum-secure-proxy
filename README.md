# Overview

Ethereum secure proxy is a tool to secure your Ethereum node easily.

It proxies the calls to the JSON RPC while exposing an HTTP endpoint which offers end to end encryption.

Additionally the proxy filters any JSON RPC call which could be harmful.

It offers 3 endpoints:

+ __/[ROOT]__ renders a HTML page containing the fingerprint of the certificate so that clients can do certificate pinning by scanning the QR code or by comparing the displayed characters
+ __/identity__ offers the PEM formatted certificate the client should download to compute the certificate fingerprint, encrypt messages to the proxy and verify the received messages 
+ __/secure__  receives encrypted requests which are then forwarded to the Ethereum node and send back signed and encrypted to the client


#Usage

##Docker


###When your node is not running in docker

    docker run  -p 80:8080 -d pacs/ethereum-secure-proxy:1.0.0 --url http://ETHEREUM-NODE-IP:PORT


###When your node is running in docker

This assumes that you named the container exposing the RPC endpoint "ethereum-node"


    docker run -p 80:8080 --link ethereum-node:ethereum-node  -d pacs/ethereum-secure-proxy:1.0.0 --url http://ethereum-node:PORT

###Full Example

This example uses [Geth](https://hub.docker.com/r/ethereum/client-go/) and shows how to setup the proxy for productive environments including backup and migration to other hosts.


#### Create a volume which will hold the container certificate

    docker volume create --name ethereum-proxy-key

#### Run the Ethereum Node

Make sure to only expose the network listening port to the host and not the RPC port
   
    docker run -d -p 30303:30303 --name ethereum-node ethereum/client-go --rpc --rpcaddr "0.0.0.0"
   
#### Run the proxy only to generate the container certificate

Dont' forget to add "--rm" which will remove the container automatically as soon as it exists
   
    docker run --rm -it --link ethereum-node:ethereum-node -v ethereum-proxy-key:/root/seccoco-secured pacs/ethereum-secure-proxy:1.0.0 --url http://ethereum-node:8545

When you see this line __Application password:__ write down the password and kill the container (e.g. by pressing Ctrl+C)
   
#### Backup the container certificate

In order to restart the container or migrate the container to a different host, backup the certificate.
  
    docker run --rm -v ethereum-proxy-key:/data -v $(pwd):/backup busybox tar cvf /backup/ethereum-secure-proxy-backup.tar /data
  
Store the resulting file ___ethereum-secure-proxy-backup.tar___ at a safe place.
You don't need to encrypt it, but make sure that you don't store the container password together with it!
  
#### Restore the container certificate

Hint: to make sure that at no point in time the password can leak you should do all the steps above at an offline host and only copy the file ___ethereum-secure-proxy-backup.tar___ to the host where your node runs.

  Delete the previously created volume (only needed if you do the steps on the same host)
  
    docker volume rm ethereum-proxy-key
  
  Create a fresh container
  
    docker volume create --name ethereum-proxy-key
  
  Restore the backup to the new volume (if you do this from a different host make sure that the file is in the current directory)
  
    docker run --rm -v ethereum-proxy-key:/data -v $(pwd):/backup busybox tar xvf /backup/ethereum-secure-proxy-backup.tar data/


### Run the proxy

    docker run -it --link ethereum-node:ethereum-node -v ethereum-proxy-key:/root/seccoco-secured pacs/ethereum-secure-proxy:1.0.0 --url http://ethereum-node:8545

This line will appear: "[Enter application password:]"
Paste the password you wrote down before.
Send the container in background (e.g. by pressing the sequence Ctrl+P,Ctrl+Q)

Done.

##No Docker

###Make sure your Java version is compliant

You need at least Java 7.

If you use Oracle Java, it needs to have the __Unlimited Strength Jurisdiction Policy__ installed.


###Get the binary
 
Download it [here](https://github.com/p-acs/ethereum-secure-proxy/releases)   or build it on your own ( __mvn package__ )

###Run the proxy

    java -jar ethereum-secure-proxy-VERSION.jar --url http://ETHEREUM-NODE-IP:PORT

This generates the certificate in the directory ___seccoco-secured___ located under your homedirectory. 

Check the log output after __Using workingdirectory__ for the exact path to it. 

Make sure to backup this directory and note down the password, which was printed after __Application password:__ 



Contact us for questions: [info@p-acs.com](mailto:info@p-acs.com)
