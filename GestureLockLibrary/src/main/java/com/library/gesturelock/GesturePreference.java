package com.library.gesturelock;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 项目名称：MeshLed_dxy
 * 类描述：
 * 创建人：oden
 * 创建时间：2016/7/25 19:05
 */
public class GesturePreference {
    private Context context;
    private final String fileName = "gesturelock";
    private String nameTable = "gesturelock.nameTable";

    public final static String FLAG_DECRYPTION = "***";//是否加过密的标记

    /**
     * xml文件数据保存加解密接口
     */
    public interface OnDecryptionListener{
        /**
         * 加密
         * @param noCipher
         * @return
         */
        String encrypt(String noCipher);

        /**
         * 解密
         * @param cipher
         * @return
         */
        String decrypt(String cipher);
    }
    private OnDecryptionListener decryptionListener;

    public void setDecryptionListener(OnDecryptionListener decryptionListener) {
        this.decryptionListener = decryptionListener;
    }

    public GesturePreference(Context context, int nameTableId) {
        this.context = context;
        if (nameTableId != -1)
            this.nameTable = nameTable + nameTableId;
    }

    public void WriteStringPreference(String data) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String cipher = "null";
        if ("null".equals(data)||data==null){
            cipher = "null";
        } else {
            if (decryptionListener!=null){
                if (decryptionListener.encrypt(data)!=null){
                    cipher = FLAG_DECRYPTION + decryptionListener.encrypt(data);
                } else {
                    cipher = data;
                }
            } else {
                cipher = data;
            }
        }
        editor.putString(nameTable, cipher);
        editor.apply();
    }

    public String ReadStringPreference() {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String noCipher = preferences.getString(nameTable, "null");
        if ((FLAG_DECRYPTION + "null").equals(noCipher)||"null".equals(noCipher)){
            noCipher = "null";
        } else {
            if (decryptionListener!=null){
                if (noCipher.contains(FLAG_DECRYPTION)){
                    noCipher = noCipher.replace(FLAG_DECRYPTION,"");
                    if (decryptionListener.decrypt(noCipher)!=null){
                        noCipher = decryptionListener.decrypt(noCipher);
                    } else {
                        noCipher = "null";
                    }
                }
            }
        }
        return noCipher;
    }

}
