package org.ionchain.wallet.manager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.fast.lib.utils.LibSPUtils;

import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.callback.OnCreateWalletCallback;
import org.ionchain.wallet.comm.api.myweb3j.MnemonicUtils;
import org.ionchain.wallet.comm.api.myweb3j.SecureRandomUtils;
import org.ionchain.wallet.comm.constants.Comm;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;

import static org.ionchain.wallet.comm.api.ApiWalletManager.DEF_WALLET_PATH;
import static org.ionchain.wallet.comm.api.myweb3j.MnemonicUtils.generateMnemonic;
import static org.ionchain.wallet.utils.RandomUntil.getNum;
import static org.web3j.crypto.Hash.sha256;

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
    private Context mContext;
    private String keystoreDir = Environment.getExternalStorageDirectory().toString() + "/ionchain/keystore";
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom(); //"https://ropsten.etherscan.io/token/0x92e831bbbb22424e0f22eebb8beb126366fa07ce"

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
            String keystore = dest + "/" + bip39Wallet.getFilename();
            String c = bip39Wallet.getMnemonic();
            Log.i("cccc", "createBip39Wallet: " + c);
            WalletBean walletBean = loadWalletFile(password, bip39Wallet.getFilename());
            walletBean.setKeystore(keystore);
            walletBean.setName(name);
            walletBean.setIconIdex(getNum(7));//设置随机的头像
            callback.onCreateSuccess(walletBean);
        } catch (Exception e) {
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
        bean.setAddress(Keys.getAddress(keyPair)); //地址
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
            Integer nameIndex = (Integer) LibSPUtils.get(mContext, Comm.LOCAL_SAVE_WALLET_INDEX, 1);
            String walletname = "新增钱包" + nameIndex;
            BigInteger key = new BigInteger(privateKey, 16);
            ECKeyPair keyPair = ECKeyPair.create(key);
            String[] tuple = getAccountTuple(keyPair);
            wallet.setPrivateKey(tuple[0]);
            wallet.setPublickey(tuple[1]);
            wallet.setAddress(tuple[2]);
            wallet.setName(walletname);
            wallet.setPassword(passwrd);
            String keystore = WalletUtils.generateWalletFile(wallet.getPassword(), keyPair, new File(DEF_WALLET_PATH), false);
            keystore = DEF_WALLET_PATH + "/" + keystore;
            wallet.setKeystore(keystore);
            callback.onCreateSuccess(wallet);
        } catch (CipherException | IOException e) {
            callback.onCreateFailure(e.getMessage());
        }
    }


    /**
     * @param address 钱包地址
     */
    public void getAccountBalance(String address) {

        BigInteger balance;
        try {
            balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
            BigDecimal decimal = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
            System.out.println(decimal);
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
