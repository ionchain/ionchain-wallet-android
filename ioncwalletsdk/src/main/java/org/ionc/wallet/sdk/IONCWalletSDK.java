package org.ionc.wallet.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ionc.wallet.sdk.R;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.greenrobot.greendao.query.QueryBuilder;
import org.ionc.wallet.bean.KeystoreBean;
import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnBalanceCallback;
import org.ionc.wallet.callback.OnCheckWalletPasswordCallback;
import org.ionc.wallet.callback.OnCreateWalletCallback;
import org.ionc.wallet.callback.OnDeletefinishCallback;
import org.ionc.wallet.callback.OnImportMnemonicCallback;
import org.ionc.wallet.callback.OnImportPrivateKeyCallback;
import org.ionc.wallet.callback.OnModifyWalletPassWordCallback;
import org.ionc.wallet.callback.OnSimulateTimeConsume;
import org.ionc.wallet.callback.OnTransationCallback;
import org.ionc.wallet.callback.OnUpdateWalletCallback;
import org.ionc.wallet.daohelper.EntityManager;
import org.ionc.wallet.greendaogen.DaoSession;
import org.ionc.wallet.greendaogen.WalletBeanDao;
import org.ionc.wallet.greendaogen.WalletBeanNewDao;
import org.ionc.wallet.sdk.widget.IONCAllWalletDialogSDK;
import org.ionc.wallet.transaction.TransactionHelper;
import org.ionc.wallet.utils.GsonUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.MnemonicUtils;
import org.ionc.wallet.utils.RandomUntil;
import org.ionc.wallet.utils.SecureRandomUtils;
import org.ionc.wallet.utils.StringUtils;
import org.ionc.wallet.utils.ToastUtil;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.ionc.wallet.constant.ConstanParams.GAS_MIN;
import static org.ionc.wallet.constant.ConstantUrl.ETH_CHAIN_NODE;
import static org.ionc.wallet.utils.MnemonicUtils.generateMnemonic;
import static org.ionc.wallet.utils.RandomUntil.getNum;
import static org.web3j.crypto.Hash.sha256;

public class IONCWalletSDK {
    private volatile static IONCWalletSDK mInstance;
    public static Context appContext;
    private static String keystoreDir;
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom(); //"https://ropsten.etherscan.io/token/0x92e831bbbb22424e0f22eebb8beb126366fa07ce"


    private Handler mHandler;
    private final String TAG = this.getClass().getSimpleName();

    private BigInteger gas = GAS_MIN;

    /**
     * 通用的以太坊基于bip44协议的助记词路径 （imtoken jaxx Metamask myetherwallet）
     */
    private static String BTC_TYPE = "m/44'/0'/0'/0/0";  //比特币
    private static String ETH_TYPE = "m/44'/60'/0'/0/0";   //以太坊


    private MnemonicCode mMnemonicCode = null;
    private final BigInteger gasLimit = Convert.toWei("21000", Convert.Unit.WEI).toBigInteger();

    private IONCWalletSDK() {

    }

    public static IONCWalletSDK getInstance() {
        if (mInstance == null) {
            synchronized (IONCWalletSDK.class) {
                if (mInstance == null) {
                    mInstance = new IONCWalletSDK();
                }
            }
        }
        return mInstance;
    }

    private String getKeystoreDir() {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = appContext.getExternalCacheDir().getPath();
        } else {
            cachePath = appContext.getCacheDir().getPath();
        }
        return cachePath;
    }

    private DaoSession mDaoSession;

    /**
     * 初始化钱包
     */
    public void initIONCWalletSDK(Context context, DaoSession daoSession) {
        try {
            mDaoSession = daoSession;
            appContext = context.getApplicationContext();
            mHandler = new Handler(appContext.getMainLooper());
            mMnemonicCode = new MnemonicCode(appContext.getAssets().open("en-mnemonic-word-list.txt"), null);
            keystoreDir = appContext.getCacheDir().getPath() + "/ionchain/keystore";
            //创建keystore路径
            File file = new File(keystoreDir);
            if (!file.exists()) {
                boolean crate = file.mkdirs();
            }
            LoggerUtils.i("文件创建成功file =" + file.getPath());
        } catch (IOException e) {
            LoggerUtils.i(e.getMessage());
        }
    }

    /**
     * 获取当前进度条下的费用
     *
     * @param currentProgress
     * @return
     */
    public BigDecimal getCurrentFee(int currentProgress) {
        BigInteger fee = gas.multiply(BigInteger.valueOf(currentProgress));
        Log.i(TAG, "getCurrentFee: " + fee);
        /*
         * 从Gwei到wei,再从wei到ether
         * */
        return Convert.fromWei(Convert.toWei(String.valueOf(fee), Convert.Unit.GWEI), Convert.Unit.ETHER);
    }

    //创建钱包---借助  importWalletByMnemonicCode
    public void createBip39Wallet(String walletName, String password, final OnImportMnemonicCallback callback) {
        try {
            byte[] initialEntropy = new byte[16];
            secureRandom.nextBytes(initialEntropy);//产生一个随机数
            List<String> mnemonicCode = mMnemonicCode.toMnemonic(initialEntropy);//生成助记词
            importWalletByMnemonicCode(walletName, mnemonicCode, password, callback);
        } catch (MnemonicException.MnemonicLengthException e) {
            callback.onImportMnemonicFailure(e.getMessage());
        }
    }

    //导入钱包--助记词---KS
    public void importWalletByMnemonicCode(String walletName, List<String> mnemonicCode, String password, OnImportMnemonicCallback callback) {
//        try {
//            MnemonicValidator.ofWordList(English.INSTANCE).validate(mnemonics);
//        } catch (InvalidChecksumException e) {
//            e.printStackTrace();
//        } catch (InvalidWordCountException e) {
//            e.printStackTrace();
//        } catch (WordNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnexpectedWhiteSpaceException e) {
//            e.printStackTrace();
//        }
        String[] pathArray = ETH_TYPE.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(mnemonicCode, null, passphrase, creationTimeSeconds);
        //种子
        byte[] seedBytes = ds.getSeedBytes();

        if (seedBytes == null) {
            callback.onImportMnemonicFailure(appContext.getString(R.string.create_wallte_failur));
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

        WalletBeanNew walletBean = new WalletBeanNew();
//            WalletFile walletFile = Wallet.create(password, ecKeyPair, 1024, 1); // WalletUtils. .generateNewWalletFile();
        String privateKey = ecKeyPair.getPrivateKey().toString(16);
        String publicKey = ecKeyPair.getPublicKey().toString(16);
        walletBean.setPrivateKey(privateKey);
        walletBean.setPublic_key(publicKey);
        LoggerUtils.i("私钥： " + privateKey);
        try {
            String keystore = WalletUtils.generateWalletFile(password, ecKeyPair, new File(keystoreDir), false);
            keystore = keystoreDir + "/" + keystore;
            walletBean.setKeystore(keystore);
            LoggerUtils.i("钱包keystore： " + keystore);
            if (StringUtils.isEmpty(walletName)) {
                walletName = generateNewWalletName();
            }
            walletBean.setName(walletName);
//            String addr1 = walletFile.getAddress();
            String addr2 = Keys.getAddress(ecKeyPair);
            String walletAddress = Keys.toChecksumAddress(addr2);
            walletBean.setAddress(Keys.toChecksumAddress(walletAddress));//设置钱包地址
            LoggerUtils.i("钱包地址： " + walletAddress);
            walletBean.setPassword(password);


            StringBuilder sb = new StringBuilder();
            for (String mnemonic : mnemonicCode) {
                sb.append(mnemonic);
                sb.append(" ");
            }
            LoggerUtils.i("助记词 === " + sb.toString());
            String mnemonicWord = sb.toString();
            walletBean.setMnemonic(mnemonicWord);
            walletBean.setMIconIndex(getNum(7));
            callback.onImportMnemonicSuccess(walletBean);
        } catch (CipherException | IOException e) {
            callback.onImportMnemonicFailure(e.getMessage());
        }

    }

    //导入钱包--KS
    public void importWalletByKeyStore(final String namestr, final String password, final String keystoreContent, final OnCreateWalletCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    KeystoreBean keystoreBean = GsonUtils.gsonToBean(keystoreContent, KeystoreBean.class);
                    if (keystoreBean == null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onCreateFailure(appContext.getResources().getString(R.string.error_key_story));
                            }
                        });
                        return;
                    }
                    final WalletBeanNew bean = new WalletBeanNew();

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
                    Credentials credentials = WalletUtils.loadCredentials(password, file);
                    ECKeyPair keyPair = credentials.getEcKeyPair();
                    String wallet_name = namestr;
                    if (wallet_name.equals("")) {
                        //兼容SDK
                        wallet_name = "new-wallet" + RandomUntil.getSmallLetter(3);
                    }
                    bean.setName(wallet_name);
                    bean.setPrivateKey(keyPair.getPrivateKey().toString(16));//私钥
                    bean.setPublic_key(keyPair.getPublicKey().toString(16));//公钥
                    bean.setAddress("0x" + Keys.getAddress(keyPair)); //地址
                    bean.setPassword(password); //密码
                    bean.setKeystore(path);
                    bean.setMIconIndex(getNum(7));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCreateSuccess(bean);
                        }
                    });
                } catch (IOException | CipherException | NullPointerException | OutOfMemoryError e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoggerUtils.e(e.getMessage() + "  " + password);
                            callback.onCreateFailure(e.getMessage());
                        }
                    });
                }
            }
        }.start();

    }

    //导入钱包--私钥---产生KS
    public void importPrivateKey(String namestr, final String privateKey, final String password, final OnCreateWalletCallback callback) {

        try {
            final WalletBeanNew wallet = new WalletBeanNew();
            String walletname = namestr;
            if (walletname.equals("")) {
                walletname = "new-wallet" + RandomUntil.getSmallLetter(3);
            }
            BigInteger key = new BigInteger(privateKey, 16);
            ECKeyPair keyPair = ECKeyPair.create(key);
            String private_key = keyPair.getPrivateKey().toString(16);
            wallet.setPrivateKey(private_key);
            wallet.setPublic_key(keyPair.getPublicKey().toString(16));
            wallet.setAddress("0x" + Keys.getAddress(keyPair));
            wallet.setName(walletname);
            wallet.setMnemonic("");
            wallet.setPassword(password);
            String keystore = WalletUtils.generateWalletFile(password, keyPair, new File(keystoreDir), false);
            keystore = keystoreDir + "/" + keystore;
            wallet.setKeystore(keystore);
            wallet.setMIconIndex(getNum(7));
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onCreateSuccess(wallet);
                }
            });

        } catch (CipherException | IOException e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onCreateFailure(e.getMessage());
                }
            });
        }
    }

    //更新密码
    public void updatePasswordAndKeyStore(final WalletBeanNew wallet, String newPassword, final OnUpdateWalletCallback callback) {
        try {
            BigInteger key = new BigInteger(wallet.getPrivateKey(), 16);
            ECKeyPair keyPair = ECKeyPair.create(key);

            String keystore = WalletUtils.generateWalletFile(newPassword, keyPair, new File(keystoreDir), false);
            keystore = keystoreDir + "/" + keystore;
            wallet.setKeystore(keystore);
            wallet.setPassword("");
            wallet.setMIconIndex(getNum(7));
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onUpdateWalletSuccess(wallet);
                }
            });

        } catch (CipherException | NumberFormatException | IOException e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onUpdateWalletFailure(e.getMessage());
                }
            });
        }
    }


    //修改密码  没有所谓的 修改的密码实现是 利用私匙重新生成一个keystore
    public void modifyPassWord(final WalletBeanNew wallet, final String newPassWord, final OnModifyWalletPassWordCallback callback) {

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    String keystore;
                    String key = wallet.getPrivateKey();
                    LoggerUtils.i("key", "importWallt: " + key);
                    BigInteger privateKeyBig = new BigInteger(key, 16);
                    ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyBig);
                    keystore = WalletUtils.generateWalletFile(newPassWord, ecKeyPair, new File(keystoreDir), false);
                    keystore = keystoreDir + "/" + keystore;

                    LoggerUtils.i("new keystore ==>" + keystore);

                    //发生更换了
                    String old = wallet.getKeystore();
                    LoggerUtils.i("old keystore ==>" + old);
                    //删除旧的keystore
                    File file = new File(old);
                    if (file.exists()) {
                        file.delete();
                    }
                    wallet.setKeystore(keystore);
                    wallet.setPassword("");
                    updateWallet(wallet);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onModifySuccess(wallet);
                        }
                    });
                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onModifyFailure(e.getMessage());
                        }
                    });

                }

            }
        }.start();
    }

    //导出私钥-- 异步获取
    public void exportPrivateKey(final String keystore, final String pwd_dao, final OnImportPrivateKeyCallback callback) {

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    Credentials credentials = WalletUtils.loadCredentials(pwd_dao, keystore);
                    final String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onImportPriKeySuccess(privateKey);
                        }
                    });
                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onImportPriKeySuccess(e.getMessage());
                        }
                    });
                }

            }
        }.start();
    }

    @NonNull
    private String generateNewWalletName() {
        char letter1 = (char) (int) (Math.random() * 26 + 97);
        char letter2 = (char) (int) (Math.random() * 26 + 97);
        String walletName = String.valueOf(letter1) + String.valueOf(letter2) + "-new-wallet";
        while (getWalletByName(walletName) != null) {
            letter1 = (char) (int) (Math.random() * 26 + 97);
            letter2 = (char) (int) (Math.random() * 26 + 97);
            walletName = String.valueOf(letter1) + String.valueOf(letter2) + "-new-wallet";
        }
        return walletName;
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
        LoggerUtils.i("mnemonic", "generateBip39Wallet: " + mnemonic);
        //根据助记词生成种子
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        //根据种子生成秘钥对
        ECKeyPair privateKey = ECKeyPair.create(sha256(seed));
        //完成钱包的创建
        String walletFile = WalletUtils.generateWalletFile(password, privateKey, destinationDirectory, false);

        return new Bip39Wallet(walletFile, mnemonic);
    }


    /**
     * 创建文件名
     *
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
     * 获取 离子币余额
     *
     * @param node
     * @param address  钱包地址
     * @param callback 回调
     */
    public void getIONCWalletBalance(String node, String address, OnBalanceCallback callback) {
        balance(node, address, callback);
    }

    /**
     * 获取以太币余额
     *
     * @param tag
     * @param address
     * @param callback
     */
    public void getETHWalletBalance(String tag, String address, OnBalanceCallback callback) {
        balance(ETH_CHAIN_NODE, address, callback);
    }

    /**
     * @param node     区块节点
     * @param address  钱包地址
     * @param callback 回调
     */
    private void balance(final String node, final String address, final OnBalanceCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Web3j web3j = Web3jFactory.build(new HttpService(node));
                    BigInteger balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();//获取余额

                    BigDecimal balanceTemp = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
                    balanceTemp = balanceTemp.setScale(4, BigDecimal.ROUND_DOWN);  //保留4位小数,四舍五入
                    final BigDecimal finalBalanceTemp = balanceTemp;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onBalanceSuccess(finalBalanceTemp, node);
                        }
                    });
                } catch (final IOException e) {
                    LoggerUtils.e("client", e.getMessage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoggerUtils.e(e.getMessage());
                            callback.onBalanceFailure(appContext.getString(R.string.error_net_ionc));
                        }
                    });
                }
            }
        }.start();
    }


    /**
     * 转账
     *
     * @param nodeIONC 主链节点
     * @param helper   转账辅助类
     * @param callback 转账结果
     */
    public void transaction(final String nodeIONC, final TransactionHelper helper, final OnTransationCallback callback) {
        new Thread() {
            /**
             * BigInteger value = Convert.toWei(BigDecimal.valueOf(account), Convert.Unit.ETHER).toBigInteger();
             */
            @SuppressWarnings("UnnecessaryLocalVariable")
            @Override
            public void run() {
                super.run();
                try {
                    Web3j web3j = Web3jFactory.build(new HttpService(nodeIONC));
                    EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(helper.getWalletBeanTx().getAddress(), DefaultBlockParameterName.PENDING).send();//转账
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                    String toAddress = helper.getToAddress().toLowerCase();
                    Credentials credentials = WalletUtils.loadCredentials(helper.getWalletBeanTx().getPassword(), helper.getWalletBeanTx().getKeystore());
                    RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, helper.getGasPrice(), gasLimit, toAddress, helper.getTxValue());
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                    String signedData = Numeric.toHexString(signedMessage);
                    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();//转账
                    final String hashTx = ethSendTransaction.getTransactionHash();//转账成功hash 不为null
                    if (!TextUtils.isEmpty(hashTx)) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.OnTxSuccess(hashTx);
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onTxFailure(appContext.getString(R.string.transacton_failed));
                            }
                        });
                    }
                } catch (final IOException | CipherException | NullPointerException e) {
                    mHandler.post(new Runnable() {
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
     * 模拟耗时操作
     */
    public void simulateTimeConsuming(final OnSimulateTimeConsume simulateTimeConsume) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            simulateTimeConsume.onSimulateFinish();
                        }
                    });
                } catch (InterruptedException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            simulateTimeConsume.onSimulateFinish();
                        }
                    });
                }
            }
        }.start();
    }

    public WalletBeanNew getWalletByName(String name) {
        WalletBeanNew wallet = null;
        List<WalletBeanNew> list = mDaoSession
                .getWalletBeanNewDao()
                .queryBuilder()
                .where(WalletBeanNewDao.Properties.Name.eq(name))
                .list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }

    public WalletBeanNew getWalletByAddress(String adress) {
        WalletBeanNew wallet = null;
        List<WalletBeanNew> list = mDaoSession.getWalletBeanNewDao().queryBuilder().where(WalletBeanNewDao.Properties.Address.eq(adress)).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }


    /**
     * 查询 所有的钱包
     */
    public List<WalletBeanNew> getAllWalletNew() {
        List<WalletBeanNew> walletList = new ArrayList<>();
        try {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            QueryBuilder<WalletBeanNew> qb = EntityManager.getInstance().getWalletDaoNew(mDaoSession).queryBuilder();
            walletList = qb.orderDesc(WalletBeanNewDao.Properties.Id).list();
        } catch (Throwable e) {
            return walletList;
        }
        return walletList;
    }

    /**
     * @param walletBeanNew 将此钱包设置为主钱包
     * @return
     */
    public void changeMainWalletAndSave(WalletBeanNew walletBeanNew) {
        List<WalletBeanNew> walletList;
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        QueryBuilder<WalletBeanNew> qb = EntityManager.getInstance().getWalletDaoNew(mDaoSession).queryBuilder();
        walletList = qb.orderDesc(WalletBeanNewDao.Properties.Id).list();
        int count = walletList.size();
        for (int i = 0; i < count; i++) {
            walletList.get(i).setIsMainWallet(false);
            saveWallet(walletList.get(i));
        }
        walletBeanNew.setIsMainWallet(true);
        saveWallet(walletBeanNew);
    }

    /**
     * 查询 所有的钱包
     */
    public List<WalletBean> getAllWalletOld() {
        List<WalletBean> walletList = null;
        try {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            QueryBuilder<WalletBean> qb = EntityManager.getInstance().getWalletDaoOld(mDaoSession).queryBuilder();
            walletList = qb.orderDesc(WalletBeanDao.Properties.Id).list();
        } catch (Throwable e) {
            return walletList;
        }
        return walletList;
    }


    /**
     * 保存钱包,保存前,检查数据库是否存在钱包,如果没有则将该钱包设置为首页展示钱包
     *
     * @param wallet 钱包
     * @return
     */
    public long saveWallet(WalletBeanNew wallet) {
        if (IONCWalletSDK.getInstance().getAllWalletNew() == null || IONCWalletSDK.getInstance().getAllWalletNew().size() == 0) {
            wallet.setIsMainWallet(true);
        }
        //私钥不存储于数据库中

        wallet.setPrivateKey("");
//        wallet.setPassword("");
        return EntityManager.getInstance().getWalletDaoNew(mDaoSession).insertOrReplace(wallet);
    }

  

    //移除私钥
    public void updateWallet(WalletBeanNew wallet) {

        try {
            wallet.setPrivateKey("");
            LoggerUtils.i("wallet = " + wallet.toString());
            EntityManager.getInstance().getWalletDaoNew(mDaoSession).update(wallet);
        } catch (Throwable e) {
            LoggerUtils.e("修改密码失败:" + e.getMessage());
        }
    }


    /**
     * 删除钱包,删除前
     *
     * @param wallet
     */
    public void deleteWallet(WalletBeanNew wallet, OnDeletefinishCallback deletefinishCallback) {

        //私钥不存储于数据库中
        EntityManager.getInstance().getWalletDaoNew(mDaoSession).delete(wallet);
        File file = new File(wallet.getKeystore());
        if (file.exists()) {
            file.delete();
        }
        deletefinishCallback.onDeleteFinish();
    }

    //获取最新的 最老的钱包
    public WalletBeanNew getWalletTop() {
        //私钥不存储于数据库中
        WalletBeanNew wallet = null;
        List<WalletBeanNew> list = EntityManager.getInstance().getWalletDaoNew(mDaoSession).queryBuilder().limit(1).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }


    /**
     * 支付
     *
     * @param activity
     * @param callback
     */
    public void transactionDialog(Activity activity, IONCAllWalletDialogSDK.OnTxResultCallback callback) {
        List<WalletBeanNew> beans = getAllWalletNew();
        if (beans == null || beans.size() <= 0) {
            ToastUtil.showLong(activity.getResources().getString(R.string.wallet_empty));
            return;
        }
        new IONCAllWalletDialogSDK(activity, beans, callback).show();
    }

    /**
     * @return 首页展示的钱包
     */
    public WalletBeanNew getMainWallet() {
        List<WalletBeanNew> beans = getAllWalletNew();
        WalletBeanNew walletBean = null;
        int count = beans.size();
        for (int i = 0; i < count; i++) {
            if (beans.get(i).getIsMainWallet()) {
                walletBean = beans.get(i);
                break;
            }
        }
        return walletBean;
    }

    public void release() {
        mInstance = null;
    }


    /**
     * @param bean
     * @param password 密码
     * @param ksp      路径
     * @param callback 回掉
     */
    public void checkPassword(WalletBeanNew bean, final String password, final String ksp, final OnCheckWalletPasswordCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //创建钱包
                    Credentials credentials = WalletUtils.loadCredentials(password, new File(ksp));
                    ECKeyPair keyPair = credentials.getEcKeyPair();
                    bean.setPrivateKey(keyPair.getPrivateKey().toString(16));//私钥
                    bean.setPublic_key(keyPair.getPublicKey().toString(16));//公钥
                    bean.setAddress("0x" + Keys.getAddress(keyPair)); //地址
                    bean.setPassword(password); //密码
                    bean.setKeystore(ksp);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCheckWalletPasswordSuccess(bean);
                        }
                    });
                } catch (IOException | CipherException | NullPointerException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCheckWalletPasswordFailure(e.getMessage());
                        }
                    });
                }
            }
        }.start();

    }

    /**
     * @param password 密码
     * @param ksp      路径
     * @param callback 回掉
     */
    public void checkPassword( final String password, final String ksp, final OnCheckWalletPasswordCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    //创建钱包
                    Credentials credentials = WalletUtils.loadCredentials(password, new File(ksp));
                    ECKeyPair keyPair = credentials.getEcKeyPair();
                    WalletBeanNew bean = new WalletBeanNew();
                    bean.setPrivateKey(keyPair.getPrivateKey().toString(16));//私钥
                    bean.setPublic_key(keyPair.getPublicKey().toString(16));//公钥
                    bean.setAddress("0x" + Keys.getAddress(keyPair)); //地址
                    bean.setPassword(password); //密码
                    bean.setKeystore(ksp);
                    mHandler.post(() -> callback.onCheckWalletPasswordSuccess(bean));
                } catch (IOException | CipherException | NullPointerException e) {
                    mHandler.post(() -> callback.onCheckWalletPasswordFailure(e.getMessage()));
                }
            }
        }.start();

    }


    /**
     * 检查旧表是否存在   WalletBean
     */
    public boolean checkOldDataBaseExist() {
        List<WalletBean> walletList = null;
        try {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            QueryBuilder<WalletBean> qb = EntityManager.getInstance().getWalletDaoOld(mDaoSession).queryBuilder();
            walletList = qb.orderDesc(WalletBeanDao.Properties.Id).list();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * @param walletBeanOlds 钱包旧数据库数据迁移
     */
    public void migrateDb(List<WalletBean> walletBeanOlds) {
        int count = walletBeanOlds.size();
        for (int i = 0; i < count; i++) {
            WalletBeanNew walletBeanNew = new WalletBeanNew();
            walletBeanNew.setAddress(walletBeanOlds.get(i).getAddress());
            walletBeanNew.setName(walletBeanOlds.get(i).getName());
            walletBeanNew.setKeystore(walletBeanOlds.get(i).getKeystore());
            walletBeanNew.setMIconIndex(walletBeanOlds.get(i).getMIconIdex());
            walletBeanNew.setBalance(walletBeanOlds.get(i).getBalance());
            walletBeanNew.setChosen(walletBeanOlds.get(i).getChoosen());
            walletBeanNew.setPrivateKey(walletBeanOlds.get(i).getPrivateKey());
            walletBeanNew.setMnemonic(walletBeanOlds.get(i).getMnemonic());
            walletBeanNew.setPublic_key(walletBeanOlds.get(i).getPublickey());
            if (count == 1) {
                walletBeanNew.setIsMainWallet(true);
            } else {
                walletBeanNew.setIsMainWallet(walletBeanOlds.get(i).getIsMainWallet());
            }
            walletBeanNew.setPassword(walletBeanOlds.get(i).getPassword());
            walletBeanNew.setRmb("0.000");
            EntityManager.getInstance().getWalletDaoNew(mDaoSession).insertOrReplace(walletBeanNew);
            EntityManager.getInstance().getWalletDaoOld(mDaoSession).delete(walletBeanOlds.get(i));
        }
    }

}
