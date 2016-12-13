package com.dazone.crewemail.Service;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.utils.TimeUtils;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by THANHTUNG on 30/12/2015.
 */
public class fileUploadDownload {
    public static String rootFile = DaZoneApplication.getInstance().getPrefs().getServerSite()+"/UI/MobileMail3/MobileFileUpload.ashx?";
    public static String uploadFileToServer(String filePath, String fileName) {
        String url = rootFile +
                "sessionId="+ DaZoneApplication.getInstance().getPrefs().getAccessToken()+"&languageCode="
                + Locale.getDefault().getLanguage().toUpperCase()+"&timeZoneOffset="
                + TimeUtils.getTimezoneOffsetInMinutes();

        String boundary = "-----------------------------7dfe519300448";
        String delimiter = "\r\n--" + boundary + "\r\n";

        try {
            StringBuilder postDataBuilder = new StringBuilder();
            postDataBuilder.append(delimiter);
            postDataBuilder.append("Content-Disposition: form-data; name=\"files[]\";filename=\"" + fileName + "\"\r\n");
            postDataBuilder.append("\r\n");

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            FileInputStream in = new FileInputStream(filePath);
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));

            out.writeUTF(postDataBuilder.toString());

            byte[] buffer = new byte[1024];
            int readLength;

            while ((readLength = in.read(buffer)) != -1) {
                out.write(buffer, 0, readLength);
            }

            out.writeBytes(delimiter);
            out.flush();
            out.close();
            in.close();

            conn.getInputStream();
            conn.disconnect();
        }catch (Exception e)
        {
            e.printStackTrace();
            return "fail";
        }
        return "success";

    }
}
