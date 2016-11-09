package org.ethereumclassic.etherjar.model;

public class Transaction {
    /**
     * The address the transaction is send from.
     */
    private Address from;

    /**
     * (optional when creating new contract) The address the transaction is directed to.
     */
    private Address to;

    /**
     * (optional, default: 90000) Integer of the gas provided for the transaction execution. It will return unused gas.
     */
    private HexQuantity gas;

    /**
     * (optional, default: To-Be-Determined) Integer of the gasPrice used for each paid gas
     */
    private Wei gasPrice;

    /**
     * (optional) Integer of the value send with this transaction
     */
    private Wei value;

    /**
     * The compiled code of a contract OR the hash of the invoked method signature and encoded parameters.
     * @see <a href="https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI">Ethereum Contract ABI</a>
     */
    private HexData data;

    /**
     * (optional) Integer of a nonce. This allows to overwrite your own pending transactions that use the same nonce.
     */
    private Long nonce;

    public Address getFrom() {
        return from;
    }

    public void setFrom(Address from) {
        this.from = from;
    }

    public Address getTo() {
        return to;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    public HexQuantity getGas() {
        return gas;
    }

    public void setGas(HexQuantity gas) {
        this.gas = gas;
    }

    public Wei getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(Wei gasPrice) {
        this.gasPrice = gasPrice;
    }

    public Wei getValue() {
        return value;
    }

    public void setValue(Wei value) {
        this.value = value;
    }

    public HexData getData() {
        return data;
    }

    public void setData(HexData data) {
        this.data = data;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

}
