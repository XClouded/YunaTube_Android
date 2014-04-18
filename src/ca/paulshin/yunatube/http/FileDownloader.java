package ca.paulshin.yunatube.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Utils;

public class FileDownloader {
	public static void download(String downloadUrl, String directory, String fileName) {
		File dir = new File(Utils.getFilePath(directory));
		if (dir.exists() == false) {
			dir.mkdirs();
		}

		File file = new File(dir, fileName);
		download(downloadUrl, file);
	}

	public static void download(String downloadUrl, File file) {
		try {
			URL url = new URL(downloadUrl);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);

			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(file);
			Utils.copyStream(is, os);
			os.close();
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
		}
	}

	public static void moveFileLocally(File src, File dest) throws Exception {
		if (dest.exists() == false) {
			try {
				dest.createNewFile();
			} catch (IOException e) {
				if (YunaTubeApplication.debuggable)
					e.printStackTrace();
			}
		}

		InputStream inStream = null;
		OutputStream outStream = null;
		inStream = new FileInputStream(src);
		outStream = new FileOutputStream(dest); // for override file content
		// outStream = new FileOutputStream(file2,<strong>true</strong>); // for append file content

		byte[] buffer = new byte[1024];
		int length;
		while ((length = inStream.read(buffer)) > 0) {
			outStream.write(buffer, 0, length);
		}
		if (inStream != null)
			inStream.close();
		if (outStream != null)
			outStream.close();
	}
}