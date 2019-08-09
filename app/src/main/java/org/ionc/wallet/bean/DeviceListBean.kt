package org.ionc.wallet.bean

import java.io.Serializable

/**
 * USER: binny  596928539@qq.com
 * DATE: 2018/9/13
 * 描述: 设备列表
 */
class DeviceListBean : Serializable {
    /**
     * success : 0
     * message : ok
     * data : [{"id":2,"name":"无线智能门锁","cksn":"c9cdcdc9129a7bd5645abc7e58acb35e","system":"Android","created_at":"2018-09-13","image_url":""},{"id":3,"name":"共享单车","cksn":"4eeb01d54c4c11a1480cdabd1497a943","system":"Android","created_at":"2018-09-13","image_url":""}]
     */

    var success: Int = 0
    var message: String? = null
    var data: List<DeviceBean.DataBean>? = null

    override fun toString(): String {
        return "DeviceListBean{" +
                "success=" + success +
                ", message='" + message + '\''.toString() +
                ", data=" + data!!.toString() +
                '}'.toString()
    }

    companion object {


        private const val serialVersionUID = 8905431811048066465L
    }
    //
    //    public static class DataBean implements Serializable {
    //        /**
    //         * id : 2
    //         * name : 无线智能门锁
    //         * cksn : c9cdcdc9129a7bd5645abc7e58acb35e
    //         * system : Android
    //         * created_at : 2018-09-13
    //         * image_url :
    //         */
    //
    //        private int id;
    //        private String name;
    //        private String cksn;
    //        private String system;
    //        private String created_at;
    //        private String image_url;
    //
    //        public int getId() {
    //            return id;
    //        }
    //
    //        public void setId(int id) {
    //            this.id = id;
    //        }
    //
    //        public String getName() {
    //            return name;
    //        }
    //
    //        public void setName(String name) {
    //            this.name = name;
    //        }
    //
    //        public String getCksn() {
    //            return cksn;
    //        }
    //
    //        public void setCksn(String cksn) {
    //            this.cksn = cksn;
    //        }
    //
    //        public String getSystem() {
    //            return system;
    //        }
    //
    //        public void setSystem(String system) {
    //            this.system = system;
    //        }
    //
    //        public String getCreated_at() {
    //            return created_at;
    //        }
    //
    //        public void setCreated_at(String created_at) {
    //            this.created_at = created_at;
    //        }
    //
    //        public String getImage_url() {
    //            return image_url;
    //        }
    //
    //        public void setImage_url(String image_url) {
    //            this.image_url = image_url;
    //        }
    //
    //        @Override
    //        public String toString() {
    //            return "DataBean{" +
    //                    "id=" + id +
    //                    ", name='" + name + '\'' +
    //                    ", cksn='" + cksn + '\'' +
    //                    ", system='" + system + '\'' +
    //                    ", created_at='" + created_at + '\'' +
    //                    ", image_url='" + image_url + '\'' +
    //                    '}';
    //        }
    //    }
}
