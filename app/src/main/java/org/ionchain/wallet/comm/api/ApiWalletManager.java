package org.ionchain.wallet.comm.api;

import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.request.WalletCreateRquest;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

public class ApiWalletManager {

    public static ResponseModel<String> createWallet(WalletCreateRquest request){
        ResponseModel<String> responseModel = new ResponseModel<String>();
        responseModel.data="123123123";
        responseModel.code = String.valueOf(ApiConstant.ApiErrCode.SUCCESS.getDesc());
        responseModel.msg = String.valueOf(ApiConstant.ApiErrMsg.SUCCESS.getDesc());
        return responseModel;
    }

   /* public static void  test(){
        Web3j web3 = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
        Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
    }*/

}
