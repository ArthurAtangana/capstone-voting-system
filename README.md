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

- In a directory at the same level of this project, clone the capstone hyperledger repository and follow the instructions from that repostitory.

#### Directory structure
|- capstone-voting-system
    |- src
    |- README.md
    |- fabric-start.sh
    |- ...
|- hyperledger
    |- asset-transfer-basic
    |- test-network
    |- README.md
    |- install-fabric.sh

### running hyperledger

- in this project, run the fabric-start.sh script which should bring up the hyperledger network  with 2 peers and deploy the chaincode to it.

