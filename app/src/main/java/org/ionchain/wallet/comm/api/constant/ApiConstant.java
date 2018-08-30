package org.ionchain.wallet.comm.api.constant;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.constants.Global;

public class ApiConstant {

    /**
     * api调用地址URI
     */
    public enum ApiUri {
        URI_LOGIN("/user/login"),
        URI_REG("/user/register"),
        URI_EDIT_PASS("/user/updatePassword"),
        URI_SMS_CODE("/sendSms"),
        URI_ATRICLE_ALL("/article/findAll"),
        URI_ATRICLE_VIEW("/article/view"),
        URI_ATRICLE_PRAISE("/article/praise"),
        URI_SYS_INFO("/sys/info");
        private String desc;

        public static ApiErrMsg codeOf(String name) {
            for (ApiErrMsg dos : ApiErrMsg.values()) {
                if (dos.name().equalsIgnoreCase(name)) {
                    return dos;
                }
            }
            return null;
        }

        ApiUri(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 错误提示信息枚举
     */
    public enum ApiErrMsg {
        SUCCESS(Global.mContext.getString(R.string.api_err_msg_success)),
        UNKOWN(Global.mContext.getString(R.string.api_err_msg_unkown));

        private String desc;

        public static ApiErrMsg codeOf(String name) {
            for (ApiErrMsg dos : ApiErrMsg.values()) {
                if (dos.name().equalsIgnoreCase(name)) {
                    return dos;
                }
            }
            return null;
        }

        ApiErrMsg(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 错误码枚举
     */
    public enum ApiErrCode {
        SUCCESS(0),
        UNKOWN(1001);

        private int desc;

        public static ApiErrCode codeOf(String name) {
            for (ApiErrCode dos : ApiErrCode.values()) {
                if (dos.name().equalsIgnoreCase(name)) {
                    return dos;
                }
            }
            return null;
        }

        ApiErrCode(int desc) {
            this.desc = desc;
        }

        public int getDesc() {
            return desc;
        }
    }

    /**
     * 错误提示信息枚举
     */
    public enum WalletMangerErrMsg {
        SUCCESS(Global.mContext.getString(R.string.api_err_msg_success)),
        UNKOWN(Global.mContext.getString(R.string.api_err_msg_unkown));

        private String desc;

        public static WalletMangerErrMsg codeOf(String name) {
            for (WalletMangerErrMsg dos : WalletMangerErrMsg.values()) {
                if (dos.name().equalsIgnoreCase(name)) {
                    return dos;
                }
            }
            return null;
        }

        WalletMangerErrMsg(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 错误码枚举
     */
    public enum WalletManagerErrCode {
        SUCCESS(1000),
        FAIL(1001),
        INIT_ERR(1002),
        UNKOWN(1999);

        private int desc;

        public static WalletManagerErrCode codeOf(String name) {
            for (WalletManagerErrCode dos : WalletManagerErrCode.values()) {
                if (dos.name().equalsIgnoreCase(name)) {
                    return dos;
                }
            }
            return null;
        }

        WalletManagerErrCode(int desc) {
            this.desc = desc;
        }

        public int getDesc() {
            return desc;
        }
    }

    /**
     * 接口码
     */
    public  enum WalletManagerType {
        MANAGER_INIT(0x9001),
        WALLET_IMPORT(0x9002),
        WALLET_CREATE(0x9003),
        WALLET_BALANCE(0x9004),
        WALLET_EDIT_PASS(0x9005),
        WALLET_EXPORT_PRIVATEKEY(0x9006),
        OTHER(0x9999);
        private int desc;

        public static WalletManagerType codeOf(int code) {
            for (WalletManagerType dos : WalletManagerType.values()) {
                if (dos.getDesc()== code) {
                    return dos;
                }
            }
            return null;
        }

        public static WalletManagerType codeOf(String name) {
            for (WalletManagerType dos : WalletManagerType.values()) {
                if (dos.name().equalsIgnoreCase(name)) {
                    return dos;
                }
            }
            return null;
        }

        WalletManagerType(int desc) {
            this.desc = desc;
        }

        public int getDesc() {
            return desc;
        }
    }

}
