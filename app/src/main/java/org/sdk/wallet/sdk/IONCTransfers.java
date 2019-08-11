package org.sdk.wallet.sdk;

import android.os.Build;
import android.text.TextUtils;

import com.google.common.collect.ImmutableList;

import org.sdk.wallet.bean.TxRecordBean;
import org.sdk.wallet.bean.WalletBeanNew;
import org.sdk.wallet.callback.OnContractCoinBalanceCallback;
import org.sdk.wallet.callback.OnContractCoinTransferCallback;
import org.sdk.wallet.callback.OnGasPriceCallback;
import org.sdk.wallet.callback.OnTransationCallback;
import org.sdk.wallet.callback.OnTxRecordFromNodeCallback;
import org.sdk.wallet.transaction.TransactionHelper;
import org.sdk.wallet.utils.LoggerUtils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static java.lang.String.valueOf;
import static org.sdk.wallet.constant.ConstanErrorCode.ERROR_TRANSCATION_FAILURE;
import static org.sdk.wallet.sdk.IONCCancelTag.CONTRACT_BALANCE_CANCEL;
import static org.sdk.wallet.sdk.IONCSDK.mHandler;
import static org.sdk.wallet.sdk.IONCWallet.loadCredentials;
import static org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction;

public class IONCTransfers {
    public static final String TX_SUSPENDED = "TX_SUSPENDED";
    public static final String TX_FAILURE = "TX_FAILURE";

    public static final String ETH_NODE = "https://mainnet.infura.io/";

    /**
     * @param node                       区块节点
     * @param hash                       交易的 哈希值
     * @param txRecordBean
     * @param onTxRecordFromNodeCallback 回调
     */
    public static void ethTransaction(final String node, final String hash, final TxRecordBean txRecordBean, final OnTxRecordFromNodeCallback onTxRecordFromNodeCallback) {

        if (TextUtils.isEmpty(hash)) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Web3j web3j = Web3j.build(new HttpService(node));
                    Transaction ethTransaction = web3j.ethGetTransactionByHash(hash).send().getTransaction().get();//获取余额
                    if (ethTransaction == null) {
                        IONCSDK.mHandler.post(() -> {
                            onTxRecordFromNodeCallback.onTxRecordNodeFailure("error", txRecordBean);
                        });
                        return;
                    }

                    if (!TextUtils.isEmpty(ethTransaction.getBlockNumberRaw())) {
                        txRecordBean.setBlockNumber(valueOf(new BigInteger(ethTransaction.getBlockNumberRaw().substring(2).toUpperCase(), 16)));
                    } else {
                        txRecordBean.setBlockNumber(TX_SUSPENDED);
                    }
                    //交易区块的哈希值
                    txRecordBean.setBlockHash(ethTransaction.getBlockHash());
                    //交易索引
                    txRecordBean.setTransactionIndex(ethTransaction.getTransactionIndexRaw());
                    txRecordBean.setRaw(ethTransaction.getRaw());
                    if (!TextUtils.isEmpty(ethTransaction.getPublicKey())) {
                        txRecordBean.setPublicKey(ethTransaction.getPublicKey());
                    }
                    txRecordBean.setR(ethTransaction.getR());
                    txRecordBean.setS(ethTransaction.getS());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        txRecordBean.setV(Math.toIntExact(ethTransaction.getV()));
                    }
                    txRecordBean.setCreates(ethTransaction.getCreates());
                    txRecordBean.setInput(ethTransaction.getInput());
                    if (!TextUtils.isEmpty(ethTransaction.getTo())) {
                        txRecordBean.setTo(ethTransaction.getTo());
                    }
                    if (!TextUtils.isEmpty(ethTransaction.getFrom())) {
                        txRecordBean.setFrom(ethTransaction.getFrom());
                    }
                    if (!TextUtils.isEmpty(ethTransaction.getValueRaw())) {
                        LoggerUtils.i(" txRecordBean   getValueRaw " + ethTransaction.getValueRaw());
                        txRecordBean.setValue(valueOf(Convert.fromWei(valueOf(new BigInteger(ethTransaction.getValueRaw().substring(2).toUpperCase(), 16)), Convert.Unit.ETHER)));
                    } else {
                        txRecordBean.setValue("");
                    }
                    if (!TextUtils.isEmpty(ethTransaction.getHash())) {
                        txRecordBean.setHash(ethTransaction.getHash());
                    }
                    txRecordBean.setGasPrice(valueOf(ethTransaction.getGasPrice()));
                    if (!TextUtils.isEmpty(ethTransaction.getGasRaw())) {
                        String gas = valueOf(new BigInteger(ethTransaction.getGasRaw().substring(2).toUpperCase(), 16));
                        txRecordBean.setGas(gas);
                    }
                    IONCSDK.mHandler.post(() -> {
                        LoggerUtils.i("ethTransaction", "txRecordBean  getBlockNumberRaw " + ethTransaction.getBlockNumberRaw());
                        onTxRecordFromNodeCallback.OnTxRecordNodeSuccess(txRecordBean);
                    });
                } catch (final IOException e) {
                    LoggerUtils.e("client", e.getMessage());
                    IONCSDK.mHandler.post(() -> {
                        LoggerUtils.e(e.getMessage());
                        onTxRecordFromNodeCallback.onTxRecordNodeFailure(e.getLocalizedMessage(), txRecordBean);
                    });
                }
            }
        }.start();
    }

    /**
     * 转账
     *
     * @param nodeIONC 主链节点
     * @param gasLimit
     * @param helper   转账辅助类
     * @param callback 转账结果
     */
    public static void transaction(final String nodeIONC, final TransactionHelper helper, final OnTransationCallback callback) {
      new Thread(){
          @Override
          public void run() {
              super.run();
              try {
                  LoggerUtils.e("fee", helper.toString());
                  Web3j web3j = Web3j.build(new HttpService(nodeIONC));
                  EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(helper.getWalletBeanTx().getAddress(), DefaultBlockParameterName.PENDING).send();//转账
                  BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                  String toAddress = helper.getToAddress().toLowerCase();
//                    Credentials credentials = MyWalletUtils.loadCredentials(helper.getWalletBeanTx().getPassword(), new File(helper.getWalletBeanTx().getKeystore()));
                  Credentials credentials = loadCredentials(helper.getWalletBeanTx().getLight(), helper.getWalletBeanTx().getPassword(), new File(helper.getWalletBeanTx().getKeystore()));
//                  Credentials credentials = WalletUtils.loadCredentials(helper.getWalletBeanTx().getPassword(), new File(helper.getWalletBeanTx().getKeystore()));;
                  RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, helper.getGasPrice(), helper.getGasLimit(), toAddress, helper.getTxValue());
//                  RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, new BigInteger("400000"), helper.getGasLimit(), toAddress, helper.getTxValue());
                  byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                  String signedData = Numeric.toHexString(signedMessage);
                  EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();//转账
                  final String hashTx = ethSendTransaction.getTransactionHash();//转账成功hash 不为null
                  if (!TextUtils.isEmpty(hashTx)) {
                      IONCSDK.mHandler.post(() -> callback.onTxSuccess(hashTx, nonce));
                  } else {
                      IONCSDK.mHandler.post(() -> callback.onTxFailure(ERROR_TRANSCATION_FAILURE));
                  }
              } catch (final IOException | CipherException | NullPointerException | IllegalArgumentException e) {
                  IONCSDK.mHandler.post(() -> callback.onTxFailure(e.getMessage()));
              }
          }
      }.start();
    }

    public static void getGasPriceETH(String node, OnGasPriceCallback priceCallback) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Web3j web3j = Web3j.build(new HttpService(node));
                    BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            priceCallback.onGasPriceSuccess(gasPrice);
                        }
                    });
                } catch (IOException e) {
                    LoggerUtils.e(e.getMessage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            priceCallback.onGasRiceFailure(e.getMessage());
                        }
                    });
                }
            }
        }.start();
    }

    public static void getGasPriceETH(OnGasPriceCallback priceCallback) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Web3j web3j = Web3j.build(new HttpService(ETH_NODE));
                    BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            priceCallback.onGasPriceSuccess(gasPrice);
                        }
                    });
                } catch (IOException e) {
                    LoggerUtils.e(e.getMessage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            priceCallback.onGasRiceFailure(e.getMessage());
                        }
                    });
                }
            }
        }.start();
    }


    /**
     * @param b
     * @param position
     * @param fromAddress     合约账户地址
     * @param contractAddress 合约地址
     */
    public static void contractBalance(int position, String fromAddress, String contractAddress, OnContractCoinBalanceCallback balanceCallback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    IONCCancelTag.CONTRACT_BALANCE_CANCEL = false;
                    Web3j web3j = Web3j.build(new HttpService(ETH_NODE));
                    Function b = new Function(
                            "balanceOf",//余额的方法名称
                            ImmutableList.of(new Address(fromAddress)),
                            Arrays.asList(new TypeReference<Address>() {
                            }, new TypeReference<Uint256>() {
                            })
                    );
                    String data = FunctionEncoder.encode(b);
                    if (CONTRACT_BALANCE_CANCEL) {
                        LoggerUtils.e("取消查询余额");
                        return;
                    }
                    String balance = web3j.ethCall(createEthCallTransaction(fromAddress, contractAddress, data), DefaultBlockParameterName.PENDING).sendAsync().get().getValue();
                    if (balance == null && !CONTRACT_BALANCE_CANCEL) {
                        LoggerUtils.e("balance = null" + fromAddress);
                        mHandler.post(() -> balanceCallback.onContractCoinBalanceSuccess(position, "0.000"));
                        return;
                    }
                    String s = new BigInteger(balance.substring(2), 16).toString();
                    LoggerUtils.e("balance = " + Convert.fromWei(new BigDecimal(s), Convert.Unit.ETHER) + "  " + balance);
                    if (!CONTRACT_BALANCE_CANCEL) {
                        mHandler.post(() -> balanceCallback.onContractCoinBalanceSuccess(position, Convert.fromWei(new BigDecimal(s), Convert.Unit.ETHER).toPlainString()));
                    } else {
                        LoggerUtils.e("取消投递消息 " + CONTRACT_BALANCE_CANCEL);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    if (!CONTRACT_BALANCE_CANCEL) {
                        mHandler.post(() -> balanceCallback.onContractCoinBalanceFailure(position, e.getMessage()));
                    }
                }
            }
        }.start();
    }

    /**
     * 通过合约发起转账
     *
     * @param fromPrivateKey  合约中账户地址对应的私钥
     * @param fromAddress     合约中的账户地址
     * @param gasPrice
     * @param gasLimit
     * @param contractAddress 合约地址，代币地址
     * @param toAddress       收款地址
     */
    public static void contractTransfer(WalletBeanNew walletBeanNew, BigInteger gasPrice, String gasLimit, String password, String contractAddress, String toAddress, double ioncWallet, OnContractCoinTransferCallback contractCoinTransferCallback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    String fromAddress = walletBeanNew.getAddress();
                    Web3j web3j = Web3j.build(new HttpService(ETH_NODE));
                    BigInteger value = Convert.toWei(BigDecimal.valueOf(ioncWallet), Convert.Unit.ETHER).toBigInteger();//转账金额

                    EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).send();
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                    LoggerUtils.e("=======nonce  :    " + nonce);
//                    BigInteger getGasPriceETH = web3j.ethGasPrice().send().getGasPrice();
//                    LoggerUtils.e("=======getGasPriceETH  :    " + getGasPriceETH);

                    //智能合约事物
                    Function function = new Function(
                            "transfer",//交易的方法名称
                            Arrays.asList(new Address(toAddress), new Uint256(value)),
                            Arrays.asList(new TypeReference<Address>() {
                            }, new TypeReference<Uint256>() {
                            })
                    );
                    String data = FunctionEncoder.encode(function);
//            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, getGasPriceETH, new BigInteger("300000"), contractAddress, value, data);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(
                            nonce,
                            gasPrice,
                            new BigInteger(gasLimit),
                            contractAddress,
                            data);
                    //通过私钥获取凭证  当然也可以根据其他的获取 其他方式详情请看web3j
//                    Credentials credentials = Credentials.create(fromPrivateKey);
                    Credentials credentials = loadCredentials(walletBeanNew.getLight(), password, new File(walletBeanNew.getKeystore()));
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                    String hexValue = Numeric.toHexString(signedMessage);
                    //发送事务
                    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
                    //事物的HASH
                    String transactionHash = ethSendTransaction.getTransactionHash();
                    mHandler.post(() -> contractCoinTransferCallback.onContractCoinTransferSuccess(transactionHash));
                } catch (CipherException | IOException | InterruptedException | ExecutionException e) {
                    mHandler.post(() -> contractCoinTransferCallback.onContractCoinTransferSuccess(e.getMessage()));
                }
            }
        }.start();

    }

    public static void contractTransfer(WalletBeanNew walletBeanNew, BigInteger gasPrice, String gasLimit, String password, double ioncWallet, OnContractCoinTransferCallback contractCoinTransferCallback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    String fromAddress = walletBeanNew.getAddress();
                    Web3j web3j = Web3j.build(new HttpService(ETH_NODE));
                    BigInteger value = Convert.toWei(BigDecimal.valueOf(ioncWallet), Convert.Unit.ETHER).toBigInteger();//转账金额

                    EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).send();
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                    LoggerUtils.e("=======nonce  :    " + nonce);
//                    BigInteger getGasPriceETH = web3j.ethGasPrice().send().getGasPrice();
//                    LoggerUtils.e("=======getGasPriceETH  :    " + getGasPriceETH);

                    //智能合约事物
                    Function function = new Function(
                            "transfer",//交易的方法名称
                            Arrays.asList(new Address("0xaCb1B4fF1974e7EF03CacBbad98448237B913036"), new Uint256(value)),
                            Arrays.asList(new TypeReference<Address>() {
                            }, new TypeReference<Uint256>() {
                            })
                    );
                    String data = FunctionEncoder.encode(function);
//            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, getGasPriceETH, new BigInteger("300000"), contractAddress, value, data);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(
                            nonce,
                            gasPrice,
                            new BigInteger(gasLimit),
                            "0xbC647aAd10114B89564c0a7aabE542bd0cf2C5aF",
                            data);
                    //通过私钥获取凭证  当然也可以根据其他的获取 其他方式详情请看web3j
//                    Credentials credentials = Credentials.create(fromPrivateKey);
                    Credentials credentials = loadCredentials(walletBeanNew.getLight(), password, new File(walletBeanNew.getKeystore()));
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                    String hexValue = Numeric.toHexString(signedMessage);
                    //发送事务
                    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
                    //事物的HASH
                    String transactionHash = ethSendTransaction.getTransactionHash();
                    mHandler.post(() -> contractCoinTransferCallback.onContractCoinTransferSuccess(transactionHash));
                } catch (CipherException | IOException | InterruptedException | ExecutionException e) {
                    mHandler.post(() -> contractCoinTransferCallback.onContractCoinTransferSuccess(e.getMessage()));
                }
            }
        }.start();

    }
}
