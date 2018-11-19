package com.gatz.smarthomeapp.utils;

import android.util.Log;


import com.gatz.smarthomeapp.model.http.MySSLSocketFactory;
import com.gatz.smarthomeapp.model.http.ObserverCallBack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyStore;


public class UploadFileUtil {

	private static int CONNECTION_TIME_OUT = 30 * 1000;
	private static int SOCKET_TIME_OUT = 30 * 1000;
	private static final String UPLOAD_FILE_TAG = "upload_file_info";
	private OnUploadFileForResultListener listener;
	private File file;

	public void setTimeOut(int CTO, int STO) {
		CONNECTION_TIME_OUT = CTO;
		SOCKET_TIME_OUT = STO;
	}

	public void uploadBg(final File file, final String sessionId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				uploadImage(file,sessionId);
			}
		}).start();
	}
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("https", sf, 443));
	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	private void uploadImage(File file, String sessionId) {
		this.file = file;
		final DefaultHttpClient client = (DefaultHttpClient) getNewHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(),
				CONNECTION_TIME_OUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), SOCKET_TIME_OUT);
		try {
			final URI uri = new URI(UrlUtils.UPLOAD_URL+sessionId);
			HttpPost request = new HttpPost(uri);
			org.apache.http.entity.mime.MultipartEntity entity = new org.apache.http.entity.mime.MultipartEntity();
			// 封装body
			FileBody fileBody = new FileBody(file);
			entity.addPart("file", fileBody);
			request.setEntity(entity);
			final HttpResponse response = client.execute(request);
			final StatusLine status = response.getStatusLine();
			final int statusCode = status.getStatusCode();
			HttpEntity contentEntity = response.getEntity();
			Log.i(UPLOAD_FILE_TAG, String.valueOf(statusCode));
			if (statusCode == HttpStatus.SC_OK) {
				int i = (int) contentEntity.getContentLength();
				if (i < 0) {
					i = 4096;
				}
				final Reader reader = new InputStreamReader(contentEntity.getContent());
				final CharArrayBuffer buffer = new CharArrayBuffer(i);
				final char[] tmp = new char[1024];
				int l;
				while ((l = reader.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
				Log.e(UPLOAD_FILE_TAG, "content:" + buffer.toString());
				doImageWorkResult(buffer.toString(), true);
			} else {
				doImageWorkResult(null, false);
			}
		} catch (UnknownHostException e) {
			doImageWorkResult("网络连接异常", false);
		} catch (Exception e) {
			Log.e(UPLOAD_FILE_TAG, e.toString());
			doImageWorkResult(null, false);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public String getFileToByte(File file) {
		byte[] by = new byte[(int) file.length()];
		try {
			InputStream is = new FileInputStream(file);
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			byte[] bb = new byte[2048];
			int ch;
			ch = is.read(bb);
			while (ch != -1) {
				bytestream.write(bb, 0, ch);
				ch = is.read(bb);
			}
			is.close();
			by = bytestream.toByteArray();
			return new String(by, "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private void doImageWorkResult(String result, boolean isUploadSuccess) {
		try {
			String result_code="";
			String text = "";
			String path = "";
			if (isUploadSuccess) {
				JSONObject resultObject = new JSONObject(result);
				if (resultObject.has(UrlUtils.STATUS)) {
					result_code = resultObject.getString(UrlUtils.STATUS);
				}
				if (resultObject.has("protocol")) {
					JSONArray qa =resultObject.optJSONArray("protocol");
					if(null!=qa&&qa.length()==0){
						text = resultObject.getString(UrlUtils.TEXT);
					}
				}

			} else {
			}
			if (listener != null)
				listener.onResultListener(isUploadSuccess, result_code, text,file);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}



	public interface OnUploadFileForResultListener {

		public abstract void onResultListener(boolean isUploadSuccess,
											  String result_code, String text, File file);
	}

	public void setListener(OnUploadFileForResultListener resultListener) {
		listener = resultListener;
	}

	public void removeListener() {
		listener = null;
	}

	/**
	 * 上传日志文件
	 * @param sessionId
	 * @param file
	 * @param fileName
	 * @param appVersion
	 * @param osVersion
	 * @param equipmentModel
     * @param logType
     * @param otime
     */
	public void uploadLogFile(String sessionId, File file, final String fileName,
							  String appVersion, String osVersion, String equipmentModel, String logType, String otime){
		Charset charset = Charset.forName("UTF-8");
		final MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart("appType",new StringBody(UrlUtils.terminal,charset));
			entity.addPart("appVersion",new StringBody(appVersion,charset));
			entity.addPart("osVersion",new StringBody(osVersion,charset));
			entity.addPart("equipmentModel",new StringBody(equipmentModel,charset));
			entity.addPart("logType",new StringBody(logType,charset));
			entity.addPart("otime",new StringBody(otime,charset));
			entity.addPart(UrlUtils.APIKEY,new StringBody(sessionId,charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		FileBody fileBody = new FileBody(file);
		entity.addPart("file", fileBody);
		startUpload(UrlUtils.LOG_REPORT,entity,file);
}

	private  void startUpload(final String url, final MultipartEntity entity, final File file){
		new Thread(new Runnable() {
			@Override
			public void run() {
				uploadFile(url,entity,file);
			}
		}).start();
	}

	/**
	 * 上传文件
	 * @param url
	 * @param entity
	 * @param file
     */
	private void uploadFile(String url, MultipartEntity entity, File file){
		this.file = file;
		final DefaultHttpClient client = (DefaultHttpClient) getNewHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(),
				CONNECTION_TIME_OUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), SOCKET_TIME_OUT);
		try {
			final URI uri = new URI(url);
			HttpPost request = new HttpPost(uri);
			request.setEntity(entity);
			final HttpResponse response = client.execute(request);
			final StatusLine status = response.getStatusLine();
			final int statusCode = status.getStatusCode();
			HttpEntity contentEntity = response.getEntity();
			Log.i(UPLOAD_FILE_TAG, statusCode+"");
			if (statusCode == HttpStatus.SC_OK) {
				int i = (int) contentEntity.getContentLength();
				if (i < 0) {
					i = 4096;
				}
				Log.i(UPLOAD_FILE_TAG, i+"");
				final Reader reader = new InputStreamReader(contentEntity.getContent());
				final CharArrayBuffer buffer = new CharArrayBuffer(i);
				final char[] tmp = new char[1024];
				int l;
				while ((l = reader.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
				Log.e(UPLOAD_FILE_TAG, "content:" + buffer.toString());
				doImageWorkResult(buffer.toString(), true);
			} else {
				Log.e(UPLOAD_FILE_TAG, "doImageWorkResult false");
				doImageWorkResult(null, false);
			}
		} catch (UnknownHostException e) {
			doImageWorkResult("网络连接异常", false);
		} catch (Exception e) {
			Log.e(UPLOAD_FILE_TAG, e.toString());
			doImageWorkResult(null, false);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

}
