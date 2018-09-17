package org.ionchain.wallet.manager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.fast.lib.utils.encrypt.base.TextUtils;

import org.ionchain.wallet.App;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.callback.OnBalanceCallback;
import org.ionchain.wallet.callback.OnCreateWalletCallback;
import org.ionchain.wallet.callback.OnImportPrivateKeyCallback;
import org.ionchain.wallet.callback.OnTransationCallback;
import org.ionchain.wallet.comm.api.myweb3j.MnemonicUtils;
import org.ionchain.wallet.comm.api.myweb3j.SecureRandomUtils;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.utils.RandomUntil;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static org.ionchain.wallet.comm.api.ApiWalletManager.DEF_WALLET_PATH;
import static org.ionchain.wallet.comm.api.myweb3j.MnemonicUtils.generateMnemonic;
import static org.ionchain.wallet.utils.RandomUntil.getNum;
import static org.web3j.crypto.Hash.sha256;

/**
 * USER: binny
 * DATE: 2018/9/14
 * 描述: 钱包管理类
 * 钱包的创建
 * 获获取余额{@link WalletManager#getAccountBalance(WalletBean, OnBalanceCallback)}
 */
public class WalletManager {
    private volatile static WalletManager mInstance;
    private static Web3j web3j;
    private Context mContext;
    private String keystoreDir = Environment.getExternalStorageDirectory().toString() + "/ionchain/keystore";
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom(); //"https://ropsten.etherscan.io/token/0x92e831bbbb22424e0f22eebb8beb126366fa07ce"

    private BigDecimal mDefaultPrice;
    private BigDecimal minFee;
    private BigDecimal maxFee;

    private final String TAG = this.getClass().getSimpleName();

    public final static int GWEI_MAX_VALUE = 20;
    public final static int GWEI_MIN_VALUE = 1;

    public final static BigInteger GAS_LIMIT = BigInteger.valueOf(30000);

    /**
     * @return 显示的默认矿工费
     */
    public BigDecimal getDefaultPrice() {
        return mDefaultPrice;
    }

    /**
     * @return 最小矿工费
     */
    public BigDecimal getMinFee() {
        return minFee;
    }

    /**
     * @return 最大旷矿工给
     */
    public BigDecimal getMaxFee() {
        return maxFee;
    }

    /**
     * @return seekbar的最大值
     */
    public int getSeekBarMaxValue() {
        return GWEI_MAX_VALUE * 5;
    }

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

    public Web3j getWeb3j() {
        return web3j;
    }

    /**
     * 初始化节点
     */
    public void initWeb3j(Context context) {
        if (web3j == null) {
            web3j = Web3jFactory.build(new HttpService("http://192.168.23.149:8545"));
        }
        if (mContext == null) {
            mContext = context;
        }
        //创建keystore路径
        File file = new File(keystoreDir);
        if (!file.exists()) {
            boolean crate = file.mkdirs();
        }


        minFee = Convert.fromWei(Convert.toWei(String.valueOf(GWEI_MIN_VALUE), Convert.Unit.GWEI).multiply(BigDecimal.valueOf(30000)), Convert.Unit.ETHER);
        maxFee = Convert.fromWei(Convert.toWei(String.valueOf(GWEI_MAX_VALUE), Convert.Unit.GWEI).multiply(BigDecimal.valueOf(30000)), Convert.Unit.ETHER);
        Log.i(TAG, "minFee: " + minFee);
        Log.i(TAG, "maxFee: " + maxFee);


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
                    BigInteger fee = gasPrice.multiply(GAS_LIMIT);
                    Log.i(TAG, "run:fee " + fee);
                    mDefaultPrice = Convert.fromWei(String.valueOf(fee), Convert.Unit.ETHER);
                    Log.i(TAG, "run:mDefaultPrice-eth " + mDefaultPrice);
                } catch (IOException e) {
                    Log.i(TAG, "run: " + e.getMessage());
                    mDefaultPrice = Convert.toWei(Convert.toWei(String.valueOf(3), Convert.Unit.GWEI), Convert.Unit.ETHER);
                }

            }
        }.start();
        if (null == MnemonicUtils.WORD_LIST) {
            String info = readAssetsTxt(this.mContext, "en-mnemonic-word-list");
            String[] a = info.split("\n");
            List<String> arr = new ArrayList<>();
            for (String word :
                    a) {
                arr.add(word);
            }
            MnemonicUtils.WORD_LIST = arr;
        }
    }

    private String readAssetsTxt(Context context, String fileName) {
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(fileName + ".txt");
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            // Should never happen!
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return "读取错误，请检查文件名";
    }

    /**
     * @param name
     * @param password 钱包密码
     */
    public void createBip39Wallet(String name, String password, final OnCreateWalletCallback callback) {

        try {
            File dest = new File(keystoreDir);
            if (!dest.exists()) {
                dest.mkdir();
            }
            byte[] initialEntropy = new byte[16];
            secureRandom.nextBytes(initialEntropy);//产红一个随机数

            //生成助记词
            String mnemonic = generateMnemonic(initialEntropy);
            Log.i("mnemonic", "generateBip39Wallet: " + mnemonic);
            //根据助记词生成种子
            byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
            //根据种子生成秘钥对
            ECKeyPair privateKey = ECKeyPair.create(sha256(seed));
            //完成钱包的创建
            String walletFile = WalletUtils.generateWalletFile(password, privateKey, dest, false);
            Bip39Wallet bip39Wallet = new Bip39Wallet(walletFile, mnemonic);
            ;
            String keystore = dest + "/" + bip39Wallet.getFilename();
            String c = bip39Wallet.getMnemonic();
            Log.i("cccc", "createBip39Wallet: " + c);
            WalletBean wallet = loadWalletFile(password, bip39Wallet.getFilename());
            wallet.setKeystore(keystore);
            wallet.setName(name);
            wallet.setIconIdex(getNum(7));//设置随机的头像
            WalletDaoTools.saveWallet(wallet);
            callback.onCreateSuccess(wallet);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onCreateFailure(e.getMessage());
        }
    }


    private Bip39Wallet generateBip39Wallet(String password, File destinationDirectory)
            throws CipherException, IOException {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);//产红一个随机数

        //生成助记词
        String mnemonic = generateMnemonic(initialEntropy);
        Log.i("mnemonic", "generateBip39Wallet: " + mnemonic);
        //根据助记词生成种子
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        //根据种子生成秘钥对
        ECKeyPair privateKey = ECKeyPair.create(sha256(seed));
        //完成钱包的创建
        String walletFile = WalletUtils.generateWalletFile(password, privateKey, destinationDirectory, false);

        return new Bip39Wallet(walletFile, mnemonic);
    }

    /**
     * 通过 钱包密码和keystore文件导入钱包
     *
     * @param password
     * @param walletFileName
     * @return
     * @throws Exception
     */
    private WalletBean loadWalletFile(String password, String walletFileName) throws Exception {
        WalletBean bean = new WalletBean();
        String src = keystoreDir + "/" + walletFileName;
        Credentials credentials = WalletUtils.loadCredentials(password, src);
        ECKeyPair keyPair = credentials.getEcKeyPair();
        bean.setPrivateKey(keyPair.getPrivateKey().toString(16));//私钥
        bean.setPublickey(keyPair.getPublicKey().toString(16));//公钥
        bean.setAddress("0x" + Keys.getAddress(keyPair)); //地址
        bean.setPassword(password); //密码
        return bean;
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

    //
//    private String[] getAccountTuple(ECKeyPair keyPair) {
//        return new String[]{
//                keyPair.getPrivateKey().toString(16),
//                keyPair.getPublicKey().toString(16),
//                Keys.getAddress(keyPair)
//        };
//    }
    /*
     * * 通过私钥创建秘钥对
     * <p>
     * 从私钥可以得到公钥，然后进一步得到账户地址，而反之则无效。
     * 显然，以太坊不需要一个中心化的账户管理系统，我们可以根据以太坊约定 的算法自由地生成账户。
     *
     * @param privateKey 私钥
     * @param passwrd    钱包密码
     * @param callback   创建结果的回调
     */
    public void importPrivateKey(final String privateKey, final String passwrd, final OnCreateWalletCallback callback) {

        try {
            final WalletBean wallet = new WalletBean();
            String walletname = "新增钱包" + RandomUntil.getSmallLetter(3);
            BigInteger key = new BigInteger(privateKey, 16);
            ECKeyPair keyPair = ECKeyPair.create(key);
            wallet.setPrivateKey(keyPair.getPrivateKey().toString(16));
            wallet.setPublickey(keyPair.getPublicKey().toString(16));
            wallet.setAddress("0x" + Keys.getAddress(keyPair));
            wallet.setName(walletname);
            wallet.setPassword(passwrd);
            String keystore = WalletUtils.generateWalletFile(wallet.getPassword(), keyPair, new File(DEF_WALLET_PATH), false);
            keystore = DEF_WALLET_PATH + "/" + keystore;
            wallet.setKeystore(keystore);
            wallet.setIconIdex(getNum(7));//设置随机的头像
            WalletDaoTools.saveWallet(wallet);
            callback.onCreateSuccess(wallet);
        } catch (CipherException | IOException e) {
            callback.onCreateFailure(e.getMessage());
        }
    }


    /**
     * @param walletBean 钱包地址
     */
    public void getAccountBalance(final WalletBean walletBean, final OnBalanceCallback callback) {


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    BigInteger balance = web3j.ethGetBalance(walletBean.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();

                    BigDecimal balacne = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
                    System.out.println(balacne);
                    walletBean.setBalance(String.valueOf(balacne));
                    WalletDaoTools.updateWallet(walletBean);
                    App.mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onBalanceSuccess(walletBean);
                        }
                    });
                } catch (final IOException e) {
                    App.mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onBalanceFailure(e.getMessage());
                        }
                    });
                }
            }
        }.start();
    }


    /**
     * 钱包转账
     *
     * @param from            转出地址
     * @param to              转入地址
     * @param currentGasPrice gasPrice
     * @param privateKey      钱包私钥
     * @param account         转账金额
     */
    public void transaction(final String from, final String to, final BigDecimal currentGasPrice, final String privateKey, final double account, final OnTransationCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                BigInteger nonce;
                EthGetTransactionCount ethGetTransactionCount = null;
                try {
                    ethGetTransactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send();
                } catch (final IOException e) {
                    App.mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onTxFailure(e.getMessage());
                        }
                    });
                    return;
                }
                if (ethGetTransactionCount == null) return;
                nonce = ethGetTransactionCount.getTransactionCount();
//                BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(3), Convert.Unit.GWEI).toBigInteger();
//                BigInteger gasLimit = BigInteger.valueOf(30000);

                BigInteger gasPrice = currentGasPrice.toBigInteger();
                BigInteger gasLimit = GAS_LIMIT;
                String toAddress = to.toLowerCase();
                BigInteger value = Convert.toWei(BigDecimal.valueOf(account), Convert.Unit.ETHER).toBigInteger();
                String data = "";
                byte chainId = ChainId.NONE;
                String signedData;
                try {
                    signedData = signTransaction(nonce, gasPrice, gasLimit, toAddress, value, data, chainId, privateKey);
                    if (signedData != null) {
                        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();
                        final String hashTx = ethSendTransaction.getTransactionHash();//转账成功hash 不为null
                        if (!TextUtils.isEmpty(hashTx)) {
                            App.mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.OnTxSuccess(hashTx);
                                }
                            });
                        } else {
                            App.mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onTxFailure("null......");
                                }
                            });
                        }
                    }
                } catch (final IOException e) {
                    App.mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onTxFailure(e.getMessage());
                        }
                    });
                }
            }
        }
                .start();
    }

    /**
     * 签名交易
     */
    private String signTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
                                   BigInteger value, String data, byte chainId, String privateKey) {
        byte[] signedMessage;
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                value,
                data);

        if (privateKey.startsWith("0x")) {
            privateKey = privateKey.substring(2);
        }
        ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
        Credentials credentials = Credentials.create(ecKeyPair);

        if (chainId > ChainId.NONE) {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        } else {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        }

        return Numeric.toHexString(signedMessage);
    }

    /**
     * 异步获取 私钥
     *
     * @param keystore
     * @param password
     */
    public void exportPrivateKey(final String keystore, final String password, final OnImportPrivateKeyCallback callback) {

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    Credentials credentials = WalletUtils.loadCredentials(password, keystore);
                    final String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
                    App.mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onImportPriKeySuccess(privateKey);
                        }
                    });
                } catch (final Exception e) {
                    App.mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onImportPriKeySuccess(e.getMessage());
                        }
                    });
                }

            }
        }.start();
    }

}
