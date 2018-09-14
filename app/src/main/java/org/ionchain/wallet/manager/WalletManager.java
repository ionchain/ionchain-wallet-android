package org.ionchain.wallet.manager;

import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.callback.OnCreateWalletCallback;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * USER: binny
 * DATE: 2018/9/14
 * 描述: 钱包管理类
 * 钱包的创建
 * 获获取余额{@link WalletManager#getAccountBalance(String)}  }
 */
public class WalletManager {
    private volatile static WalletManager mInstance;
    private static Web3j web3j;

    private WalletManager() {

    }

    public static WalletManager getInstance() {
        if (mInstance == null) {
            synchronized (WalletManager.class) {
                if (mInstance == null) {
                    mInstance = new WalletManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化节点
     */
    public void initWeb3j() {
        if (web3j == null) {
            web3j = Web3jFactory.build(new HttpService("http://192.168.23.149:8545"));

        }
    }

    /**
     * 从私钥可以得到公钥，然后进一步得到账户地址，而反之则无效。
     * 显然，以太坊不需要一个中心化的账户管理系统，我们可以根据以太坊约定 的算法自由地生成账户。
     * <p>
     * <p>
     * 通过私钥创建秘钥对
     *
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public void importPrivateKey(String privateKey, OnCreateWalletCallback callback) {

        WalletBean wallet = WalletDaoTools.getWalletByPrivateKey(privateKey);
        if (null != wallet) {
            callback.onCreateFailure("该钱包已存在,钱包名 : " + wallet.getName());
            return;
        }
        WalletBean bean = new WalletBean();

        BigInteger key = new BigInteger(privateKey, 16);
        ECKeyPair keyPair = ECKeyPair.create(key);
        String[] tuple = getAccountTuple(keyPair);
        bean.setPrivateKey(tuple[0]);
        bean.setPublickey(tuple[1]);
        bean.setAddress(tuple[2]);
        callback.onCreateSuccess(bean);
    }

    /**
     * 返回账户信息
     * 私钥
     * 公钥
     *
     * @param keyPair
     * @return
     */
    private String[] getAccountTuple(ECKeyPair keyPair) {
        return new String[]{
                keyPair.getPrivateKey().toString(16),//私钥
                keyPair.getPublicKey().toString(16),//公钥
                Keys.getAddress(keyPair)//地址
        };
    }

    /**
     * 打印账户信息
     *
     * @param tuple
     */
    public void logAccount(String[] tuple) {
        System.out.println("私钥: " + tuple[0]);
        System.out.println("公钥: " + tuple[1]);
        System.out.println("地址: " + tuple[2]);
    }

    /**
     * @param address 钱包地址
     */
    public void getAccountBalance(String address) {

        BigInteger balance = null;
        try {
            balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
            BigDecimal gasPrice = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
            System.out.println(gasPrice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转账
     *
     * @param from   转出账户
     * @param to     转入账户
     * @param amount 账户金额
     * @return 该交易的哈希值
     * @throws Exception
     */
    public String transactionOnly(String from, String to, int amount) throws Exception {
        BigInteger value = BigInteger.valueOf(amount);
        BigInteger gasPrice = null;
        BigInteger gasLimit = null;
        DefaultBlockParameter block = DefaultBlockParameterName.LATEST;
        String data = "null";
        BigInteger nonce = null;
        Transaction tx = new Transaction(from, nonce, gasPrice, gasLimit, to, value, data);
        String txHash = web3j.ethSendTransaction(tx).send().getTransactionHash();
        System.out.println("tx hash: " + txHash);
        return txHash;
    }
}
