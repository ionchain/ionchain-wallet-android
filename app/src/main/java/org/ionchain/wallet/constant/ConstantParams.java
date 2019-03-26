package org.ionchain.wallet.constant;

/**
 * USER: binny
 * DATE: 2018/9/26
 * 描述: 常量类
 */
public final class ConstantParams {
    /**
     * 钱包实体类
     */
    public static final String SERIALIZABLE_DATA_WALLET_BEAN = "wallet_bean";
    public static final int REQUEST_MODIFY_WALLET_PWD = 100;


    public static final String SERIALIZABLE_DATA = "serializable_data";
    public static final String SERIALIZABLE_DATA1 = "serializable_data1";

    public static final String DB_NAME = "ionchainwallet";
    public static final String SPLIT = ",";
    public static final String IS = "=";
    public static final String JUMP_PARM_ISADDMODE = "isaddmode";
    public static final String user = "user_model";
    public static final String FROM_WELCOME = "welcome";

    public static final int FROM_SCAN = 999;

    public static final int DECODE = 1;
    public static final int DECODE_FAILED = 2;
    public static final int DECODE_SUCCEEDED = 3;
    public static final int LAUNCH_PRODUCT_QUERY = 4;
    public static final int QUIT = 5;
    public static final int RESTART_PREVIEW = 6;
    public static final int RETURN_SCAN_RESULT = 7;
    public static final int FLASH_OPEN = 8;
    public static final int FLASH_CLOSE = 9;
    public static final int REQUEST_IMAGE = 10;
    public static final String CODED_CONTENT = "result";
    public static final String CODED_BITMAP = "codedBitmap";


    /*传递的zxingconfing*/

    public static final String INTENT_ZXING_CONFIG = "zxingConfig";

    public static final String SERVER_PROTOCOL_KEY = "title_name";
    public static final String SERVER_PROTOCOL_VALUE = "离子链钱包服务协议";
    public static final String PICTURE_FILE_NAME = "ionchainAddress";


    public static final int SEEK_BAR_MAX_VALUE_101_GWEI = 100;//seekbar 本身的最大值 Gwei
    public static final int SEEK_BAR_MIN_VALUE_1_GWEI = 1;
}
