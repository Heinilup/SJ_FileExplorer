package com.ikabi.apps.adapter;

import java.util.Collection;
import java.util.HashMap;

import com.ikabi.apps.R;
import com.ikabi.apps.apis.FileCategoryHelper;
import com.ikabi.apps.apis.FileIconHelper;
import com.ikabi.apps.apis.FileInteractionHub;
import com.ikabi.apps.apis.FileListItem;
import com.ikabi.apps.entity.FileInfo;
import com.ikabi.apps.utils.FileUtil;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class FileListCursorAdapter extends CursorAdapter {

	private final LayoutInflater mLayoutInflater;

	private FileInteractionHub mFileInteractionHub;

	private Context mContext;

	private FileIconHelper mFileIconHelper;

	private OnClickListener mOnClickListener;

	private HashMap<Integer, FileInfo> mFileNameList = new HashMap<Integer, FileInfo>();

	public FileListCursorAdapter(Context context, Cursor c,
			FileInteractionHub fHub, FileIconHelper fileIcon) {
		super(context, c, false);
		// TODO Auto-generated constructor stub
		mLayoutInflater = LayoutInflater.from(context);
		mFileInteractionHub = fHub;
		mFileIconHelper = fileIcon;
		mContext = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		FileInfo fileInfo = getFileItem(cursor.getPosition());
		if (fileInfo == null) {
			// file is not existing, create a fake info
			fileInfo = new FileInfo();
			fileInfo.dbId = cursor.getLong(FileCategoryHelper.COLUMN_ID);
			fileInfo.filePath = cursor
					.getString(FileCategoryHelper.COLUMN_PATH);
			fileInfo.fileName = FileUtil.getNameFromFilepath(fileInfo.filePath);
			fileInfo.fileSize = cursor.getLong(FileCategoryHelper.COLUMN_SIZE);
			fileInfo.ModifiedDate = cursor
					.getLong(FileCategoryHelper.COLUMN_DATE);
		}
		
		FileListItem.setupFileListItemInfo(mContext, view, fileInfo,
				mFileIconHelper, mFileInteractionHub);

		mOnClickListener = new FileListItem.FileItemOnClickListener(mContext,
				mFileInteractionHub);
		view.findViewById(R.id.category_file_checkbox_area).setOnClickListener(
				mOnClickListener);
		view.findViewById(R.id.favorite_area).setOnClickListener(
				mOnClickListener);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		return mLayoutInflater.inflate(R.layout.category_file_browser_item,
				viewGroup, false);
	}

	@Override
	public void changeCursor(Cursor cursor) {
		// TODO Auto-generated method stub
		mFileNameList.clear();
		super.changeCursor(cursor);
	}

	public Collection<FileInfo> getAllFiles() {
		if (mFileNameList.size() == getCount())
			return mFileNameList.values();

		Cursor cursor = getCursor();
		if (cursor.moveToFirst()) {
			do {
				Integer position = Integer.valueOf(cursor.getPosition());
				if (mFileNameList.containsKey(position))
					continue;
				FileInfo fileInfo = getFileInfo(cursor);
				if (fileInfo != null) {
					mFileNameList.put(position, fileInfo);
				}
			} while (cursor.moveToNext());
		}

		return mFileNameList.values();
	}

	public FileInfo getFileItem(int pos) {
		Integer position = Integer.valueOf(pos);
		if (mFileNameList.containsKey(position))
			return mFileNameList.get(position);

		Cursor cursor = (Cursor) getItem(pos);
		FileInfo fileInfo = getFileInfo(cursor);
		if (fileInfo == null)
			return null;

		mFileNameList.put(position, fileInfo);
		fileInfo.dbId = cursor.getLong(FileCategoryHelper.COLUMN_ID);
		return fileInfo;
	}

	private FileInfo getFileInfo(Cursor cursor) {
		return (cursor == null || cursor.getCount() == 0) ? null : FileUtil
				.GetFileInfo(cursor.getString(FileCategoryHelper.COLUMN_PATH));
	}

}
