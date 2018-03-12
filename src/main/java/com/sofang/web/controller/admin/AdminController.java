package com.sofang.web.controller.admin;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.sofang.base.ResponseEntity;
import com.sofang.base.StatusCode;
import com.sofang.service.QiNiuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.sofang.web.dto.QiNiuPutRet;

import java.io.IOException;
import java.io.InputStream;

/**
 * 后台管理
 *
 * @since 1.0
 *
 * @version 1.0
 *
 * @author gegf
 */
@Controller
public class AdminController {
    @Autowired
    private QiNiuService qiNiuService;

    @Autowired
    private Gson gson;

    @GetMapping("/admin/center")
    public String adminCenterPage(){
        return "admin/center";
    }

    @GetMapping("/admin/welcome")
    public String welcomePage(){
        return "admin/welcome";
    }

    @GetMapping("/admin/login")
    public String login(){
        return "admin/login";
    }

    @GetMapping("/admin/add/house")
    public String addHousePage(){
        return "admin/house-add";
    }

    /**
     * 上传图片接口
     * @param file
     * @return
     */
    @PostMapping(value = "admin/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.createByErrorCodeMessage(StatusCode.NOT_VALID_PARAM);
        }

        String fileName = file.getOriginalFilename();

        try {
            InputStream inputStream = file.getInputStream();
            Response response = qiNiuService.uploadFile(inputStream);
            if (response.isOK()) {
                QiNiuPutRet ret = gson.fromJson(response.bodyString(), QiNiuPutRet.class);
                return ResponseEntity.createBySuccessMessage("success");
            } else {
                return ResponseEntity.createByErrorCodeMessage(StatusCode.INTERNAL_SERVER_ERROR);
            }

        } catch (QiniuException e) {
            Response response = e.response;
            try {
                return ResponseEntity.createByErrorCodeMessage(response.statusCode, response.bodyString());
            } catch (QiniuException e1) {
                e1.printStackTrace();
                return ResponseEntity.createByErrorCodeMessage(StatusCode.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            return ResponseEntity.createByErrorCodeMessage(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }
}