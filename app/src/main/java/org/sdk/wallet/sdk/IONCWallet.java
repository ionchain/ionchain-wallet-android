package org.sdk.wallet.sdk;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.greenrobot.greendao.query.QueryBuilder;
import org.sdk.wallet.bean.KeystoreBean;
import org.sdk.wallet.bean.TxRecordBean;
import org.sdk.wallet.bean.WalletBean;
import org.sdk.wallet.bean.WalletBeanNew;
import org.sdk.wallet.callback.OnBalanceCallback;
import org.sdk.wallet.callback.OnCheckWalletPasswordCallback;
import org.sdk.wallet.callback.OnCreateWalletCallback;
import org.sdk.wallet.callback.OnDeletefinishCallback;
import org.sdk.wallet.callback.OnImportMnemonicCallback;
import org.sdk.wallet.callback.OnSimulateTimeConsume;
import org.sdk.wallet.callback.OnUpdateWalletCallback;
import org.sdk.wallet.daohelper.EntityManager;
import org.sdk.wallet.greendaogen.WalletBeanDao;
import org.sdk.wallet.greendaogen.WalletBeanNewDao;
import org.sdk.wallet.utils.GsonUtils;
import org.sdk.wallet.utils.LoggerUtils;
import org.sdk.wallet.utils.RandomUntil;
import org.sdk.wallet.utils.SecureRandomUtils;
import org.sdk.wallet.utils.StringUtils;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.exceptions.ClientConnectionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

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

import static java.lang.String.valueOf;
import static org.sdk.wallet.constant.ConstanErrorCode.ERROR_CODE_BALANCE_IONC;
import static org.sdk.wallet.constant.ConstanErrorCode.ERROR_CREATE_WALLET_FAILURE;
import static org.sdk.wallet.constant.ConstanErrorCode.ERROR_KEYSTORE;
import static org.sdk.wallet.sdk.IONCSDK.appContext;
import static org.sdk.wallet.sdk.IONCSDK.keystoreDir;
import static org.sdk.wallet.sdk.IONCSDK.mDaoSession;
import static org.sdk.wallet.sdk.IONCSDK.mHandler;
import static org.sdk.wallet.utils.RandomUntil.getNum;

public class IONCWallet {
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom(); //"https://ropsten.etherscan.io/token/0x92e831bbbb22424e0f22eebb8beb126366fa07ce"
    public static final String TX_SUSPENDED = "TX_SUSPENDED";
    public static final String TX_FAILURE = "TX_FAILURE";

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
     * gas下线
     */
    private final BigInteger gasLimit = Convert.toWei("21000", Convert.Unit.WEI).toBigInteger();


    //创建钱包---借助  importWalletByMnemonicCode
    public static void createIONCWallet(String walletName, String password, final OnImportMnemonicCallback callback) {
        try {
            byte[] initialEntropy = new byte[16];
            secureRandom.nextBytes(initialEntropy);//产生一个随机数
            MnemonicCode mMnemonicCode = new MnemonicCode(appContext.getAssets().open("en-mnemonic-word-list.txt"), null);
            List<String> mnemonicList = mMnemonicCode.toMnemonic(initialEntropy);//生成助记词
            importWalletByMnemonicCode(walletName, mnemonicList, password, callback);
        } catch (MnemonicException.MnemonicLengthException | IOException e) {
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
    public static void importWalletByMnemonicCode(String walletName, List<String> mnemonicCode, String password, OnImportMnemonicCallback callback) {
        String[] pathArray = ETH_TYPE.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(mnemonicCode, null, passphrase, creationTimeSeconds);
        //种子
        byte[] seedBytes = ds.getSeedBytes();

        if (seedBytes == null) {
            callback.onImportMnemonicFailure(ERROR_CREATE_WALLET_FAILURE);
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
            walletBean.setAddress(walletAddress);//设置钱包地址
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

    public static void importWalletByKeyStore(final String walletName, final String password, final String keystoreContent, final OnCreateWalletCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    KeystoreBean keystoreBean = GsonUtils.gsonToBean(keystoreContent, KeystoreBean.class);
                    if (keystoreBean == null) {
                        mHandler.post(() -> callback.onCreateFailure(ERROR_KEYSTORE));
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
    public static void importPrivateKey(String walletName, final String privateKey, final String password, final OnCreateWalletCallback callback) {

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
    public static void updatePasswordAndKeyStore(final WalletBeanNew wallet, String newPassword, final OnUpdateWalletCallback callback) {
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
    private static String generateNewWalletName() {
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
    private static String getWalletFileName(String walletAddress) {
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
    public static void getIONCWalletBalance(String node, String address, OnBalanceCallback callback) {
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
    private static void balance(final String node, final String address, final OnBalanceCallback callback) {
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
                        callback.onBalanceFailure(ERROR_CODE_BALANCE_IONC);
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
                    sleep(1000);
                    mHandler.post(simulateTimeConsume::onSimulateFinish);
                } catch (InterruptedException e) {
                    mHandler.post(simulateTimeConsume::onSimulateFinish);
                }
            }
        }.start();
    }

    public static WalletBeanNew getWalletByName(String name) {
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
    public static WalletBeanNew getWalletByAddress(String address) {
        return mDaoSession.getWalletBeanNewDao().queryBuilder().where(WalletBeanNewDao.Properties.Address.eq(address)).unique();
    }


    /**
     * @param walletBeanNew 将此钱包设置为主钱包
     * @return
     */
    public static void changeMainWalletAndSave(WalletBeanNew walletBeanNew) {
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
    public static List<WalletBeanNew> getAllWalletNew() {
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
    public static List<WalletBean> getAllWalletOld() {
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
     * 保存钱包,保存前,检查数据库是否存在钱包,如果没有则将该钱包设置为首页展示钱包
     *
     * @param wallet 钱包
     * @return
     */
    public static void saveWallet(WalletBeanNew wallet) {
        if (getAllWalletNew() == null || getAllWalletNew().size() == 0) {
            wallet.setIsMainWallet(true);
        }
        //私钥不存储于数据库中
        wallet.setAddress(wallet.getAddress().toLowerCase());//小写本地地址
        wallet.setPrivateKey("");
//        wallet.setPassword("");
        EntityManager.getInstance().getWalletDaoNew(mDaoSession).insertOrReplace(wallet);
    }



    /**
     * 更新钱包
     *
     * @param wallet
     */
    public static void updateWallet(WalletBeanNew wallet) {

        try {
            wallet.setPrivateKey("");//移除私钥
            LoggerUtils.i("bean", "update--wallet = " + wallet.toString());
            EntityManager.getInstance().getWalletDaoNew(mDaoSession).update(wallet);
        } catch (Throwable e) {
            LoggerUtils.e("修改密码失败:" + e.getMessage());
        }
    }



    /**
     * 删除钱包,删除前
     *
     * @param currentWallet
     * @param deleteFinishCallback
     */
    public static void deleteWallet(WalletBeanNew currentWallet, OnDeletefinishCallback deleteFinishCallback) {

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
    public static void deleteTxRecordBean(TxRecordBean txRecordBean) {

        //私钥不存储于数据库中
        EntityManager.getInstance().getTxRecordBeanDao(mDaoSession).delete(txRecordBean);
    }


    /**
     * @return 首页展示的钱包
     */
    public static WalletBeanNew getMainWallet() {
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

    /**
     * @param needTemp
     * @param beanNew 当前正在操作的钱包此处可以缓存钱包的所有信息，换出位置是内存
     * @param password          密码
     * @param ksp               路径
     * @param callback          回掉
     */
    public static void checkCurrentWalletPassword(boolean needTemp, WalletBeanNew beanNew, final String password, final String ksp, final OnCheckWalletPasswordCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //创建钱包
                    Credentials credentials = loadCredentials(beanNew.getLight(), password, new File(ksp));
                    ECKeyPair keyPair = credentials.getEcKeyPair();
                    beanNew.setPrivateKey(keyPair.getPrivateKey().toString(16));//私钥
                    beanNew.setPublic_key(keyPair.getPublicKey().toString(16));//公钥
                    beanNew.setAddress("0x" + Keys.getAddress(keyPair)); //地址
                    if (needTemp) {
                        beanNew.setPassword(password); //密码,发起交易的时候，需要内存的中的密码
                    }else {
                        beanNew.setPassword("");
                    }
                    beanNew.setKeystore(ksp);
                    mHandler.post(() -> callback.onCheckWalletPasswordSuccess(beanNew));
                } catch (IOException | CipherException | NullPointerException e) {
                    mHandler.post(() -> callback.onCheckWalletPasswordFailure(e.getMessage()));
                }
            }
        }.start();

    }

    /**
     * 检查旧表是否存在   WalletBean
     */
    public static boolean checkWalletBeanTableExist() {
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
     * 检查旧表是否存在   TxRecordBean
     */
    public static boolean checkTxRecordBeanTableExist() {
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
    public static void migrateWalletDb(List<WalletBean> walletBeanOlds) {
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
    public static Credentials loadCredentialsByP(int p, String password, File file) throws IOException, CipherException {
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
    public static Credentials loadCredentials(boolean light, String password, File file) throws IOException, CipherException {
        Credentials credentials;
        if (light) {
            credentials = WalletUtils.loadCredentials(password, file);
        } else {
            credentials = MyWalletUtils.loadCredentials(password, file);
        }
        return credentials;
    }

}
