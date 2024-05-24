# SmartContract module

### Example of usage of Highload Wallet V3

```java
HighloadWalletV3 contract = HighloadWalletV3.builder()
        .tonlib(tonlib)
        .walletId(42)
        .build();

String nonBounceableAddress = contract.getAddress().toNonBounceable();
String bounceableAddress = contract.getAddress().toBounceable();
String rawAddress = contract.getAddress().toRaw();

log.info("non-bounceable address {}", nonBounceableAddress);
log.info("    bounceable address {}", bounceableAddress);
log.info("           raw address {}", rawAddress);
log.info("pub-key {}", Utils.bytesToHex(contract.getKeyPair().getPublicKey()));
log.info("prv-key {}", Utils.bytesToHex(contract.getKeyPair().getSecretKey()));

// top up new wallet using test-faucet-wallet
BigInteger balance = TestFaucet.topUpContract(tonlib, Address.of(nonBounceableAddress), Utils.toNano(12));
Utils.sleep(30, "topping up...");
log.info("new wallet {} balance: {}", contract.getName(), Utils.formatNanoValue(balance));

HighloadV3Config config = HighloadV3Config.builder()
        .walletId(42)
        .queryId(HighloadQueryId.fromSeqno(0).getQueryId())
        .build();

ExtMessageInfo extMessageInfo = contract.deploy(config);
assertThat(extMessageInfo.getError().getCode()).isZero();

contract.waitForDeployment(45);

config = HighloadV3Config.builder()
        .walletId(42)
        .queryId(HighloadQueryId.fromSeqno(1).getQueryId())
        .body(contract.createBulkTransfer(
                createDummyDestinations(300),
                BigInteger.valueOf(HighloadQueryId.fromSeqno(1).getQueryId())))
        .build();

extMessageInfo = contract.sendTonCoins(config);
assertThat(extMessageInfo.getError().getCode()).isZero();
log.info("sent 1000 messages");

// help method
List<Destination> createDummyDestinations(int count) throws NoSuchAlgorithmException {
    List<Destination> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
        String dstDummyAddress = "0:" + Utils.bytesToHex(MessageDigest.getInstance("SHA-256").digest(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));

        result.add(Destination.builder()
                .bounce(false)
                .address(dstDummyAddress)
                .amount(Utils.toNano(0.01))
//              .comment("comment-" + i)
                .build());
    }
    return result;
}
```

![Class Diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/neodix42/ton4j/highload-v3-tests/smartcontract/highload-v3.puml)

More examples on how to work with [smart-contracts](../smartcontract/src/main/java/org/ton/java/smartcontract) can be
found [here](../smartcontract/src/test/java/org/ton/java/smartcontract).