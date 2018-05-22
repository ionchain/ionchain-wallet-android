package org.ionchain.wallet.comm.api.constant;

import android.app.Application;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.constants.Global;

public class ApiConstant {

    /**
     * api调用地址URI
     */
    public enum ApiUri {
        URI_LOGIN("/login");

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
}
