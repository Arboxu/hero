package com.arbo.hero.util;

import android.util.Log;

import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密类
 * Created by Administrator on 2016/10/7.
 */
public class EncrypAES {
    private final String TAG = "EncryAES";

    /**
     * Cipher负责完成加密或解密工作
     */
    private Cipher c;

    /**
    * 用于保存加密后的结果
    * */
    private byte[] cipherByte ;

    /**
     * 用于生产秘钥
     *
     */
    private final String SECRETKEY = "AESDemo";
    private SecretKeySpec deskey;

    public EncrypAES(){
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        try{
            deskey = new SecretKeySpec(getRawKey(SECRETKEY.getBytes()),"AES");
            //生成Cipher对象,指定其支持的DES算法
            c = Cipher.getInstance("AES");
        }catch(Exception e){
            Log.e(TAG, "EnrypAES construct failed.",e);
        }
    }


    /**
     * 对字符串加密
     *
     * @param str
     * @return
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private byte[] Encrytor(String str) throws InvalidKeyException,
            IllegalBlockSizeException,BadPaddingException{
        //根据秘钥，对Cipher对象进行加密，ENCRYPT_MODE表示加密模式
        c.init(Cipher.ENCRYPT_MODE,deskey);
        byte[] src  = str.getBytes();
        cipherByte = c.doFinal(src);//加密，结果保存在cipherByte中
        return cipherByte;
    }


    /**
     * 对字符串解密
     *
     * @param buff
     * @return
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private byte[] Decrytor(byte[] buff) throws InvalidKeyException,
            IllegalBlockSizeException,BadPaddingException{
        //根据秘钥，对Cipher对象进行加密，ENCRYPT_MODE表示解密模式
        c.init(Cipher.DECRYPT_MODE,deskey);
        cipherByte = c.doFinal(buff);
        return cipherByte;
    }


    /**
     * 对字符串进行加密
     * @param string 要加密的字符串
     * @return 加密后的字符串
     */
    public String EncryptorString(String string){
        String result = null;
        byte[] encontent;
        try {
            encontent = Encrytor(string);
            result = toHex(encontent);
        }catch (InvalidKeyException e){
            Log.e(TAG, "EncryptorString",e);
        }catch (IllegalBlockSizeException e){
            Log.e(TAG, "EncryptorString",e);
        }catch (BadPaddingException e){
            Log.e(TAG, "EncryptorString",e);
        }
        return result;
    }

    /**
     * 对字符串进行解密
     * @param string 要解密的字符串
     * @return 解密后的字符串
     */

    public String DecryptorString(String string){
        byte[] cryptcontent=toByte(string);
        byte[] decontent;
        String result = null;
        try{
            decontent = Decrytor(cryptcontent);
            result = new String(decontent);
        }catch (InvalidKeyException e){
            Log.e(TAG, "EncryptorString",e);
        }catch (IllegalBlockSizeException e){
            Log.e(TAG, "EncryptorString",e);
        }catch (BadPaddingException e){
            Log.e(TAG, "EncryptorString",e);
        }
        return result;
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    private static String toHex(String encontent) {
        return toHex(encontent.getBytes());
    }
    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }


    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        /**
         * 这一句很关键。
         * <br>网上有的代码是这一句SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
         * <br>在getInstance函数中少了一个String参数。
         * 这个参数是不能少的，因为如果少了这个参数的话，生成的密钥是随机的，而加密和解密必须是用同样的密钥的
         * 所以会出现不能解密的问题（总是抛出BadPaddingException异常）。而且这种方法中，对比少了一个参数的加密结果，
         * 会发现每一次加密的结果都是不一样的。
         * <br>而用下面的语句得到的密钥加密，同样的字符串任何时候得到的加密结果都是一样的。
         */
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }
}
