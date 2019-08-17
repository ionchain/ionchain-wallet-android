package org.ionc.ionclib.bean;

import java.io.Serializable;

/**
 * 解析以ks方式导入钱包
 *
 */
public class KeystoreBean implements Serializable {

    /**
     * address : 58e49e04b60d62f15240b62b488ace310fd63eef
     * crypto : {"cipher":"aes-128-ctr","cipherparams":{"iv":"9d91bdc999e5d6ac2635d5114a355a0f"},"ciphertext":"7d73a4b9b2d64e3d112323026716edc00d42221b79848c655c9bbe336e381098","kdf":"scrypt","kdfparams":{"dklen":32,"n":65536,"p":1,"r":8,"salt":"32db95eb771a3b70b5e89474d5ca372f7fe28652422245c54bd211c4fec8731c"},"mac":"c5fc9307c88e5b45b0cfe9234433ae9743a52d261575b9572dd057b8270d312d"}
     * id : 9d52f5d4-d46c-4cdb-9416-e61c52167e97
     * version : 3
     */

    private String address;
    private CryptoBean crypto;
    private String id;
    private int version;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CryptoBean getCrypto() {
        return crypto;
    }

    public void setCrypto(CryptoBean crypto) {
        this.crypto = crypto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public static class CryptoBean implements Serializable {
        /**
         * cipher : aes-128-ctr
         * cipherparams : {"iv":"9d91bdc999e5d6ac2635d5114a355a0f"}
         * ciphertext : 7d73a4b9b2d64e3d112323026716edc00d42221b79848c655c9bbe336e381098
         * kdf : scrypt
         * kdfparams : {"dklen":32,"n":65536,"p":1,"r":8,"salt":"32db95eb771a3b70b5e89474d5ca372f7fe28652422245c54bd211c4fec8731c"}
         * mac : c5fc9307c88e5b45b0cfe9234433ae9743a52d261575b9572dd057b8270d312d
         */

        private String cipher;
        private CipherparamsBean cipherparams;
        private String ciphertext;
        private String kdf;
        private KdfparamsBean kdfparams;
        private String mac;

        public String getCipher() {
            return cipher;
        }

        public void setCipher(String cipher) {
            this.cipher = cipher;
        }

        public CipherparamsBean getCipherparams() {
            return cipherparams;
        }

        public void setCipherparams(CipherparamsBean cipherparams) {
            this.cipherparams = cipherparams;
        }

        public String getCiphertext() {
            return ciphertext;
        }

        public void setCiphertext(String ciphertext) {
            this.ciphertext = ciphertext;
        }

        public String getKdf() {
            return kdf;
        }

        public void setKdf(String kdf) {
            this.kdf = kdf;
        }

        public KdfparamsBean getKdfparams() {
            return kdfparams;
        }

        public void setKdfparams(KdfparamsBean kdfparams) {
            this.kdfparams = kdfparams;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public static class CipherparamsBean implements Serializable {
            /**
             * iv : 9d91bdc999e5d6ac2635d5114a355a0f
             */

            private String iv;

            public String getIv() {
                return iv;
            }

            public void setIv(String iv) {
                this.iv = iv;
            }
        }

        public static class KdfparamsBean implements Serializable {
            /**
             * dklen : 32
             * n : 65536
             * p : 1
             * r : 8
             * salt : 32db95eb771a3b70b5e89474d5ca372f7fe28652422245c54bd211c4fec8731c
             */

            private int dklen;
            private int n;
            private int p;
            private int r;
            private String salt;

            public int getDklen() {
                return dklen;
            }

            public void setDklen(int dklen) {
                this.dklen = dklen;
            }

            public int getN() {
                return n;
            }

            public void setN(int n) {
                this.n = n;
            }

            public int getP() {
                return p;
            }

            public void setP(int p) {
                this.p = p;
            }

            public int getR() {
                return r;
            }

            public void setR(int r) {
                this.r = r;
            }

            public String getSalt() {
                return salt;
            }

            public void setSalt(String salt) {
                this.salt = salt;
            }
        }
    }
}
