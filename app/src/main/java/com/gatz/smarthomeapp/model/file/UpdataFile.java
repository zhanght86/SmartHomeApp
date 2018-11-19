package com.gatz.smarthomeapp.model.file;

import android.text.TextUtils;

import com.gatz.smarthomeapp.bean.Profile;
import com.gatz.smarthomeapp.bean.Result;
import com.gatz.smarthomeapp.bean.VersionBean;
import com.gatz.smarthomeapp.bean.VersionsInfo;
import com.gatz.smarthomeapp.utils.JsonParser;
import com.gatz.smarthomeapp.utils.UrlUtils;
import com.gatz.smarthomeapp.utils.Utils;

import org.apache.http.util.EncodingUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouh on 2017/12/27.
 * 更新模式 type-version/
 */
public class UpdataFile {
    private static final String fileName = "/dnake/cfg/app_version.txt";

    public static void initUpdataFile() {
        File file = new File(fileName);
        if (file.exists()) {
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<VersionBean> list = new ArrayList<>();
        for(int i = 0; i < 7; i ++) {
            VersionBean versionBean = new VersionBean();
            if (i == 0) {
                versionBean.setAppType(Utils.LSR_HOME);
            } else if (i == 1) {
                versionBean.setAppType(Utils.LSR_CTRL);
            } else if (i == 2) {
                versionBean.setAppType(Utils.LSR_AIR);
            } else if (i == 3) {
                versionBean.setAppType(Utils.LSR_ENVI);
            } else if (i == 4) {
                versionBean.setAppType(Utils.LSR_MSG);
            } else if (i == 5) {
                versionBean.setAppType(Utils.LSR_TALK);
            } else if (i == 6) {
                versionBean.setAppType(Utils.LSR_SECURITY);
            }
            versionBean.setVersion("1.0.0");
            list.add(versionBean);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String res = objectMapper.writeValueAsString(list);
            wirteUpdataFile(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUpdataFile() {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static String readUpdataFile() {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                int length = fileInputStream.available();
                byte[] bf = new byte[length];
                fileInputStream.read(bf);
                String res = EncodingUtils.getString(bf, "UTF-8");
                fileInputStream.close();
                return res;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void wirteUpdataFile(String res) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bytes = res.getBytes();
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean updataVersion(String app, String version) {
        VersionBean b = new VersionBean();
        b.setAppType(app);
        b.setVersion(version);
        String res = readUpdataFile();
        if (TextUtils.isEmpty(res)) {
            return false;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaTypeProfile = objectMapper.getTypeFactory().constructParametricType(List.class, VersionBean.class);
        try {
            List<VersionBean> versionBeanList = objectMapper.readValue(res, javaTypeProfile);
            for(VersionBean bean1 : versionBeanList) {
                if (bean1.getAppType().equals(app)) {
                    versionBeanList.remove(bean1);
                    versionBeanList.add(b);
                    break;
                }
            }
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
                String ress = objectMapper.writeValueAsString(versionBeanList);
                wirteUpdataFile(ress);
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static List<VersionBean> JsonParserFile(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            if (obj.has(UrlUtils.STATUS)) {
                if (obj.getString(UrlUtils.STATUS).equals("success")) {
                    if (obj.has(UrlUtils.T)) {
                        JSONArray objT = obj.getJSONArray(UrlUtils.T);
                        if ((null != objT) && (!TextUtils.isEmpty(obj.toString()))) {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JavaType javaTypeProfile = objectMapper.getTypeFactory().
                                    constructParametricType(List.class, VersionBean.class);
                            List<VersionBean> list = objectMapper.readValue(objT.toString(), javaTypeProfile);
                            return list;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
