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


    /**
     * 传递钱包对象
     */
    public static final String PARCELABLE_WALLET_BEAN = "parcelable_data";
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


    public static final int SEEK_BAR_MAX_VALUE_100_GWEI = 100;//seekbar 本身的最大值 Gwei
    public static final int SEEK_BAR_MIN_VALUE_1_GWEI = 1;//最小值
    public static final int SEEK_BAR_SRART_VALUE = 30;//进度条的起始值

    public static final String CURRENT_ADDRESS = "address";
    public static final String CURRENT_KSP = "ksp";

    public static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    public static final int REQUEST_STORAGE_PERMISSIONS = 2;


    /*
    * 版本对话框的按钮事件
    * */
    public static final char VERSION_TAG_CHECK_FOR_UPDATE = 0;
    public static final char VERSION_TAG_DOWNLOAD = 1;



    public static final String INTENT_PARAME_WALLET_ADDRESS = "msg";




    /**
     * WEB 连接
     */

    public static final String URL_REQUEST_TYPE = "url_tag";//协议
    public static final char URL_TAG_PROTOCOL = 0;//协议
    public static final char URL_TAG_ABOUT_US = 1;//关于我们


    public static final int QRCODE_BIND_DEVICE = 10;

    public static final String DOWNLOAD_MUST_UPDATE_NO = "0";//  可以取消对话框
    public static final String DOWNLOAD_MUST_UPDATE_YES = "1";//   不可取消


}
