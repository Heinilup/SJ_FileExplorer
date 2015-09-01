package com.ikabi.apps.interfaces;

import java.util.Collection;

import com.ikabi.apps.apis.FileSortHelper;
import com.ikabi.apps.entity.FileInfo;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public interface IFileInteractionListener {

	public View getViewById(int id);

	public Context getContext();

	public FileInfo getItem(int pos);

	public void onPick(FileInfo f);

	public void onDataChanged();

	public void runOnUiThread(Runnable r);

	public void sortCurrentList(FileSortHelper sort);

	public boolean onRefreshFileList(String path, FileSortHelper sort);

	public Collection<FileInfo> getAllFiles();

	public void startActivity(Intent intent);
	
	public void startActivityForResult(Intent intent,int requestCode);

	public void addSingleFile(FileInfo file);
	
	public void ShowMovingOperationBar(boolean isShow);
	
	public void updateMediaData();

}
