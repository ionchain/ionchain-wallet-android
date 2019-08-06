package org.ionc.wallet.sdk;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.daohelper.EntityManager;
import org.ionc.wallet.greendaogen.TxRecordBeanDao;

import java.util.Collections;
import java.util.List;

import static org.ionc.wallet.sdk.IONCSDK.mDaoSession;

public class IONCTxRecords {

    /**
     * 通过钱包地址查询钱包
     * <p>
     * 转入
     *
     * @param timeStemp 转入地址
     * @return 该钱包收到的转账记录
     */
    public static TxRecordBean getTxRecordBeansByTimes(String timeStemp) {
        return mDaoSession.getTxRecordBeanDao().queryBuilder().where(TxRecordBeanDao.Properties.Tc_in_out.eq(timeStemp)).unique();
    }


    public static boolean notExist(String hash) {
        List<TxRecordBean> list = mDaoSession.getTxRecordBeanDao().queryBuilder().where(TxRecordBeanDao.Properties.Hash.eq(hash)).list();
        int size = list.size();
        return size == 0;
    }


    /**
     * 更新交易记录
     *
     * @param txRecordBean
     */
    public static void updateTxRecordBean(TxRecordBean txRecordBean) {
        EntityManager.getInstance().getTxRecordBeanDao(mDaoSession).update(txRecordBean);
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
    public static List<TxRecordBean> getTxRecordBeanInByAddress(int offset, String address, int limit, int num) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        return dao.queryBuilder().where(TxRecordBeanDao.Properties.To.eq(address))
                .offset(offset * num).limit(limit).list();
    }
    /**
     * 保存钱包,保存前,检查数据库是否存在钱包,如果没有则将该钱包设置为首页展示钱包
     *
     * @param txRecordBean 钱包
     * @return
     */
    public static void saveTxRecordBean(TxRecordBean txRecordBean) {

        EntityManager.getInstance().getTxRecordBeanDao(mDaoSession).insertOrReplace(txRecordBean);
    }
    /**
     * 把当前钱包的相关记录，都找出来
     *
     * @param address 当前钱包相关的交易记录
     * @return 交易总数
     */
    public  static long txRecordItemCountAllByAddress(String address) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        return dao.queryBuilder().whereOr(TxRecordBeanDao.Properties.From.eq(address), TxRecordBeanDao.Properties.To.eq(address)).count();
    }

    /**
     * @param address 当前钱包相关的交易记录
     * @return 交易总数
     */
    public  static long txRecordItemCountOutByAddress(String address) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        return dao.queryBuilder().where(TxRecordBeanDao.Properties.From.eq(address)).count();
    }

    /**
     * @param address 当前钱包相关的交易记录
     * @return 交易总数
     */
    public  static long txRecordCountItemInByAddress(String address) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        return dao.queryBuilder().where(TxRecordBeanDao.Properties.To.eq(address)).count();
    }

    /**
     * 分页加载数据
     * 先选出所有数据，然后偏移，然后截取
     *
     * @param address        钱包地址
     * @param offset         偏移量
     * @param perPageDataNum 取出数量，即每页现实的数量
     * @return 当页交易记录
     */
    public  static List<TxRecordBean> txRecordAllByAddress(String address, long offset, long perPageDataNum) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        List<TxRecordBean> list = dao.queryBuilder().whereOr(TxRecordBeanDao.Properties.From.eq(address), TxRecordBeanDao.Properties.To.eq(address))
                .offset((int) offset)
                .limit((int) perPageDataNum)
                .list();
        Collections.reverse(list);
        return list;
    }

    /**
     * 分页加载数据
     *
     * @param blockNum
     * @param address
     * @param offset
     * @param perPageDataNum
     * @return 当页交易记录
     */
    public  static List<TxRecordBean> txRecordAllByBlockNumber(String blockNum, String address, long offset, long perPageDataNum) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        List<TxRecordBean> list = dao.queryBuilder()
                .where(TxRecordBeanDao.Properties.BlockNumber.eq(blockNum), TxRecordBeanDao.Properties.From.eq(address))
//                .offset((int) offset)
//                .limit((int) perPageDataNum)
                .list();
        List<TxRecordBean> list1 = dao.queryBuilder()
                .where(TxRecordBeanDao.Properties.BlockNumber.eq(blockNum), TxRecordBeanDao.Properties.To.eq(address))
//                .offset((int) offset)
//                .limit((int) perPageDataNum)
                .list();
        list.addAll(list1);
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
    public  static List<TxRecordBean> txRecordOutByAddress(String address, long offset, long perPageNum) {
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
    public  static List<TxRecordBean> txRecordInByAddress(String address, long offset, long perPageNum) {
        TxRecordBeanDao dao = mDaoSession.getTxRecordBeanDao();
        List<TxRecordBean> list = dao.queryBuilder().where(TxRecordBeanDao.Properties.To.eq(address))
                .offset((int) offset)
                .limit((int) perPageNum)
                .list();
        Collections.reverse(list);
        return list;
    }
}
