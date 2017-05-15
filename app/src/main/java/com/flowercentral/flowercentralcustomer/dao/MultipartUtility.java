package com.flowercentral.flowercentralcustomer.dao;

import android.util.Log;

import com.flowercentral.flowercentralcustomer.preference.UserPreference;
import com.flowercentral.flowercentralcustomer.util.Util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP 
 * POST requests to a web server. 
 * @author www.codejava.net
 *
 */

public class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 10000;

    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    /**
     * This constructor initializes a new HTTP POST request with content type 
     * is set to multipart/form-data 
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    public MultipartUtility(String requestURL, String charset)
            throws IOException {
        this.charset = charset;

        // creates a unique boundary based on time stamp 
        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setConnectTimeout(CONNECT_TIMEOUT);
        httpConn.setReadTimeout(READ_TIMEOUT);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Accept-Charset", charset);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("Authorization", "Bearer "+ UserPreference.getApiToken());
        httpConn.setUseCaches(false);
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);

        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }

    /**
     * Adds a form field to the request 
     * @param name field name 
     * @param value field value 
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request 
     * @param fieldName name attribute in <input type="file" name="..." /> 
     * @param uploadFile a File to be uploaded 
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request. 
     * @param name - name of the header field 
     * @param value - value of the header field 
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server. 
     * @return a list of Strings as response in case the server returned 
     * status OK, otherwise an exception is thrown. 
     * @throws IOException
     */ 
    /* 
    public List<String> finish() throws IOException { 
        List<String> response = new ArrayList<String>(); 
  
        writer.append(LINE_FEED).flush(); 
        writer.append("--" + boundary + "--").append(LINE_FEED); 
        writer.close(); 
  
        // checks server's status code first 
        int status = httpConn.getResponseCode(); 
        if (status == HttpURLConnection.HTTP_OK) { 
            BufferedReader reader = new BufferedReader(new InputStreamReader( 
                    httpConn.getInputStream())); 
            String line = null; 
            while ((line = reader.readLine()) != null) { 
                response.add(line); 
            } 
            reader.close(); 
            httpConn.disconnect(); 
        } else { 
            throw new IOException("Server returned non-OK status: " + status); 
        } 
  
        return response; 
    } 
    */

    /**
     * Completes the request and receives response from the server. 
     * @return a list of Strings as response in case the server returned 
     * status OK, otherwise an exception is thrown. 
     * @throws IOException
     */
    public String finish() throws IOException {
        String response = "";

        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        // checks server's status code first 
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream in = new BufferedInputStream(httpConn.getInputStream());
            response = Util.streamToString(in);
            in.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response;
    }


    private static String inputStreamToString(InputStream in) {
        String result = "";
        if (in == null) {
            return result;
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            result = out.toString();
            reader.close();

            return result;
        } catch (Exception e) {
            // TODO: handle exception 
            Log.e("InputStream", "Error : " + e.toString());
            return result;
        }

    }
} 