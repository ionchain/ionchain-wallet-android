package org.ionchain.wallet.comm.api;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.myweb3j.MnemonicUtils;
import org.ionchain.wallet.comm.api.myweb3j.SecureRandomUtils;
import org.ionchain.wallet.comm.api.request.WalletCreateRquest;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.web3j.crypto.Hash.sha256;


public class ApiWalletManager {
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();
    private static final String WALLET_ADDRESS = "https://ropsten.infura.io/JOEnl84Gm76oX0RMUrJB";

    public static ResponseModel<String> createWallet(WalletCreateRquest request) {
        ResponseModel<String> responseModel = new ResponseModel<String>();
        responseModel.data = "123123123";
        responseModel.code = String.valueOf(ApiConstant.ApiErrCode.SUCCESS.getDesc());
        responseModel.msg = String.valueOf(ApiConstant.ApiErrMsg.SUCCESS.getDesc());
        return responseModel;
    }

    public static void test(Activity main) {

//         /storage/emulated/0/Pictures/UTC--2018-05-28T18-54-22.869--dc39f3895c38f5999ba462fc10dfb1f78bdfecf2.json
//        address 0xdc39f3895c38f5999ba462fc10dfb1f78bdfecf2
//        public  4ef416ee8ae89dde959b4f3b7210e9d4cd5382c2e71776274dbd114c544800d60682c39aba41d5d8c65bf6d068ac9b7fbcbcd91dca5ae2733d5932e4c9371ce8
//        private  b71c1f7594fd8718c7ae06ea89c48e2e621990e5736c6f1087f04defa5c251a6

       // createWallet("", main);
//        File file = new File("/storage/emulated/0/wallet");
//        file.mkdir();
        importPrivateKey("b71c1f7594fd8718c7ae06ea89c48e2e621990e5736c6f1087f04defa5c251a6","1234567890xxxx22","/storage/emulated/0/wallet");
        return;

        /*try {
            int REQUEST_EXTERNAL_STORAGE = 1;
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            int permission = ActivityCompat.checkSelfPermission(main, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        main,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
            String info = readAssetsTxt(main, "en-mnemonic-word-list");
            String[] a = info.split("\n");
            printtest(a.length+"");
            List<String> arr = new ArrayList<String>();
            for (String word:
                    a) {
                arr.add(word);
            }
            MnemonicUtils.WORD_LIST = arr;
//            List<String> a = populateWordList();
//            printtest(a.size() + "");
            String pass = "1234567890xxxx22";
            String filePath = Environment.getExternalStorageDirectory().toString() + "/Pictures";
            Bip39Wallet wallet = generateBip39Wallet(pass,new File(filePath));
            printtest(wallet.getFilename());
            printtest(wallet.getMnemonic());
                        printtest(wallet.getMnemonic());
            Credentials credentials = WalletUtils.loadBip39Credentials(pass,
                    wallet.getMnemonic());
            //钱包地址
            printtest(credentials.getAddress());
            //公钥16进制字符串表示
            printtest(credentials.getEcKeyPair().getPublicKey().toString(16));
            //私钥16进制字符串表示
            printtest(credentials.getEcKeyPair().getPrivateKey().toString(16));

            credentials = WalletUtils.loadCredentials(pass, filePath + "/"+wallet.getFilename() );
            printtest(credentials.getAddress());
            printtest(credentials.getEcKeyPair().getPublicKey().toString(16));
            printtest(credentials.getEcKeyPair().getPrivateKey().toString(16));
            String adderess = credentials.getAddress();
            getBlannce(adderess);

           *//* String pass = "testwallet1984testwallet1984";
            Web3j web3 = Web3jFactory.build(new HttpService("https://ropsten.infura.io/JOEnl84Gm76oX0RMUrJB"));  // defaults to http://localhost:8545/
            Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
//            String keyStoreDir = WalletUtils.getDefaultKeyDirectory();
            String filePath = Environment.getExternalStorageDirectory().toString() + "/Pictures";
//            String fileName = WalletUtils.generateNewWalletFile("password",new File(filePath),false);
            Log.e("wallet", "生成keyStore文件的默认目录：" + filePath +"  clientVersion "+clientVersion);
//            //通过密码及keystore目录生成钱包
            Bip39Wallet wallet = WalletUtils.generateBip39Wallet(pass, new File(filePath));
//            //keyStore文件名
            printtest(wallet.getFilename());*//*
//            //12个单词的助记词
//            printtest(wallet.getMnemonic());
//            Credentials credentials = WalletUtils.loadBip39Credentials(pass,
//                    wallet.getMnemonic());
//            //钱包地址
//            printtest(credentials.getAddress());
//            //公钥16进制字符串表示
//            printtest(credentials.getEcKeyPair().getPublicKey().toString(16));
//            //私钥16进制字符串表示
//            printtest(credentials.getEcKeyPair().getPrivateKey().toString(16));
//            credentials = WalletUtils.loadCredentials(pass, keyStoreDir + "/"+wallet.getFilename() );
//            printtest(credentials.getAddress());
//            printtest(credentials.getEcKeyPair().getPublicKey().toString(16));
//            printtest(credentials.getEcKeyPair().getPrivateKey().toString(16));

        } catch (Exception e) {
            Log.e("wallet", "", e);
        }*/

    }

    public static void testImport() {

    }


    public static void printtest(String info) {
        Log.e("wallet", info);
    }

    private static List<String> populateWordList() {
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("en-mnemonic-word-list.txt");
        printtest(url.toString());
        //  printtest(url.toURI().getSchemeSpecificPart());
        try {
            String path = url.toURI().getSchemeSpecificPart();
            printtest(path);
            File file = new File(path);
            printtest(file.exists() + "");
            return readAllLines(path);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<String> readAllLines(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        List<String> data = new ArrayList<String>();
        for (String line; (line = br.readLine()) != null; ) {
            data.add(line);
        }
        return data;
    }

    public static Bip39Wallet generateBip39Wallet(String password, File destinationDirectory)
            throws CipherException, IOException {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);

        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        ECKeyPair privateKey = ECKeyPair.create(sha256(seed));

        String walletFile = WalletUtils.generateWalletFile(password, privateKey, destinationDirectory, false);

        return new Bip39Wallet(walletFile, mnemonic);
    }

    public static String readAssetsTxt(Context context, String fileName) {
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
     * c
     *
     * @param main
     */
    public static void createWallet(String pass, Activity main) {

        try {
            int REQUEST_EXTERNAL_STORAGE = 1;
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            int permission = ActivityCompat.checkSelfPermission(main, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        main,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
            String info = readAssetsTxt(main, "en-mnemonic-word-list");
            String[] a = info.split("\n");
            printtest(a.length + "");
            List<String> arr = new ArrayList<String>();
            for (String word :
                    a) {
                arr.add(word);
            }
            Credentials credentials = null;
            MnemonicUtils.WORD_LIST = arr;
//            List<String> a = populateWordList();
//            printtest(a.size() + "");
            pass = "1234567890xxxx22";
            String filePath = Environment.getExternalStorageDirectory().toString() + "/Pictures";
            Bip39Wallet wallet = generateBip39Wallet(pass, new File(filePath));
  /*          printtest(wallet.getFilename());
            printtest(wallet.getMnemonic());
                        printtest(wallet.getMnemonic());
            Credentials credentials = WalletUtils.loadBip39Credentials(pass,
                    wallet.getMnemonic());
            //钱包地址
            printtest(credentials.getAddress());
            //公钥16进制字符串表示
            printtest(credentials.getEcKeyPair().getPublicKey().toString(16));
            //私钥16进制字符串表示
            printtest(credentials.getEcKeyPair().getPrivateKey().toString(16));*/

            credentials = WalletUtils.loadCredentials(pass, filePath + "/" + wallet.getFilename()); ///storage/emulated/0/Pictures/UTC--2018-05-28T18-50-23.360--7ee4da5bb8ec79cc44421351803adba143a93ace.json
            printtest(filePath + "/" + wallet.getFilename());
            printtest("address " + credentials.getAddress());
            printtest("xx  " + credentials.getEcKeyPair().getPublicKey().toString(16));
            printtest("oo  " + credentials.getEcKeyPair().getPrivateKey().toString(16));


        } catch (Exception e) {
            Log.e("wallet", "", e);
        }

    }


    public static void getBlannce(String address) {
        Web3j web3 = Web3jFactory.build(new HttpService(WALLET_ADDRESS));  // defaults to http://localhost:8545/
        try {
            EthGetBalance ethGetBalance = web3
                    .ethGetBalance("0x0df14334e094acc0197d52a415d799c2b8a3b04b", DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
            Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
            BigInteger wei = ethGetBalance.getBalance();
            printtest(web3ClientVersion.toString() + "  xxxxx  " + wei.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 导入私钥
     *
     * @param privateKey 私钥
     * @param password   密码
     * @param directory  存储路径 默认测试网络WalletUtils.getTestnetKeyDirectory() 默认主网络 WalletUtils.getMainnetKeyDirectory()
     */
    private static void importPrivateKey(String privateKey, String password, String directory) {
        try {
            BigInteger privateKeyBig = new BigInteger(privateKey,16);
            ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyBig);
            String keystoreName = WalletUtils.generateWalletFile(password,
                    ecKeyPair,
                    new File(directory),
                    true);
            printtest("keystore name " + keystoreName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("wallet","importPrivateKey Err",e);
        }
    }

}
