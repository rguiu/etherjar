package org.ethereumclassic.etherjar.rpc

import org.ethereumclassic.etherjar.model.Address
import org.ethereumclassic.etherjar.model.BlockHash
import org.ethereumclassic.etherjar.model.Hex32
import org.ethereumclassic.etherjar.model.HexData
import org.ethereumclassic.etherjar.model.Nonce
import org.ethereumclassic.etherjar.model.TransactionId
import org.ethereumclassic.etherjar.rpc.json.BlockJson
import org.ethereumclassic.etherjar.rpc.json.BlockTag
import org.ethereumclassic.etherjar.rpc.json.TraceItemJson
import org.ethereumclassic.etherjar.rpc.json.TransactionJson
import org.ethereumclassic.etherjar.rpc.json.TransactionReceiptJson
import org.ethereumclassic.etherjar.rpc.transport.RpcTransport
import spock.lang.Specification

import static org.ethereumclassic.etherjar.rpc.ConcurrencyUtils.*

/**
 *
 * @author Igor Artamonov
 */
class DefaultRpcClientSpec extends Specification {

    DefaultRpcClient defaultRpcClient
    RpcTransport rpcTransport

    def setup() {
        rpcTransport = Mock(RpcTransport)
        defaultRpcClient = new DefaultRpcClient(rpcTransport)
    }

    def "Load current height"() {
        when:
        def act = defaultRpcClient.eth().getBlockNumber().get()
        then:
        1 * rpcTransport.execute("eth_blockNumber", [], String) >> new CompletedFuture<>("0x1f47d0")
        act == 2050000
    }

    def "Get balance"() {
        when:
        def act = defaultRpcClient.eth().getBalance(Address.from('0xf45c301e123a068badac079d0cff1a9e4ad51911'), BlockTag.LATEST).get()
        then:
        1 * rpcTransport.execute("eth_getBalance", ['0xf45c301e123a068badac079d0cff1a9e4ad51911', 'latest'], String) >> new CompletedFuture<>("0x0234c8a3397aab58")
        act.toString() == "0.1590 ether"

        when:
        act = defaultRpcClient.eth().getBalance(Address.from('0xf45c301e123a068badac079d0cff1a9e4ad51911'), 2050000).get()
        then:
        1 * rpcTransport.execute("eth_getBalance", ['0xf45c301e123a068badac079d0cff1a9e4ad51911', '0x1f47d0'], String) >> new CompletedFuture<>("0x0234c8a3397aab58")
        act.toString() == "0.1590 ether"
    }

    def "Get block by number"() {
        setup:
        def json = new BlockJson()
        json.number = 2050000
        when:
        def act = defaultRpcClient.eth().getBlock(2050000, false)
        then:
        1 * rpcTransport.execute("eth_getBlockByNumber", ['0x1f47d0', false], BlockJson) >> new CompletedFuture<>(json)
        act.get() == json

        when:
        act = defaultRpcClient.eth().getBlock(2050000, true)
        then:
        1 * rpcTransport.execute("eth_getBlockByNumber", ['0x1f47d0', true], BlockJson) >> new CompletedFuture<>(json)
        act.get() == json
    }

    def "Get block by hash"() {
        setup:
        def json = new BlockJson()
        json.number = 2050000
        when:
        def act = defaultRpcClient.eth().getBlock(BlockHash.from('0xdb87647a46c2418c22250ecb23a3861bd6a223632d85b5c5af12303a04387339'), false)
        then:
        1 * rpcTransport.execute("eth_getBlockByHash", ['0xdb87647a46c2418c22250ecb23a3861bd6a223632d85b5c5af12303a04387339', false], BlockJson) >> new CompletedFuture<>(json)
        act.get() == json

        when:
        act = defaultRpcClient.eth().getBlock(BlockHash.from('0xdb87647a46c2418c22250ecb23a3861bd6a223632d85b5c5af12303a04387339'), true)
        then:
        1 * rpcTransport.execute("eth_getBlockByHash", ['0xdb87647a46c2418c22250ecb23a3861bd6a223632d85b5c5af12303a04387339', true], BlockJson) >> new CompletedFuture<>(json)
        act.get() == json
    }

    def "Get tx by hash"() {
        setup:
        def json = new TransactionJson()
        json.hash = TransactionId.from("0x1e694eba2778d34855fa1e01e0765acb31ce75a9abe8667882ffc2c12f4372bc")
        when:
        def act = defaultRpcClient.eth().getTransaction(TransactionId.from("0x1e694eba2778d34855fa1e01e0765acb31ce75a9abe8667882ffc2c12f4372bc"))
        then:
        1 * rpcTransport.execute("eth_getTransactionByHash",
                ["0x1e694eba2778d34855fa1e01e0765acb31ce75a9abe8667882ffc2c12f4372bc"], TransactionJson) >> new CompletedFuture<>(json)
        act.get() == json
    }
    def "Get tx by block hash and index"() {
        setup:
        def json = new TransactionJson()
        json.hash = TransactionId.from("0x1e694eba2778d34855fa1e01e0765acb31ce75a9abe8667882ffc2c12f4372bc")
        when:
        def act = defaultRpcClient.eth().getTransaction(BlockHash.from('0x604f7bef716ded3aeea97946652940c0c075bcbb2e6745af042ab1c1ad988946'), 0)
        then:
        1 * rpcTransport.execute("eth_getTransactionByBlockHashAndIndex",
                ["0x604f7bef716ded3aeea97946652940c0c075bcbb2e6745af042ab1c1ad988946", '0x0'], TransactionJson) >> new CompletedFuture<>(json)
        act.get() == json
    }
    def "Get tx by block number and index"() {
        setup:
        def json = new TransactionJson()
        json.hash = TransactionId.from("0x1e694eba2778d34855fa1e01e0765acb31ce75a9abe8667882ffc2c12f4372bc")
        when:
        def act = defaultRpcClient.eth().getTransaction(2007232, 0)
        then:
        1 * rpcTransport.execute("eth_getTransactionByBlockNumberAndIndex",
                ["0x1ea0c0", '0x0'], TransactionJson) >> new CompletedFuture<>(json)
        act.get() == json
    }

    def "Get tx receipt"() {
        setup:
        def json = new TransactionReceiptJson()
        json.transactionHash = TransactionId.from("0x1e694eba2778d34855fa1e01e0765acb31ce75a9abe8667882ffc2c12f4372bc")
        when:
        def act = defaultRpcClient.eth().getTransactionReceipt(TransactionId.from("0x1e694eba2778d34855fa1e01e0765acb31ce75a9abe8667882ffc2c12f4372bc"))
        then:
        1 * rpcTransport.execute("eth_getTransactionReceipt",
                ["0x1e694eba2778d34855fa1e01e0765acb31ce75a9abe8667882ffc2c12f4372bc"], TransactionReceiptJson) >> new CompletedFuture<>(json)
        act.get() == json
    }

    def "Get tx count"() {
        when:
        def act = defaultRpcClient.eth().getTransactionCount(Address.from('0xf45c301e123a068badac079d0cff1a9e4ad51911'), BlockTag.LATEST).get()
        then:
        1 * rpcTransport.execute("eth_getTransactionCount", ['0xf45c301e123a068badac079d0cff1a9e4ad51911', 'latest'], String) >> new CompletedFuture<>("0x0234")
        act == 564L

        when:
        act = defaultRpcClient.eth().getTransactionCount(Address.from('0xf45c301e123a068badac079d0cff1a9e4ad51911'), BlockTag.LATEST).get()
        then:
        1 * rpcTransport.execute("eth_getTransactionCount", ['0xf45c301e123a068badac079d0cff1a9e4ad51911', 'latest'], String) >> new CompletedFuture<>("0x1")
        act == 1L

        when:
        act = defaultRpcClient.eth().getTransactionCount(Address.from('0xf45c301e123a068badac079d0cff1a9e4ad51911'), 2050000).get()
        then:
        1 * rpcTransport.execute("eth_getTransactionCount", ['0xf45c301e123a068badac079d0cff1a9e4ad51911', '0x1f47d0'], String) >> new CompletedFuture<>("0x9")
        act == 9L
    }

    def "Get block tx count"() {
        when:
        def act = defaultRpcClient.eth().getBlockTransactionCount(
                BlockHash.from('0xdb87647a46c2418c22250ecb23a3861bd6a223632d85b5c5af12303a04387339')
        ).get()
        then:
        1 * rpcTransport.execute("eth_getBlockTransactionCountByHash", ['0xdb87647a46c2418c22250ecb23a3861bd6a223632d85b5c5af12303a04387339'], String) >> new CompletedFuture<>("0x0a")
        act == 10L

        when:
        act = defaultRpcClient.eth().getBlockTransactionCount(
                2050000
        ).get()
        then:
        1 * rpcTransport.execute("eth_getBlockTransactionCountByNumber", ['0x1f47d0'], String) >> new CompletedFuture<>("0x1")
        act == 1L
    }

    def "Get block uncles count"() {
        when:
        def act = defaultRpcClient.eth().getUncleCount(
                BlockHash.from('0xdb87647a46c2418c22250ecb23a3861bd6a223632d85b5c5af12303a04387339')
        ).get()
        then:
        1 * rpcTransport.execute("eth_getUncleCountByBlockHash", ['0xdb87647a46c2418c22250ecb23a3861bd6a223632d85b5c5af12303a04387339'], String) >> new CompletedFuture<>("0x0a")
        act == 10L

        when:
        act = defaultRpcClient.eth().getUncleCount(
                2050000
        ).get()
        then:
        1 * rpcTransport.execute("eth_getUncleCountByBlockNumber", ['0x1f47d0'], String) >> new CompletedFuture<>("0x1")
        act == 1L
    }

    def "Get code"() {
        when:
        def act = defaultRpcClient.eth().getCode(
                Address.from('0xf45c301e123a068badac079d0cff1a9e4ad51911'), BlockTag.LATEST
        ).get()
        then:
        1 * rpcTransport.execute("eth_getCode",
                ['0xf45c301e123a068badac079d0cff1a9e4ad51911', 'latest'],
                String) >> new CompletedFuture<>("0x600160008035811a818181146012578301005b601b6001356025565b8060005260206000f25b600060078202905091905056")
        act.toHex() == "0x600160008035811a818181146012578301005b601b6001356025565b8060005260206000f25b600060078202905091905056"

        when:
        act = defaultRpcClient.eth().getCode(
                Address.from('0xf45c301e123a068badac079d0cff1a9e4ad51911'), 2L
        ).get()
        then:
        1 * rpcTransport.execute("eth_getCode",
                ['0xf45c301e123a068badac079d0cff1a9e4ad51911', '0x2'],
                String) >> new CompletedFuture<>("0x600160008035811a818181146012578301005b601b6001356025565b8060005260206000f25b600060078202905091905056")
        act.toHex() == "0x600160008035811a818181146012578301005b601b6001356025565b8060005260206000f25b600060078202905091905056"
    }

    def "Get uncle"() {
        setup:
        def json = new BlockJson()
        json.number = 2050000
        when:
        def act = defaultRpcClient.eth().getUncle(BlockHash.from('0xdb87647a46c2418c22250ecb23a3861bd6a223632d85b5c5af12303a04387339'), 0L)
        then:
        1 * rpcTransport.execute("eth_getUncleByBlockHashAndIndex",
                ['0xdb87647a46c2418c22250ecb23a3861bd6a223632d85b5c5af12303a04387339', '0x0'],
                BlockJson) >> new CompletedFuture<>(json)
        act.get() == json

        when:
        act = defaultRpcClient.eth().getUncle(2050000, 0L)
        then:
        1 * rpcTransport.execute("eth_getUncleByBlockNumberAndIndex",
                ['0x1f47d0', '0x0'],
                BlockJson) >> new CompletedFuture<>(json)
        act.get() == json
    }

    def "Get trace"() {
        setup:
        def txid = TransactionId.from('0x1e694eba2778d34855fa1e01e0765acb31ce75a9abe8667882ffc2c12f4372bc')
        def json = [
                new TraceItemJson()
        ]
        when:
        def act = defaultRpcClient.trace().getTransaction(txid)
        then:
        1 * rpcTransport.execute("trace_transaction",
                ['0x1e694eba2778d34855fa1e01e0765acb31ce75a9abe8667882ffc2c12f4372bc'],
                TraceList) >> new CompletedFuture<>(json)
        act.get() == json
    }

    def "Get work"() {
        setup:
        def data = [    '0x7aecf7e21cd03501010454105ccd4b688939684505a01457cef338a33924ad02',
                        '0x002440e15267eebdf06fa7fe5aee5ccff445967925a90ecce6429aef7f8feb1f',
                        '0x000000000029891796c0001e696bca79de31c4640e112f147dc80e77263ffa1a']
        when:
        def act = defaultRpcClient.eth().getWork()
        then:
        1 * rpcTransport.execute("eth_getWork", [], HexData[]) >> new CompletedFuture<>(data)
        act.get().size() == data.size()
        act.get() as Set == data as Set
    }

    def "Submit Hashrate"() {
        setup:
        def hashRate = Hex32.from("0x0000000000000000000000000000000000000000000000000000000000500000");
        def id = Hex32.from("0x59daa26581d0acd1fce254fb7e85952f4c09d0915afd33d3886cd914bc7d283c");
        when:
        def act = defaultRpcClient.eth().submitHashrate(hashRate, id);
        then:
        1 * rpcTransport.execute("eth_submitHashrate", [hashRate.toHex(), id.toHex()], Boolean) >> new CompletedFuture<>(true)
        act.get() == true
    }

    def "Submit Work"() {
        setup:
        def nonce = Nonce.from("0x0000000000000001");
        def powHash = Hex32.from("0x0234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef");
        def digest = Hex32.from("0x01fe5700000000000000000000000000d1fe5700000000000000000000000000");
        when:
        def act = defaultRpcClient.eth().submitWork(nonce, powHash, digest);
        then:
        1 * rpcTransport.execute("eth_submitWork", [nonce.toHex(), powHash.toHex(), digest.toHex()], Boolean) >> new CompletedFuture<>(true)
        act.get() == true
    }

    def "Coinbase"() {
        setup:
        def data = '0x7aecf7e21cd03501010454105ccd4b688939684505a01457cef338a33924ad02'
        when:
        def act = defaultRpcClient.eth().getCoinbase()
        then:
        1 * rpcTransport.execute("eth_coinbase", [], Address) >> new CompletedFuture<>(data)
        act.get() == data
    }

    def "Get Hashrate"() {
        setup:
        def data = 947330l
        when:
        def act = defaultRpcClient.eth().getHashrate()
        then:
        1 * rpcTransport.execute("eth_hashrate", [], String) >> new CompletedFuture<>("0x1f47d0")
        act.get() == 2050000
    }

    def "Is Mining"() {
        when:
        def act = defaultRpcClient.eth().isMining();
        then:
        1 * rpcTransport.execute("eth_mining", [], Boolean) >> new CompletedFuture<>(true)
        act.get() == true
    }

    def "Gas price"() {
        setup:
        def data = 20000000000L
        when:
        def act = defaultRpcClient.eth().getGasPrice()
        then:
        1 * rpcTransport.execute("eth_gasPrice", [], String) >> new CompletedFuture<>("0x4A817C800")
        act.get() == data
    }

    def "Accounts"() {
        setup:
        def data = [    Address.from('0xf45c301e123a068badac079d0cff1a9e4ad51911'),
                        Address.from('0x1e45c30168ba23a0dac51911079d0fcff1a9e4ad'),
                        Address.from('0xf45c301e123a068badac079d0cff1a9e4ad51911'),
                        Address.from('0xf45c301e123a068badac079d0cff1a9e4ad51911')]
        when:
        def act = defaultRpcClient.eth().getAccounts()
        then:
        1 * rpcTransport.execute("eth_accounts", [], Address[]) >> new CompletedFuture<>(data)
        act.get().size() == data.size()
        act.get() as Set == data as Set
    }

    def "Get Compilers"() {
        setup:
        def data = ["solidity", "lll", "serpent"]
        when:
        def act = defaultRpcClient.eth().getCompilers()
        then:
        1 * rpcTransport.execute("eth_getCompilers", [], String[]) >> new CompletedFuture<>(data)
        act.get().size() == data.size()
        act.get() as Set == data as Set
    }
}
