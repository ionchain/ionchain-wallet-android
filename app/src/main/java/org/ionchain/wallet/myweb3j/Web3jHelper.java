package org.ionchain.wallet.myweb3j;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.ionchain.wallet.App;
import org.ionchain.wallet.bean.KeystoreBean;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.mvp.callback.OnBalanceCallback;
import org.ionchain.wallet.mvp.callback.OnCreateWalletCallback;
import org.ionchain.wallet.mvp.callback.OnImportMnemonicCallback;
import org.ionchain.wallet.mvp.callback.OnImportPrivateKeyCallback;
import org.ionchain.wallet.mvp.callback.OnModifyWalletPassWordCallback;
import org.ionchain.wallet.mvp.callback.OnSimulateTimeConsume;
import org.ionchain.wallet.mvp.callback.OnTransationCallback;
import org.ionchain.wallet.mvp.callback.OnUpdatePasswordCallback;
import org.ionchain.wallet.utils.GsonUtils;
import org.ionchain.wallet.utils.RandomUntil;
import org.ionchain.wallet.utils.StringUtils;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.ionchain.wallet.constant.ConstantUrl.IONC_CHAIN_NODE;
import static org.ionchain.wallet.myweb3j.MnemonicUtils.generateMnemonic;
import static org.web3j.crypto.Hash.sha256;

/**
 * USER: binny
 * DATE: 2018/9/14
 * 描述: 钱包管理类
 * 钱包的创建
 * 获获取余额{@link Web3jHelper#getAccountBalance(WalletBean, OnBalanceCallback)}
 */
@SuppressWarnings({"JavaDoc", "ResultOfMethodCallIgnored"})
public class Web3jHelper {
    private volatile static Web3jHelper mInstance;
    private static Web3j web3j;
    private Context mContext;
    private static String keystoreDir;
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom(); //"https://ropsten.etherscan.io/token/0x92e831bbbb22424e0f22eebb8beb126366fa07ce"

    private BigDecimal mDefaultPrice;
    private BigDecimal minFee;
    private BigDecimal maxFee;

    private final String TAG = this.getClass().getSimpleName();

    public final static int GWEI_MAX_VALUE = 20;
    public final static int GWEI_MIN_VALUE = 1;

    public final static BigInteger GAS_LIMIT = BigInteger.valueOf(30000);
    /**
     * 通用的以太坊基于bip44协议的助记词路径 （imtoken jaxx Metamask myetherwallet）
     */
    public static String ETH_JAXX_TYPE = "m/44'/60'/0'/0/0";
    public static String ETH_LEDGER_TYPE = "m/44'/60'/0'/0";
    public static String ETH_CUSTOM_TYPE = "m/44'/60'/1'/0/0";


    private MnemonicCode mMnemonicCode = null;

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

    private Web3jHelper() {

    }

    public static Web3jHelper getInstance() {
        if (mInstance == null) {
            synchronized (Web3jHelper.class) {
                if (mInstance == null) {
                    mInstance = new Web3jHelper();
                }
            }
        }
        return mInstance;
    }


    /**
     * 初始化钱包
     */
    public void initWeb3j(Context context) {
        web3j = Web3jFactory.build(new HttpService(IONC_CHAIN_NODE));
        mContext = context;

        try {
            mMnemonicCode = new MnemonicCode(App.Companion.getMContext().getAssets().open("en-mnemonic-word-list.txt"), null);
            keystoreDir = context.getExternalCacheDir() + "/ionchain/keystore";
            Log.i(TAG, "initWeb3j: 文件创建成功 keystoreDir = " + keystoreDir);
            //创建keystore路径
            File file = new File(keystoreDir);
            if (!file.exists()) {
                boolean crate = file.mkdirs();
            }

            Log.i(TAG, "initWeb3j: 文件创建成功file =" + file.getPath());
            minFee = Convert.fromWei(Convert.toWei(String.valueOf(GWEI_MIN_VALUE), Convert.Unit.GWEI).multiply(BigDecimal.valueOf(30000)), Convert.Unit.ETHER);
            maxFee = Convert.fromWei(Convert.toWei(String.valueOf(GWEI_MAX_VALUE), Convert.Unit.GWEI).multiply(BigDecimal.valueOf(30000)), Convert.Unit.ETHER);
            Log.i(TAG, "minFee: " + minFee);
            Log.i(TAG, "maxFee: " + maxFee);


        } catch (IOException e) {
            Logger.i(e.getMessage());
        }


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
    }


    /**
     * 创建钱包
     *
     * @param walletName 钱包名字
     * @param password   钱包密码
     */
    public void createBip39Wallet(String walletName, String password, final OnImportMnemonicCallback callback) {

        try {
            byte[] initialEntropy = new byte[16];
            secureRandom.nextBytes(initialEntropy);//产红一个随机数
            List<String> mnemonicCode = mMnemonicCode.toMnemonic(initialEntropy);//生成助记词
            importWalletByMnemonicCode(walletName, mnemonicCode, password, callback);
        } catch (MnemonicException.MnemonicLengthException e) {
            callback.onImportMnemonicFailure(e.getMessage());
        }


    }


    /**
     * 通过助记词导入钱包
     *
     * @param walletName   钱包名字
     * @param mnemonicCode 助记词
     * @param password     密码
     * @param callback     回调结果
     */
    public void importWalletByMnemonicCode(String walletName, List<String> mnemonicCode, String password, OnImportMnemonicCallback callback) {
        String[] pathArray = ETH_JAXX_TYPE.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(mnemonicCode, null, passphrase, creationTimeSeconds);
        //种子
        byte[] seedBytes = ds.getSeedBytes();

        if (seedBytes == null) {
            callback.onImportMnemonicFailure("创建种子（钱包）失败");
            return;
        }
        DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
        for (int i = 1; i < pathArray.length; i++) {
            ChildNumber childNumber;
            if (pathArray[i].endsWith("'")) {
                int number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length() - 1));
                childNumber = new ChildNumber(number, true);
            } else {
                int number = Integer.parseInt(pathArray[i]);
                childNumber = new ChildNumber(number, false);
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber);
        }

        ECKeyPair ecKeyPair = ECKeyPair.create(dkKey.getPrivKeyBytes());

        WalletBean walletBean = new WalletBean();
//            WalletFile walletFile = Wallet.create(password, ecKeyPair, 1024, 1); // WalletUtils. .generateNewWalletFile();
        String privateKey = ecKeyPair.getPrivateKey().toString(16);
        String publicKey = ecKeyPair.getPublicKey().toString(16);
        walletBean.setPrivateKey(privateKey);
        walletBean.setPublickey(publicKey);
        Logger.i("私钥： " + privateKey);
        try {
            String keystore = WalletUtils.generateWalletFile(password, ecKeyPair, new File(keystoreDir), false);
            keystore = keystoreDir + "/" + keystore;
            walletBean.setKeystore(keystore);
            Logger.i("钱包keystore： " + keystore);
            if (StringUtils.isEmpty(walletName)) {
                walletName = generateNewWalletName();
            }
            walletBean.setName(walletName);
//            String addr1 = walletFile.getAddress();
            String addr2 = Keys.getAddress(ecKeyPair);
            String walletAddress = Keys.toChecksumAddress(addr2);
            walletBean.setAddress(Keys.toChecksumAddress(walletAddress));//设置钱包地址
            Logger.i("钱包地址： " + walletAddress);
            walletBean.setPassword(password);


//            Logger.i("addr1 " + addr1);
//            Logger.i("addr2 " + addr2);
//            Logger.i("addr3 " + Keys.toChecksumAddress(walletFile.getAddress()));

            StringBuilder sb = new StringBuilder();
            for (String mnemonic : mnemonicCode) {
                sb.append(mnemonic);
                sb.append(" ");
            }
            Logger.i("助记词 === " + sb.toString());
            String mnemonicWord = sb.toString();
            walletBean.setMnemonic(mnemonicWord);
//            WalletDaoTools.saveWallet(walletBean);
            callback.onImportMnemonicSuccess(walletBean);
        } catch (CipherException | IOException e) {
            callback.onImportMnemonicFailure(e.getMessage());
        }

    }


    @NonNull
    private static String generateNewWalletName() {
        char letter1 = (char) (int) (Math.random() * 26 + 97);
        char letter2 = (char) (int) (Math.random() * 26 + 97);
        String walletName = String.valueOf(letter1) + String.valueOf(letter2) + "-新钱包";
        while (WalletDaoTools.getWalletByName(walletName) != null) {
            letter1 = (char) (int) (Math.random() * 26 + 97);
            letter2 = (char) (int) (Math.random() * 26 + 97);
            walletName = String.valueOf(letter1) + String.valueOf(letter2) + "-新钱包";
        }
        return walletName;
    }

    private static String convertMnemonicList(List<String> mnemonics) {
        StringBuilder sb = new StringBuilder();
        for (String mnemonic : mnemonics
                ) {
            sb.append(mnemonic);
            sb.append(" ");
        }
        Logger.i("助记词 1=== " + sb.toString());

        return sb.toString();
    }


    /**
     * 创建 keystore
     *
     * @param pwd
     * @param ecKeyPair
     * @return
     * @throws CipherException
     * @throws IOException
     */
    private static String getKeystorePath(String pwd, ECKeyPair ecKeyPair) throws CipherException, IOException {
        String keystore = WalletUtils.generateWalletFile(pwd, ecKeyPair, new File(keystoreDir), false);
        keystore = keystoreDir + "/" + keystore;
        return keystore;
    }


    private static boolean createParentDir(File file) {
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建");
            if (!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        return true;
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
     * 1/创建文件
     * 2/导入钱包
     * @param password
     * @param keystoreContent
     * @return
     * @throws Exception
     */
    public void importWalletByKeyStore(final String password, final String keystoreContent, final OnCreateWalletCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    KeystoreBean keystoreBean = GsonUtils.gsonToBean(keystoreContent, KeystoreBean.class);
                    final WalletBean bean = new WalletBean();

                    String path = keystoreDir + "/" + getWalletFileName(keystoreBean.getAddress());
                    File file = new File(path);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(file, false); //如果追加方式用true,此处覆盖
//                    StringBuffer sb = new StringBuffer();
                    byte[] bytes = keystoreContent.getBytes();
                    out.write(bytes);
//                out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
                    out.close();
                    //创建钱包
                    Credentials credentials = WalletUtils.loadCredentials(password, path);
                    ECKeyPair keyPair = credentials.getEcKeyPair();
                    String walletname = "新增钱包" + RandomUntil.getSmallLetter(3);
                    bean.setName(walletname);
                    bean.setPrivateKey(keyPair.getPrivateKey().toString(16));//私钥
                    bean.setPublickey(keyPair.getPublicKey().toString(16));//公钥
                    bean.setAddress("0x" + Keys.getAddress(keyPair)); //地址
                    bean.setPassword(password); //密码
                    bean.setKeystore(path);
                    App.Companion.getMHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCreateSuccess(bean);
                        }
                    });
                } catch (IOException | CipherException e) {
                    App.Companion.getMHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCreateFailure(e.getMessage());
                        }
                    });
                }
            }
        }.start();

    }



    /**
     * 创建文件名
     * @param walletAddress
     * @return
     */
    private String getWalletFileName(String walletAddress) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.SSS'--'");
        return dateFormat.format(new Date()) + walletAddress + ".json";
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
            String private_key = keyPair.getPrivateKey().toString(16);
            wallet.setPrivateKey(private_key);
            wallet.setPublickey(keyPair.getPublicKey().toString(16));
            wallet.setAddress("0x" + Keys.getAddress(keyPair));
            wallet.setName(walletname);
            wallet.setMnemonic("");
            wallet.setPassword(passwrd);
            String keystore = WalletUtils.generateWalletFile(passwrd, keyPair, new File(keystoreDir), false);
            keystore = keystoreDir + "/" + keystore;
            wallet.setKeystore(keystore);

            App.Companion.getMHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onCreateSuccess(wallet);
                }
            });

        } catch (CipherException | IOException e) {
            App.Companion.getMHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onCreateFailure(e.getMessage());
                }
            });
        }
    }    /*
     * * 更新密码和key_store，该方法在导入私钥的时候，遇到钱包已存在的情况下调用
     * <p>
     * 从私钥可以得到公钥，然后进一步得到账户地址，而反之则无效。
     * 显然，以太坊不需要一个中心化的账户管理系统，我们可以根据以太坊约定 的算法自由地生成账户。
     *
     * @param privateKey 私钥
     * @param passwrd    钱包密码
     * @param callback   创建结果的回调
     */

    public static void updatePasswordAndKeyStore(final WalletBean wallet, String newPassword, final OnUpdatePasswordCallback callback) {
        try {

            BigInteger key = new BigInteger(wallet.getPrivateKey(), 16);
            ECKeyPair keyPair = ECKeyPair.create(key);

            wallet.setPassword(newPassword);
            String keystore = WalletUtils.generateWalletFile(newPassword, keyPair, new File(keystoreDir), false);
            keystore = keystoreDir + "/" + keystore;
            wallet.setKeystore(keystore);

            App.Companion.getMHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onUpdatePasswordSuccess(wallet);
                }
            });

        } catch (CipherException | NumberFormatException | IOException e) {
            App.Companion.getMHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onUpdatePasswordFailure(e.getMessage());
                }
            });
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
                    balacne = balacne.setScale(4, BigDecimal.ROUND_DOWN);
                    Logger.i("余额" + balacne);
                    int a = balacne.compareTo(BigDecimal.valueOf(10));
                    walletBean.setBalance(String.valueOf(balacne));
                    WalletDaoTools.updateWallet(walletBean);
                    App.Companion.getMHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onBalanceSuccess(walletBean);
                        }
                    });
                } catch (final IOException e) {
                    App.Companion.getMHandler().post(new Runnable() {
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
            @SuppressWarnings("UnnecessaryLocalVariable")
            @Override
            public void run() {
                super.run();
                BigInteger nonce;
                EthGetTransactionCount ethGetTransactionCount = null;
                try {
                    ethGetTransactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send();
                } catch (final IOException e) {
                    App.Companion.getMHandler().post(new Runnable() {
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
                    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();
                    final String hashTx = ethSendTransaction.getTransactionHash();//转账成功hash 不为null
                    if (!TextUtils.isEmpty(hashTx)) {
                        App.Companion.getMHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                callback.OnTxSuccess(hashTx);
                            }
                        });
                    } else {
                        App.Companion.getMHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onTxFailure("null......");
                            }
                        });
                    }
                } catch (final IOException e) {
                    App.Companion.getMHandler().post(new Runnable() {
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
                    App.Companion.getMHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onImportPriKeySuccess(privateKey);
                        }
                    });
                } catch (final Exception e) {
                    App.Companion.getMHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onImportPriKeySuccess(e.getMessage());
                        }
                    });
                }

            }
        }.start();
    }

    public void deleteWallet(WalletBean walletBean) {
        File file = new File(walletBean.getKeystore());
        if (file.exists()) {
            file.delete();
        }
        WalletDaoTools.deleteWallet(walletBean.getId());
    }

    /**
     * 没有所谓的 修改密码 修改的密码实现是 利用私匙重新生成一个keystore
     *
     * @param wallet
     * @param newPassWord
     * @param callback
     */
    public void modifyPassWord(final WalletBean wallet, final String newPassWord, final OnModifyWalletPassWordCallback callback) {

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    Credentials credentials = WalletUtils.loadCredentials(wallet.getPassword(), wallet.getKeystore());
                    if (null == credentials || !credentials.getAddress().equals(wallet.getAddress())) {
                        App.Companion.getMHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                App.Companion.getMHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onModifyFailure("修改失败！");
                                    }
                                });
                            }
                        });
                        return;
                    }
                    wallet.setPrivateKey(credentials.getEcKeyPair().getPrivateKey().toString(16));
                    String keystore;
                    String key = wallet.getPrivateKey();
                    Logger.i("key", "importWallt: " + key);
                    BigInteger privateKeyBig = new BigInteger(key, 16);
                    ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyBig);
                    keystore = WalletUtils.generateWalletFile(newPassWord, ecKeyPair, new File(keystoreDir), false);
                    keystore = keystoreDir + "/" + keystore;

                    Logger.i("new keystore ==>" + keystore);

                    //发生更换了
                    if (null != wallet.getKeystore() && !wallet.getKeystore().equals(keystore)) {
                        String old = wallet.getKeystore();
                        //删除旧的keystore
                        File file = new File(old);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    wallet.setKeystore(keystore);
                    wallet.setPassword(newPassWord);
                    App.Companion.getMHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onModifySuccess(wallet);
                        }
                    });
                } catch (final Exception e) {
                    App.Companion.getMHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onModifyFailure(e.getMessage());
                        }
                    });

                }

            }
        }.start();
    }

    /**
     * 模拟耗时操作
     */
    public static void simulateTimeConsuming(final OnSimulateTimeConsume simulateTimeConsume) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                    App.Companion.getMHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            simulateTimeConsume.onSimulateFinish();
                        }
                    });
                } catch (InterruptedException e) {
                    App.Companion.getMHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            simulateTimeConsume.onSimulateFinish();
                        }
                    });
                }
            }
        }.start();
    }
}
