## Secure and Transparent Voting System


## Members

- Arthur Atangana
- Jazmine Gad El Hak
- Michael De Santis
- Rebecca Elliott
- Victoria Malouf

## Hyperledger Fabric

Everything needed for the hyperledger fabric network and chaincode can be found in [this repository](https://github.com/ArthurAtangana/hyperledger)

### Prerequisites:
- git
- curl
- docker and docker compose
- go
- jq

### Installation:

- In a directory at the same level of this project, clone the hyperledger repository and follow the instructions from that repostitory.
- To run the app without the network, set the flag "fabric.enabled" to false in application.properties

#### Directory structure
|- capstone-voting-system
    |- src
    |- README.md
    |- ...
|- hyperledger
    |- asset-transfer-basic
    |- test-network
    |- README.md
    |- fabric-start.sh
    |- install-fabric.sh
    |- ...

### Server Configuration
- Make sure that nginx is running with the proper config `nginx.conf`
- Run hyperledger fabric with `fabric-start.sh`.
- Run the spring boot app with the right timezone:
    * no logs
    ```bash
    nohup env JAVA_TOOL_OPTIONS="-Duser.timezone=America/Toronto" mvn spring-boot:run > /dev/null 2>&1 &
    ```
    * logs
    ```bash
    nohup env JAVA_TOOL_OPTIONS="-Duser.timezone=America/Toronto" mvn spring-boot:run > output.log 2>&1
    ```
