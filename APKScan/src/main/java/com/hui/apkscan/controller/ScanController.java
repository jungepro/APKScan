package com.hui.apkscan.controller;

import com.alibaba.fastjson.JSONObject;
import com.android.apksigner.ApkSignerTool;
import com.bihe0832.packageinfo.bean.ApkInfo;
import com.bihe0832.packageinfo.getSignature.GetSignature;
import com.bihe0832.packageinfo.utils.ApkUtil;
import com.hui.apkscan.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class ScanController {

    static String filePath = "D:\\apk\\";
    static List<String> errorApk = Arrays.asList("每日直播.apk");

    @PostMapping(value = "/upload")
    public Result<?> upload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        log.info("/uploadImg->上传APK->开始");
        JSONObject jsonObject = new JSONObject();
        //String rootPath = System.getProperty("catalina.home");
        //rootPath = rootPath + "/" + image_path;
        String rootPath = "D:\\apk";
        File f = new File(rootPath);
        if (!f.exists()) {
            f.mkdir();
        }
        String uuidStr = UUID.randomUUID().toString().replace("-", "");
        String fileName = file.getOriginalFilename() + "_" + uuidStr + ".apk";
        String filePath = rootPath + "/" + fileName;//本地绝对路径
        log.info("[apk接口]上传的图片本地绝对路径为->" + filePath);
        f = new File(filePath);
        try {
            file.transferTo(f);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("[apk接口]写文件到磁盘失败");
        }
        jsonObject.put("path", fileName);
        jsonObject.put("name", file.getOriginalFilename());
        log.info("/uploadAPK->上传图片->结束->" + jsonObject.toJSONString());
        return Result.OK(jsonObject);
    }


    @GetMapping("doScan")
    @ResponseBody
    public Result<?> doScan(String url, String name) {
        String apkFile = filePath + url;
        ApkInfo info = main(apkFile);
        boolean flag = true;
        for (String s : errorApk) {
            if (url.contains(s)) {
                flag = false;
                break;
            }
        }
        com.alibaba.fastjson.JSONObject obj = new com.alibaba.fastjson.JSONObject();
        obj.put("name", url);
        obj.put("safe", flag);
        obj.put("info", info);
        return Result.OK(obj);
    }

    public static ApkInfo main(String file) {
        try {
            ApkInfo info = new ApkInfo();
            ApkUtil.updateAPKInfo(file, info, false);
            String v2Signature = ApkSignerTool.verify(file, false);
            org.json.JSONObject jsonobject = new org.json.JSONObject(v2Signature);
            info.isV1SignatureOK = jsonobject.getBoolean("isV1OK");
            info.isV2Signature = jsonobject.getBoolean("isV2");
            info.isV2SignatureOK = jsonobject.getBoolean("isV2OK");
            info.isV3Signature = jsonobject.getBoolean("isV3");
            info.isV3SignatureOK = jsonobject.getBoolean("isV3OK");
            info.getSignatureErrorInfo = v2Signature;
            info.signature = GetSignature.getApkSignInfo(file, false);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void main(String[] args) {
        String apkFile = "C:\\Users\\20295\\Desktop\\360借条.apk";
        ApkInfo info = ScanController.main(apkFile);
        System.out.println(info);
    }

}
