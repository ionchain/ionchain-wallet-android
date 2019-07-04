package org.ionc.wallet.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.ionc.wallet.sdk.R;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.greenrobot.greendao.query.QueryBuilder;
import org.ionc.wallet.bean.KeystoreBean;
import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnBalanceCallback;
import org.ionc.wallet.callback.OnCheckWalletPasswordCallback;
import org.ionc.wallet.callback.OnCreateWalletCallback;
import org.ionc.wallet.callback.OnDeletefinishCallback;
import org.ionc.wallet.callback.OnImportMnemonicCallback;
import org.ionc.wallet.callback.OnSimulateTimeConsume;
import org.ionc.wallet.callback.OnTransationCallback;
import org.ionc.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionc.wallet.callback.OnUpdateWalletCallback;
import org.ionc.wallet.daohelper.EntityManager;
import org.ionc.wallet.greendaogen.DaoSession;
import org.ionc.wallet.greendaogen.TxRecordBeanDao;
import org.ionc.wallet.greendaogen.WalletBeanDao;
import org.ionc.wallet.greendaogen.WalletBeanNewDao;
import org.ionc.wallet.sdk.widget.IONCAllWalletDialogSDK;
import org.ionc.wallet.transaction.TransactionHelper;
import org.ionc.wallet.utils.GsonUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.RandomUntil;
import org.ionc.wallet.utils.SecureRandomUtils;
import org.ionc.wallet.utils.StringUtils;
import org.ionc.wallet.utils.ToastUtil;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.exceptions.ClientConnectionException;
import org.web3j.protocol.http.HttpService;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.String.valueOf;
import static org.ionc.wallet.utils.RandomUntil.getNum;

public class IONCWalletSDK {
    private volatile static IONCWalletSDK mInstance;
    public static Context appContext;
    private static String keystoreDir;
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom(); //"https://ropsten.etherscan.io/token/0x92e831bbbb22424e0f22eebb8beb126366fa07ce"
    public static final String TX_SUSPENDED = "TX_SUSPENDED";

    private Handler mHandler;
    private final String TAG = this.getClass().getSimpleName();


    /**
     * 通用的以太坊基于bip44协议的助记词路径
     */
    private static String BTC_TYPE = "m/44'/0'/0'/0/0";  //比特币
    private static String ETH_TYPE = "m/44'/60'/0'/0/0";   //以太坊
    /**
     * 轻钱包
     */
    private static final int N_LIGHT = 1 << 12;
    /**
     * 轻钱包
     */
    private static final int P_LIGHT = 6;

    /**
     * 标准钱包
     */
    private static final int N_STANDARD = 1 << 18;
    /**
     * 标准钱包
     */
    private static final int P_STANDARD = 1;

    /**
     * 助记词
     */
    private MnemonicCode mMnemonicCode = null;
    /**
     * gas下线
     */
    private final BigInteger gasLimit = Convert.toWei("21000", Convert.Unit.WEI).toBigInteger();

    /**
     * 交易的nonce值，来判断是否是同一笔交易
     */
    private BigInteger nonce;

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


    //创建钱包---借助  importWalletByMnemonicCode
    public void createIONCWallet(String walletName, String password, final OnImportMnemonicCallback callback) {
        try {
            byte[] initialEntropy = new byte[16];
            secureRandom.nextBytes(initialEntropy);//产生一个随机数
            List<String> mnemonicCode = mMnemonicCode.toMnemonic(initialEntropy);//生成助记词
            importWalletByMnemonicCode(walletName, mnemonicCode, password, callback);
        } catch (MnemonicException.MnemonicLengthException e) {
            callback.onImportMnemonicFailure(e.getMessage());
        }
    }

    /**
     * 此方式导入到的钱包时轻钱包
     *
     * @param walletName
     * @param mnemonicCode
     * @param password
     * @param callback
     */
    //导入钱包--助记词---KS
    public void importWalletByMnemonicCode(String walletName, List<String> mnemonicCode, String password, OnImportMnemonicCallback callback) {
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

    /**
     * 导入钱包--KS
     * 此方式导入到的钱包要判断是不是以太坊标准钱包 使用{@link #loadCredentialsByP(int, String, File)}
     *
     * @param walletName
     * @param password
     * @param keystoreContent
     * @param callback
     */

    public void importWalletByKeyStore(final String walletName, final String password, final String keystoreContent, final OnCreateWalletCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    KeystoreBean keystoreBean = GsonUtils.gsonToBean(keystoreContent, KeystoreBean.class);
                    if (keystoreBean == null) {
                        mHandler.post(() -> callback.onCreateFailure(appContext.getResources().getString(R.string.error_key_story)));
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
                    int p = keystoreBean.getCrypto().getKdfparams().getP();
                    LoggerUtils.i("p = " + p);
                    Credentials credentials = loadCredentialsByP(p, password, file);
                    if (p == 6) {
                        bean.setLight(true);
                    } else {
                        bean.setLight(false);
                    }
                    ECKeyPair keyPair = credentials.getEcKeyPair();
                    String wallet_name = walletName;
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
                    mHandler.post(() -> callback.onCreateSuccess(bean));
                } catch (IOException | CipherException | NullPointerException | OutOfMemoryError e) {
                    mHandler.post(() -> {
                        LoggerUtils.e(e.getMessage() + "  " + password);
                        callback.onCreateFailure(e.getMessage());
                    });
                }
            }
        }.start();

    }


    /**
     * @param walletName 钱包名字
     * @param privateKey 钱包私钥
     * @param password   钱包密码
     * @param callback   导入结果的回调
     */
    //导入钱包--私钥---产生KS
    public void importPrivateKey(String walletName, final String privateKey, final String password, final OnCreateWalletCallback callback) {

        try {
            final WalletBeanNew wallet = new WalletBeanNew();
            String name = walletName;
            if (name.equals("")) {
                name = "new-wallet" + RandomUntil.getSmallLetter(3);
            }
            BigInteger key = new BigInteger(privateKey, 16);
            ECKeyPair keyPair = ECKeyPair.create(key);
            String private_key = keyPair.getPrivateKey().toString(16);
            wallet.setPrivateKey(private_key);
            wallet.setPublic_key(keyPair.getPublicKey().toString(16));
            wallet.setAddress("0x" + Keys.getAddress(keyPair));
            wallet.setName(name);
            wallet.setMnemonic("");
            wallet.setPassword(password);
            String keystore = WalletUtils.generateWalletFile(password, keyPair, new File(keystoreDir), false);
            keystore = keystoreDir + "/" + keystore;
            wallet.setKeystore(keystore);
            wallet.setMIconIndex(getNum(7));
            mHandler.post(() -> callback.onCreateSuccess(wallet));

        } catch (CipherException | IOException e) {
            mHandler.post(() -> callback.onCreateFailure(e.getMessage()));
        }
    }

    /**
     * 更新密码
     * 更新密码后 产生的钱包就是轻钱包，此处要把钱包的属性：light设置为 true,
     *
     * @param wallet      钱包
     * @param newPassword 新密码
     * @param callback
     */
    public void updatePasswordAndKeyStore(final WalletBeanNew wallet, String newPassword, final OnUpdateWalletCallback callback) {
        try {
            BigInteger key = new BigInteger(wallet.getPrivateKey(), 16);
            ECKeyPair keyPair = ECKeyPair.create(key);

            String keystore = WalletUtils.generateWalletFile(newPassword, keyPair, new File(keystoreDir), false);  //轻钱包
            keystore = keystoreDir + "/" + keystore;
            //发生更换了
            String old = wallet.getKeystore();
            LoggerUtils.i("old keystore ==>" + old);
            //删除旧的keystore
            File file = new File(old);
            if (file.exists()) {
                file.delete();
            }
            wallet.setKeystore(keystore);
            wallet.setPassword(newPassword);
            wallet.setMIconIndex(getNum(7));
            wallet.setLight(true); //修改密码后，是轻钱包
            saveWallet(wallet);
            mHandler.post(() -> callback.onUpdateWalletSuccess(wallet));

        } catch (CipherException | NumberFormatException | IOException e) {
            mHandler.post(() -> callback.onUpdateWalletFailure(e.getMessage()));
        }
    }


    @NonNull
    private String generateNewWalletName() {
        char letter1 = (char) (int) (Math.random() * 26 + 97);
        char letter2 = (char) (int) (Math.random() * 26 + 97);
        String walletName = valueOf(letter1) + letter2 + "-new-wallet";
        while (getWalletByName(walletName) != null) {
            letter1 = (char) (int) (Math.random() * 26 + 97);
            letter2 = (char) (int) (Math.random() * 26 + 97);
            walletName = letter1 + valueOf(letter2) + "-new-wallet";
        }
        return walletName;
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
     * 获取 离子币余额
     *
     * @param node
     * @param address  钱包地址
     * @param callback 回调
     */
    public void getIONCWalletBalance(String node, String address, OnBalanceCallback callback) {
        balance(node, address, callback);
    }

//    /**
//     * 获取以太币余额
//     *
//     * @param tag
//     * @param address
//     * @param callback
//     */
//    public void getETHWalletBalance(String tag, String address, OnBalanceCallback callback) {
//        balance(ETH_CHAIN_NODE, address, callback);
//    }

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
                    Web3j web3j = Web3j.build(new HttpService(node));
                    BigInteger balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();//获取余额

                    BigDecimal balanceTemp = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
                    balanceTemp = balanceTemp.setScale(4, BigDecimal.ROUND_DOWN);  //保留4位小数,四舍五入
                    final BigDecimal finalBalanceTemp = balanceTemp;
                    mHandler.post(() -> callback.onBalanceSuccess(finalBalanceTemp, node));
                } catch (final IOException | ClientConnectionException e) {
                    LoggerUtils.e("client", e.getMessage());
                    mHandler.post(() -> {
                        LoggerUtils.e(e.getMessage());
                        callback.onBalanceFailure(appContext.getString(R.string.error_net_ionc));
                    });
                }
            }
        }.start();
    }

    /**
     * @param node         区块节点
     * @param hash         交易的 哈希值
     * @param txRecordBean
     * @param onTxRecordFromNodeCallback     回调
     */
    public void ethTransaction(final String node, final String hash, final TxRecordBean txRecordBean, final OnTxRecordFromNodeCallback onTxRecordFromNodeCallback) {

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
                        mHandler.post(() -> {
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
                    mHandler.post(() -> {
                        LoggerUtils.i("ethTransaction", "txRecordBean  getBlockNumberRaw " + ethTransaction.getBlockNumberRaw());
                        onTxRecordFromNodeCallback.OnTxRecordNodeSuccess(txRecordBean);
                    });
                } catch (final IOException e) {
                    LoggerUtils.e("client", e.getMessage());
                    mHandler.post(() -> {
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
     * @param helper   转账辅助类
     * @param callback 转账结果
     */
    public void transaction(final String nodeIONC, final TransactionHelper helper, final OnTransationCallback callback) {
        try {
            Web3j web3j = Web3j.build(new HttpService(nodeIONC));
            LoggerUtils.i("gettest","0 "+Thread.currentThread().getName());
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(helper.getWalletBeanTx().getAddress(), DefaultBlockParameterName.PENDING).sendAsync().get();//转账
            LoggerUtils.i("gettest","1 "+Thread.currentThread().getId());
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            String toAddress = helper.getToAddress().toLowerCase();
//                    Credentials credentials = MyWalletUtils.loadCredentials(helper.getWalletBeanTx().getPassword(), new File(helper.getWalletBeanTx().getKeystore()));
            Credentials credentials = loadCredentials(helper.getWalletBeanTx().getLight(), helper.getWalletBeanTx().getPassword(), new File(helper.getWalletBeanTx().getKeystore()));
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, helper.getGasPrice(), gasLimit, toAddress, helper.getTxValue());
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String signedData = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).sendAsync().get();//转账
            LoggerUtils.i("gettest","2 "+Thread.currentThread().getId());
            final String hashTx = ethSendTransaction.getTransactionHash();//转账成功hash 不为null
            LoggerUtils.i("nonce", valueOf(nonce));

            web3j.shutdown();
            if (nonce.equals(IONCWalletSDK.this.nonce)) {
                mHandler.post(callback::OnTxDoing);
                return;
            }
            IONCWalletSDK.this.nonce = nonce;
            if (!TextUtils.isEmpty(hashTx)) {
                mHandler.post(() -> callback.OnTxSuccess(hashTx, nonce));
            } else {
                mHandler.post(() -> callback.onTxFailure(appContext.getString(R.string.transacton_failed)));
            }
        } catch (final IOException | CipherException | NullPointerException | IllegalArgumentException | InterruptedException | ExecutionException e) {
            mHandler.post(() -> callback.onTxFailure(e.getMessage()));
        }
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
                    mHandler.post(simulateTimeConsume::onSimulateFinish);
                } catch (InterruptedException e) {
                    mHandler.post(simulateTimeConsume::onSimulateFinish);
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

    /**
     * 通过钱包地址查询钱包
     *
     * @param address
     * @return
     */
    public WalletBeanNew getWalletByAddress(String address) {
        WalletBeanNew wallet = null;
        List<WalletBeanNew> list = mDaoSession.getWalletBeanNewDao().queryBuilder().where(WalletBeanNewDao.Properties.Address.eq(address)).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
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
     * 查询 所有的钱包
     */
    public List<WalletBean> getAllWalletOld() {
        List<WalletBean> walletList;
        try {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            QueryBuilder<WalletBean> qb = EntityManager.getInstance().getWalletDaoOld(mDaoSession).queryBuilder();
            walletList = qb.orderDesc(WalletBeanDao.Properties.Id).list();
        } catch (Throwable e) {
            return null;
        }
        return walletList;
    }


    /**
     * 通过钱包地址查询钱包
     * <p>
     * 转入
     *
     * @param timeStemp 转入地址
     * @return 该钱包收到的转账记录
     */
    public TxRecordBean getTxRecordBeansByTimes(String timeStemp) {
        return mDaoSession.getTxRecordBeanDao().queryBuilder().where(TxRecordBeanDao.Properties.Tc_in_out.eq(timeStemp)).unique();
    }


    public boolean notExist(String hash) {
        List<TxRecordBean> list = mDaoSession.getTxRecordBeanDao().queryBuilder().where(TxRecordBeanDao.Properties.Hash.eq(hash)).list();
        int size = list.size();
        return size == 0;
    }


    /**
     * 分页加载数据
     *
     * @param offset  页数
     * @param address 查询条件 转入和转出地址
     * @param limit
     * @param num
     * @return 当页交易记录
     */
    public List<TxRecordBean> getTxRecordBeanInByAddress(int offset, String address, int limit, int num) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        return dao.queryBuilder().where(TxRecordBeanDao.Properties.To.eq(address))
                .offset(offset * num).limit(limit).list();
    }


    /**
     * 保存钱包,保存前,检查数据库是否存在钱包,如果没有则将该钱包设置为首页展示钱包
     *
     * @param wallet 钱包
     * @return
     */
    public void saveWallet(WalletBeanNew wallet) {
        if (getAllWalletNew() == null || getAllWalletNew().size() == 0) {
            wallet.setIsMainWallet(true);
        }
        //私钥不存储于数据库中
        wallet.setAddress(wallet.getAddress().toLowerCase());//小写本地地址
        wallet.setPrivateKey("");
        wallet.setPassword("");
        EntityManager.getInstance().getWalletDaoNew(mDaoSession).insertOrReplace(wallet);
    }

    /**
     * 保存钱包,保存前,检查数据库是否存在钱包,如果没有则将该钱包设置为首页展示钱包
     *
     * @param txRecordBean 钱包
     * @return
     */
    public void saveTxRecordBean(TxRecordBean txRecordBean) {

        EntityManager.getInstance().getTxRecordBeanDao(mDaoSession).insertOrReplace(txRecordBean);
    }


    /**
     * 更新钱包
     *
     * @param wallet
     */
    public void updateWallet(WalletBeanNew wallet) {

        try {
            wallet.setPrivateKey("");//移除私钥
            LoggerUtils.i("bean", "update--wallet = " + wallet.toString());
            EntityManager.getInstance().getWalletDaoNew(mDaoSession).update(wallet);
        } catch (Throwable e) {
            LoggerUtils.e("修改密码失败:" + e.getMessage());
        }
    }


    /**
     * 更新交易记录
     *
     * @param txRecordBean
     */
    public void updateTxRecordBean(TxRecordBean txRecordBean) {
        EntityManager.getInstance().getTxRecordBeanDao(mDaoSession).update(txRecordBean);
    }


    /**
     * 删除钱包,删除前
     *
     * @param currentWallet
     * @param deleteFinishCallback
     */
    public void deleteWallet(WalletBeanNew currentWallet, OnDeletefinishCallback deleteFinishCallback) {

        //私钥不存储于数据库中
        EntityManager.getInstance().getWalletDaoNew(mDaoSession).delete(currentWallet);
        File file = new File(currentWallet.getKeystore());
        if (file.exists()) {
            file.delete();
        }
        deleteFinishCallback.onDeleteFinish();
    }

    /**
     * 删除 本地的旧数据
     *
     * @param txRecordBean
     */
    public void deleteTxRecordBean(TxRecordBean txRecordBean) {

        //私钥不存储于数据库中
        EntityManager.getInstance().getTxRecordBeanDao(mDaoSession).delete(txRecordBean);
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
     * @param currentWalletTemp 当前正在操作的钱包此处可以缓存钱包的所有信息，换出位置是内存
     * @param password          密码
     * @param ksp               路径
     * @param callback          回掉
     */
    public void checkCurrentWalletPassword(WalletBeanNew currentWalletTemp, final String password, final String ksp, final OnCheckWalletPasswordCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //创建钱包
                    Credentials credentials = loadCredentials(currentWalletTemp.getLight(), password, new File(ksp));
                    ECKeyPair keyPair = credentials.getEcKeyPair();
                    currentWalletTemp.setPrivateKey(keyPair.getPrivateKey().toString(16));//私钥
                    currentWalletTemp.setPublic_key(keyPair.getPublicKey().toString(16));//公钥
                    currentWalletTemp.setAddress("0x" + Keys.getAddress(keyPair)); //地址
                    currentWalletTemp.setPassword(password); //密码
                    currentWalletTemp.setKeystore(ksp);
                    mHandler.post(() -> callback.onCheckWalletPasswordSuccess(currentWalletTemp));
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
        List<WalletBean> walletList;
        try {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            QueryBuilder<WalletBean> qb = EntityManager.getInstance().getWalletDaoOld(mDaoSession).queryBuilder();
            walletList = qb.orderDesc(WalletBeanDao.Properties.Id).list();
            return walletList != null;
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

    /**
     * 判断钱包是不是轻钱包 ,判断依据是KS中的p值
     *
     * @param p        用于判断是不是轻钱包,轻钱包的p= 6 ,p=1,标准钱包
     * @param password 钱包密码
     * @param file     ks 文件
     * @return
     * @throws IOException
     * @throws CipherException
     */
    private Credentials loadCredentialsByP(int p, String password, File file) throws IOException, CipherException {
        Credentials credentials;
        if (p == P_LIGHT) {
            credentials = WalletUtils.loadCredentials(password, file);
        } else {
            credentials = MyWalletUtils.loadCredentials(password, file);
        }
        return credentials;
    }

    /**
     * 根据数据库缓存字段
     *
     * @param light    是否是轻钱包
     * @param password 钱包密码
     * @param file
     * @return
     * @throws IOException
     * @throws CipherException
     */
    private Credentials loadCredentials(boolean light, String password, File file) throws IOException, CipherException {
        Credentials credentials;
        if (light) {
            credentials = WalletUtils.loadCredentials(password, file);
        } else {
            credentials = MyWalletUtils.loadCredentials(password, file);
        }
        return credentials;
    }


    /**
     * @param address 当前钱包相关的交易记录
     * @return 交易总数
     */
    public long txRecordItemCountAllByAddress(String address) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        return dao.queryBuilder().whereOr(TxRecordBeanDao.Properties.From.eq(address), TxRecordBeanDao.Properties.To.eq(address)).count();
    }

    /**
     * @param address 当前钱包相关的交易记录
     * @return 交易总数
     */
    public long txRecordItemCountOutByAddress(String address) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        return dao.queryBuilder().where(TxRecordBeanDao.Properties.From.eq(address)).count();
    }

    /**
     * @param address 当前钱包相关的交易记录
     * @return 交易总数
     */
    public long txRecordCountItemInByAddress(String address) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        return dao.queryBuilder().where(TxRecordBeanDao.Properties.To.eq(address)).count();
    }

    /**
     * 分页加载数据
     *
     * @param address
     * @param offset
     * @param perPageAddNum
     * @return 当页交易记录
     */
    public List<TxRecordBean> txRecordAllByAddress(String address, long offset, long perPageAddNum) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        List<TxRecordBean> list = dao.queryBuilder().whereOr(TxRecordBeanDao.Properties.From.eq(address), TxRecordBeanDao.Properties.To.eq(address))
                .offset((int) offset)
                .limit((int) perPageAddNum)
                .list();
        Collections.reverse(list);
        return list;
    }

    /**
     * 分页加载数据
     * 转出的数据
     *
     * @param address    查询条件 转出地址
     * @param offset     页数
     * @param perPageNum 每页数据量
     * @return 当页交易记录
     */
    public List<TxRecordBean> txRecordOutByAddress(String address, long offset, long perPageNum) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        List<TxRecordBean> list = dao.queryBuilder().where(TxRecordBeanDao.Properties.From.eq(address))
                .offset((int) offset)
                .limit((int) perPageNum)
                .list();
        Collections.reverse(list);
        return list;
    }

    /**
     * 分页加载数据
     * 转入的交易
     *
     * @param address    查询条件 转入地址
     * @param offset     页数
     * @param perPageNum item
     * @return 当页交易记录
     */
    public List<TxRecordBean> txRecordInByAddress(String address, long offset, long perPageNum) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        List<TxRecordBean> list = dao.queryBuilder().where(TxRecordBeanDao.Properties.To.eq(address))
                .offset((int) offset)
                .limit((int) perPageNum)
                .list();
        Collections.reverse(list);
        return list;
    }

}
