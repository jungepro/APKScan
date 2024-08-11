package com.hui.apkscan;

import com.android.apksigner.ApkSignerTool;
import com.bihe0832.packageinfo.bean.ApkInfo;
import com.bihe0832.packageinfo.getSignature.GetSignature;
import com.bihe0832.packageinfo.utils.ApkUtil;
import org.json.JSONObject;

public class test {

    public static void main(String[] args) throws Exception {
        //  Main.main(new String[]{"C:\\Users\\20295\\Desktop\\360借条.apk"});
        String file = "C:\\Users\\20295\\Desktop\\360借条.apk";
        ApkInfo info = new ApkInfo();

        try {
            ApkUtil.updateAPKInfo(file, info, false);
        } catch (Exception var5) {
            return;
        }

        String v2Signature = ApkSignerTool.verify(file, false);
        JSONObject jsonobject = new JSONObject(v2Signature);
        info.isV1SignatureOK = jsonobject.getBoolean("isV1OK");
        info.isV2Signature = jsonobject.getBoolean("isV2");
        info.isV2SignatureOK = jsonobject.getBoolean("isV2OK");
        info.isV3Signature = jsonobject.getBoolean("isV3");
        info.isV3SignatureOK = jsonobject.getBoolean("isV3OK");
        info.getSignatureErrorInfo = v2Signature;
        info.signature = GetSignature.getApkSignInfo(file, false);
        System.out.println(info);
    }


}
