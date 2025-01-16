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

