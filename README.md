## Secure and Transparent Voting System


## Members

- Arthur Atangana
- Jazmine Gad El Hak
- Michael De Santis
- Rebecca Elliott
- Victoria Malouf

## Hyperledger Fabric

### Prerequisites:
- git
- curl
- docker and docker compose
- go
- jq

### Installation:

- In a directory outside of the project curl the installation script:

```bash
curl -sSLO https://raw.githubusercontent.com/hyperledger/fabric/main/scripts/install-fabric.sh && chmod +x install-fabric.sh
```
- Run the installation script:
```bash
./install-fabric --fabric-version 2.5.9 --ca-version 1.5.12 docker samples binaries
```
