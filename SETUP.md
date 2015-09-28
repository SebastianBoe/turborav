Instructions on environment setup and configurations.

# Installing sources

For your convenience turborav can be compiled and run using a Docker image that contains the necessary sources and binaries. For more info on Docker, see the [docker userguide](http://docs.docker.com/userguide/).

At the time of writing the docker image is hosted on Sebastian's local server, making access to Sebastian's local network a necessity.

To obtain and run the image follow the steps below.

Install Docker using the package manager of your choice 
```
yaourt -S docker
sudo apt-get install docker
```
Add the server certificate to your trusted Docker sources
```
sudo mkdir -p /etc/docker/certs.d/192.168.1.7:5000/
sudo cp path_to_repository/cert/turborav.crt /etc/docker/certs.d/192.168.1.7:5000/
```
Grab the turborav Docker image
```
docker pull 192.168.1.7:5000/turborav
```
Run the turborav Docker image
```
docker run turborav
```

# Docker registry server setup reference

Added as reference should it be necessary to set up a registry server again.

## Using self-signed certificate [(source)](http://docs.docker.com/registry/insecure/#using-self-signed-certificates)

#### Certificate

Configure certificate target IP [(source)](http://serverfault.com/questions/611120/failed-tls-handshake-does-not-contain-any-ip-sans)
Edit the file /etc/ssl/openssl.cnf on the registry server, and under [v3_ca] section add
```
subjectAltName = IP:192.168.1.7
```

Generate certificate [(source)](https://docs.docker.com/registry/insecure/#using-self-signed-certificates)
```
cd ~ && mkdir certs
openssl req -newkey rsa:4096 -nodes -sha256 -keyout certs/turborav.key -x509 -days 365 -out certs/turborav.crt
```
Update contents of path_to_repository/cert/ using the newly produced .crt file; turborav.crt & ca.pem.


#### Launch and load registry server [(source)](https://docs.docker.com/registry/deploying/#running-a-domain-registry)

On server
```
docker run -d -p 5000:5000 --restart=always --name registry -v `pwd`/certs:/certs -e REGISTRY_HTTP_TLS_CERTIFICATE=/certs/turborav.crt -e REGISTRY_HTTP_TLS_KEY=/certs/turborav.key registry:2
```
On client with turborav Docker image
```
docker tag turborav 192.168.1.7:5000/turborav
docker push 192.168.1.7:5000/turborav
docker pull 192.168.1.7:5000/turborav
```


## Plain HTTP registry [(source)](https://docs.docker.com/registry/insecure/#deploying-a-plain-http-registry)

Edit the file /etc/default/docker and add a line that reads
```
DOCKER_OPTS="--insecure-registry 192.168.1.7:5000"
```
or add that to existing DOCKER_OPTS.

Then restart
```
sudo service docker stop && sudo service docker start
```

## Troubleshooting

##### Timestamp error
```
Error: An error occurred trying to connect: Get https://192.168.1.7:5000/v1.20/version: x509: certificate has expired or is not yet valid
```
Certificate timestamp depends on host time settings. Verify time and date using timedatectl to configure, e.g.
```
timedatectl set-timezone Europe/Oslo
timedatectl set-time "2015-09-28 07:00:00"
```

##### View server logs
Connection attempts can be viewed in the server logs
```
docker logs registry
```

##### Verifying client to server connection
```
cd ~
mkdir .docker
cp path_to_repository/cert/ca.pem ~/.docker
docker --tlsverify  -H tcp://192.168.1.7:5000 version

lynx http://192.168.1.7:5000/
lynx https://192.168.1.7:5000/
```

##### IP SANs error
```
Error: An error occurred trying to connect: Get https://192.168.1.7:5000/v1.20/version: x509: cannot validate certificate for 192.168.1.7 because it doesn't contain any IP SANs
```
See how to configure certificate target IP above.
