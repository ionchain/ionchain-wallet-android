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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.web3j.crypto.Hash.sha256;


public class ApiWalletManager {
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    public static ResponseModel<String> createWallet(WalletCreateRquest request) {
        ResponseModel<String> responseModel = new ResponseModel<String>();
        responseModel.data = "123123123";
        responseModel.code = String.valueOf(ApiConstant.ApiErrCode.SUCCESS.getDesc());
        responseModel.msg = String.valueOf(ApiConstant.ApiErrMsg.SUCCESS.getDesc());
        return responseModel;
    }

    public static void test(Activity main) {
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
           /* String pass = "testwallet1984testwallet1984";
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
            printtest(wallet.getFilename());*/
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
        }

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


}
