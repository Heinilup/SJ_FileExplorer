package com.ikabi.apps.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import com.ikabi.actionbarsherlock.view.ActionMode;
import com.ikabi.apps.R;
import com.ikabi.apps.apis.FileCategoryHelper;
import com.ikabi.apps.apis.FileCategoryHelper.FileCategoryType;
import com.ikabi.apps.apis.SettingHelper;
import com.ikabi.apps.entity.FavoriteItem;
import com.ikabi.apps.entity.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class FileUtil {

	private static final String LOG_TAG = "Util";

	private static String ANDROID_SECURE = "/mnt/sdcard/.android_secure";

	public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
		{
			add("text/plain");
			add("text/html");
			add("application/vnd.ms-powerpoint");
			add("application/pdf");
			add("application/msword");
			add("application/vnd.ms-excel");
			add("application/vnd.ms-excel");
		}
	};
	
	public static String sZipFileMimeType = "application/zip";

	public static String getSdDirectory() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public static boolean isSDCardReady() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	

	
	 public static ArrayList<FavoriteItem> getDefaultFavorites(Context context) {
	        ArrayList<FavoriteItem> list = new ArrayList<FavoriteItem>();
	        list.add(new FavoriteItem(context.getString(R.string.favorite_photo), makePath(getSdDirectory(), "DCIM/Camera")));
	        list.add(new FavoriteItem(context.getString(R.string.favorite_sdcard), getSdDirectory()));
	        return list;
	    }

	public static boolean setText(View view, int id, String text) {
		TextView textView = (TextView) view.findViewById(id);
		if (textView == null)
			return false;

		textView.setText(text);
		return true;
	}


	public static boolean isNormalFile(String fullName) {
		return !fullName.equals(ANDROID_SECURE);
	}

	private static String[] SysFileDirs = new String[] { "miren_browser/imagecaches" };


	public static boolean shouldShowFile(String path) {
		return shouldShowFile(new File(path));
	}

	public static boolean shouldShowFile(File file) {
		boolean show = SettingHelper.getShowHideFile();
		if (show)
			return true;

		if (file.isHidden())
			return false;

		if (file.getName().startsWith("."))
			return false;

		String sdFolder = getSdDirectory();
		for (String s : SysFileDirs) {
			if (file.getPath().startsWith(makePath(sdFolder, s)))
				return false;
		}

		return true;
	}

	public static String makePath(String path1, String path2) {
		if (path1.endsWith(File.separator))
			return path1 + path2;

		return path1 + File.separator + path2;
	}


	public static String formatDateString(Context context, long time) {
		DateFormat dateFormat = android.text.format.DateFormat
				.getDateFormat(context);
		DateFormat timeFormat = android.text.format.DateFormat
				.getTimeFormat(context);
		Date date = new Date(time);

		return dateFormat.format(date) + " " + timeFormat.format(date);
	}


	public static String convertStorage(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}
	

	public static Uri getMediaUriFromFilename(String filename){
		String extString = getExtFromFilename(filename);
		String volumeName = "external";
        FileCategoryType fileCategoryType = FileCategoryHelper.fileExtCategoryType.get(extString);
		
        Uri uri = null;
        if (fileCategoryType == FileCategoryType.Music) {
            uri = Audio.Media.getContentUri(volumeName);
		}else if (fileCategoryType == FileCategoryType.Picture) {
			uri = Images.Media.getContentUri(volumeName);
		}else if(fileCategoryType == FileCategoryType.Video){
			uri = Video.Media.getContentUri(volumeName);
		}else {
			uri = Files.getContentUri(volumeName);
		}
		return uri;
	}
	 

	public static String getMimetypeFromFilename(String filename){
		String mimetype = null;
		String extString = getExtFromFilename(filename);
		
		MimeTypeMap mineMap = MimeTypeMap.getSingleton();
		mimetype = mineMap.getMimeTypeFromExtension(extString);
		
		return mimetype;
	}
	


	public static String getExtFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(dotPosition + 1, filename.length());
		}
		return "";
	}


	public static String getNameFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(0, dotPosition);
		}
		return "";
	}


	public static String getNameFromFilepath(String filepath) {
		int pos = filepath.lastIndexOf('/');
		if (pos != -1) {
			return filepath.substring(pos + 1);
		}
		return "";
	}



	public static String getPathFromFilepath(String filepath) {
		int pos = filepath.lastIndexOf('/');
		if (pos != -1) {
			return filepath.substring(0, pos);
		}
		return "";
	}


	public static Drawable getApkIcon(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = apkPath;
			appInfo.publicSourceDir = apkPath;
			try {
				return appInfo.loadIcon(pm);
			} catch (OutOfMemoryError e) {
				LogUtils.e(LOG_TAG, e.toString());
			}
		}
		return null;
	}



	public static long getTotalSizeOfFilesInDir(final File file) {
		if (file.isFile())
			return file.length();
		final File[] children = file.listFiles();
		long total = 0;
		if (children != null)
			for (final File child : children)
				total += getTotalSizeOfFilesInDir(child);
		return total;
	}


	public static FileInfo GetFileInfo(File f, FilenameFilter filter,
			boolean showHidden) {
		FileInfo lFileInfo = new FileInfo();
		String filePath = f.getPath();
		File lFile = new File(filePath);
		lFileInfo.canRead = lFile.canRead();
		lFileInfo.canWrite = lFile.canWrite();
		lFileInfo.isHidden = lFile.isHidden();
		lFileInfo.fileName = f.getName();
		lFileInfo.ModifiedDate = lFile.lastModified();
		lFileInfo.IsDir = lFile.isDirectory();
		lFileInfo.filePath = filePath;
		if (lFileInfo.IsDir) {
			int lCount = 0;
			File[] files = lFile.listFiles(filter);

			// null means we cannot access this dir
			if (files == null) {
				return null;
			}

			for (File child : files) {
				if ((!child.isHidden() || showHidden)
						&& FileUtil.isNormalFile(child.getAbsolutePath())) {
					lCount++;
				}
			}
			lFileInfo.Count = lCount;
		} else {

			lFileInfo.fileSize = lFile.length();

		}
		return lFileInfo;
	}

	public static FileInfo GetFileInfo(String filePath) {
		File lFile = new File(filePath);
		if (!lFile.exists())
			return null;

		FileInfo lFileInfo = new FileInfo();
		lFileInfo.canRead = lFile.canRead();
		lFileInfo.canWrite = lFile.canWrite();
		lFileInfo.isHidden = lFile.isHidden();
		lFileInfo.fileName = FileUtil.getNameFromFilepath(filePath);
		lFileInfo.ModifiedDate = lFile.lastModified();
		lFileInfo.IsDir = lFile.isDirectory();
		lFileInfo.filePath = filePath;
		lFileInfo.fileSize = lFile.length();
		return lFileInfo;
	}


	public static void updateActionModeTitle(ActionMode mode, Context context,
			int selectedNum) {
		if (mode != null) {
			View view = mode.getCustomView();
			Button btnTitle = (Button) view.findViewById(R.id.selection_menu);
			btnTitle.setText(context.getString(R.string.multi_select_title,
					selectedNum));
			if (selectedNum == 0) {
				mode.finish();
			}
		}
	}


	public static String copyFile(String src, String dest) {
		File file = new File(src);
		if (!file.exists() || file.isDirectory()) {
			return null;
		}

		FileInputStream fi = null;
		FileOutputStream fo = null;
		try {
			fi = new FileInputStream(file);
			File destPlace = new File(dest);
			if (!destPlace.exists()) {
				if (!destPlace.mkdirs())
					return null;
			}

			String destPath = FileUtil.makePath(dest, file.getName());
			File destFile = new File(destPath);
			int i = 1;
			while (destFile.exists()) {
				String destName = FileUtil.getNameFromFilename(file.getName())
						+ " " + i++ + "."
						+ FileUtil.getExtFromFilename(file.getName());
				destPath = FileUtil.makePath(dest, destName);
				destFile = new File(destPath);
			}

			if (!destFile.createNewFile())
				return null;

			fo = new FileOutputStream(destFile);
			int count = 102400;
			byte[] buffer = new byte[count];
			int read = 0;
			while ((read = fi.read(buffer, 0, count)) != -1) {
				fo.write(buffer, 0, read);
			}

			// TODO: set access privilege

			return destPath;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			LogUtils.e(LOG_TAG, "copyFile: " + e.toString());
		} finally {
			try {
				if (fi != null)
					fi.close();
				if (fo != null)
					fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
