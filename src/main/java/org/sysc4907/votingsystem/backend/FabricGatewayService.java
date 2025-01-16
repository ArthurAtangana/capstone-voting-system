package org.sysc4907.votingsystem.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import org.hyperledger.fabric.client.*;

import org.hyperledger.fabric.client.identity.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "fabric.enabled", havingValue = "true")
public class FabricGatewayService {
    private static final String MSP_ID = "Org1MSP";
    private static final String CHANNEL_NAME = "mychannel";
    private static final String CHAINCODE_NAME = "basic";

    private static final Path CRYPTO_PATH = Paths.get("../hyperledger/test-network/organizations/peerOrganizations/org1.example.com");
    private static final Path CERT_DIR_PATH = CRYPTO_PATH.resolve(Paths.get("users/User1@org1.example.com/msp/signcerts"));
    private static final Path KEY_DIR_PATH = CRYPTO_PATH.resolve(Paths.get("users/User1@org1.example.com/msp/keystore"));
    private static final Path TLS_CERT_PATH = CRYPTO_PATH.resolve(Paths.get("peers/peer0.org1.example.com/tls/ca.crt"));

    private static final String PEER_ENDPOINT = "localhost:7051";
    private static final String OVERRIDE_AUTH = "peer0.org1.example.com";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Contract contract;

    public FabricGatewayService() throws Exception {
        initializeGateway();
    }

    private void initializeGateway() throws Exception {
        var channel = newGrpcConnection();

        var builder = Gateway.newInstance()
                .identity(newIdentity())
                .signer(newSigner())
                .hash(Hash.SHA256)
                .connection(channel)
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

        var gateway = builder.connect();
        var network = gateway.getNetwork(CHANNEL_NAME);
        contract = network.getContract(CHAINCODE_NAME);
    }

    private ManagedChannel newGrpcConnection() throws IOException {
        var credentials = TlsChannelCredentials.newBuilder()
                .trustManager(TLS_CERT_PATH.toFile())
                .build();
        return Grpc.newChannelBuilder(PEER_ENDPOINT, credentials)
                .overrideAuthority(OVERRIDE_AUTH)
                .build();
    }

    private Identity newIdentity() throws IOException, CertificateException {
        try (var certReader = Files.newBufferedReader(getFirstFilePath(CERT_DIR_PATH))) {
            var certificate = Identities.readX509Certificate(certReader);
            return new X509Identity(MSP_ID, certificate);
        }
    }

    private Signer newSigner() throws IOException, InvalidKeyException {
        try (var keyReader = Files.newBufferedReader(getFirstFilePath(KEY_DIR_PATH))) {
            var privateKey = Identities.readPrivateKey(keyReader);
            return Signers.newPrivateKeySigner(privateKey);
        }
    }

    private Path getFirstFilePath(Path dirPath) throws IOException {
        try (var keyFiles = Files.list(dirPath)) {
            return keyFiles.findFirst().orElseThrow();
        }
    }

    public String evaluateTransaction(String function, String... args) throws Exception {
        if (args == null || args.length == 0) {
            var result = contract.evaluateTransaction(function);
            return new String(result, StandardCharsets.UTF_8);
        }
        var result = contract.evaluateTransaction(function, args);
        return new String(result, StandardCharsets.UTF_8);
    }

    public String submitTransaction(String function, String... args) throws Exception {
        if (args == null || args.length == 0) {
            var result = contract.submitTransaction(function, args);
            return new String(result, StandardCharsets.UTF_8);
        }
        var result = contract.submitTransaction(function, args);
        return new String(result, StandardCharsets.UTF_8);
    }
}