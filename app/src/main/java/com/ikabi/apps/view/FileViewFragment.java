package com.ikabi.apps.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ikabi.actionbarsherlock.app.SherlockFragment;
import com.ikabi.actionbarsherlock.view.ActionMode;
import com.ikabi.actionbarsherlock.view.Menu;
import com.ikabi.actionbarsherlock.view.MenuInflater;
import com.ikabi.apps.R;
import com.ikabi.apps.adapter.FileListAdater;
import com.ikabi.apps.apis.FileCategoryHelper;
import com.ikabi.apps.apis.FileIconHelper;
import com.ikabi.apps.apis.FileInteractionHub;
import com.ikabi.apps.apis.FileInteractionHub.Mode;
import com.ikabi.apps.apis.FileSortHelper;
import com.ikabi.apps.apis.SettingHelper;
import com.ikabi.apps.entity.FileInfo;
import com.ikabi.apps.entity.GlobalConsts;
import com.ikabi.apps.interfaces.IFileInteractionListener;
import com.ikabi.apps.ui.MainActivity;
import com.ikabi.apps.utils.FileUtil;
import com.ikabi.apps.utils.MenuUtils;
import com.ikabi.apps.utils.MenuUtils.MenuItemType;
import com.ikabi.apps.utils.ToastUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

public class FileViewFragment extends SherlockFragment implements
		IFileInteractionListener, OnClickListener {

	private FileInteractionHub mFileInteractionHub;

	private FileIconHelper mFileIconHelper;

	private FileCategoryHelper mFileCategoryHelper;
	
	private SettingHelper  mSettingHelper;

	private MainActivity mActivity;

	private FileSortHelper mFileSortHelper;

	private View mRootView;

	private ListView mfileListView;

	private View memptyView;

	private View mnoSdView;

	private LinearLayout mMovingOperationBar;

	private ImageButton mButtonMovingConfirm;

	private TextView mTitleOperationBar;

	private Button mButtonMovingCancle;

	private LinearLayout mrefreshViewLinearLayout;

	private HorizontalScrollView mHorizontalScrollView;

	private LinearLayout mcurrentPathLinearLayout;

	private ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();

	private ArrayAdapter<FileInfo> mAdapter;

	private static final String sdDir = FileUtil.getSdDirectory();

	private refreshFileAsyncTask mrefreshFileAsyncTask;

	private MenuUtils mMenuUtils;

	private String mcurrentPath;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivity = (MainActivity) getActivity();
		mActivity.setFileViewFragment(this);

		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.file_explorer_list, container,
				false);
		mcurrentPathLinearLayout = (LinearLayout) mRootView
				.findViewById(R.id.current_path);
		mHorizontalScrollView = (HorizontalScrollView) mRootView
				.findViewById(R.id.horizontalscrollview);
		mMovingOperationBar = (LinearLayout) mRootView
				.findViewById(R.id.moving_operation_bar);
		mTitleOperationBar = (TextView) mRootView.findViewById(R.id.title);
		mButtonMovingConfirm = (ImageButton) mRootView
				.findViewById(R.id.button_moving_confirm);
		mButtonMovingConfirm.setOnClickListener(this);
		mButtonMovingCancle = (Button) mRootView
				.findViewById(R.id.button_moving_cancel);
		mButtonMovingCancle.setOnClickListener(this);

		mrefreshViewLinearLayout = (LinearLayout) mRootView
				.findViewById(R.id.refresh_view);

		mSettingHelper = SettingHelper.getInstance(mActivity);
		mFileInteractionHub = new FileInteractionHub(this);
		mFileCategoryHelper = new FileCategoryHelper(mActivity);
		mMenuUtils = new MenuUtils(mActivity, mFileInteractionHub);

		memptyView = mRootView.findViewById(R.id.empty_view);
		mnoSdView = mRootView.findViewById(R.id.sd_not_available_page);

		mfileListView = (ListView) mRootView.findViewById(R.id.file_path_list);
		mFileIconHelper = new FileIconHelper(mActivity);
		mAdapter = new FileListAdater(mActivity, R.layout.file_browser_item,
				mFileNameList, mFileInteractionHub, mFileIconHelper);
		mfileListView.setAdapter(mAdapter);

		mFileInteractionHub.setMode(Mode.View);
		
		
		mFileInteractionHub.setRootPath(sdDir);
		mFileInteractionHub.setCurrentPath(mSettingHelper.getRootPath());

		updateUI();

		return mRootView;
	}

	private void updateUI() {
		boolean sdCardReady = FileUtil.isSDCardReady();
		mnoSdView.setVisibility(sdCardReady ? View.GONE : View.VISIBLE);

		mfileListView.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);

		if (sdCardReady) {
			mFileInteractionHub.refreshFileList();
		}

	}


	private void setFileNum(int filenum) {
		mActivity.setFileNum(filenum,MenuItemType.MENU_DEVICE);
	}


	private void showEmptyView(boolean show) {
		if (memptyView != null)
			memptyView.setVisibility(show ? View.VISIBLE : View.GONE);
	}



	private void showProgressBar(boolean show) {
		if (show) {
			mrefreshViewLinearLayout.setVisibility(View.VISIBLE);
			mfileListView.setVisibility(View.GONE);
		} else {
			mrefreshViewLinearLayout.setVisibility(View.GONE);
			mfileListView.setVisibility(View.VISIBLE);
			sortCurrentList(mFileSortHelper);
			showEmptyView(mFileNameList.size() == 0);


		}
	}


	public void ShowMovingOperationBar(boolean isShow) {

		mMovingOperationBar.setVisibility(isShow ? View.VISIBLE : View.GONE);

		if (isShow) {

			mTitleOperationBar.setText(R.string.operation_paste);
			setHasOptionsMenu(false);

			ActionMode mode = mActivity.getActionMode();
			if (mode != null) {
				mode.finish();
			}

			if (mActivity.getActionBar().isShowing()) {
				mActivity.getActionBar().hide();
			}
		} else {
			if (!mActivity.getActionBar().isShowing()) {
				mActivity.getActionBar().show();
			}
		}
	}

	public void refresh() {
		if (mFileInteractionHub != null) {
			mFileInteractionHub.refreshFileList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ikabi.apps.interfaces.IFileInteractionListener#onRefreshFileList
	 * (java.lang.String, com.ikabi.apps.apis.FileSortHelper)
	 */
	public boolean onRefreshFileList(String path, FileSortHelper sort) {
		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return false;
		}

		mFileSortHelper = sort;

		mrefreshFileAsyncTask = new refreshFileAsyncTask();
		mrefreshFileAsyncTask.execute(file);
		mcurrentPath = path;

		showProgressBar(true);
		createPathNavigation(mcurrentPath);

		return true;
	}


	public boolean onBack() {
		if (!FileUtil.isSDCardReady() || mFileInteractionHub == null) {
			return false;
		}
		return mFileInteractionHub.onBackPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ikabi.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(android
	 * .view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		if (!mActivity.getSupportActionBar().isShowing()) {
			return;
		}

		mMenuUtils.addMenu(menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GlobalConsts.TAKE_PHOTO
				&& resultCode == mActivity.RESULT_OK) {
			mFileInteractionHub.addTakePhotoFile();
		}

	}


	public void createPathNavigation(String filePath) {
		mcurrentPathLinearLayout.removeAllViews();
		String[] fileNameStrings = filePath.split("/");
		int index = 0;
		for (String f : fileNameStrings) {
			if (!TextUtils.isEmpty(f)) {
				break;
			}

			index++;
		}

		String filename;
		TextView textView;
		if (fileNameStrings.length - index == 1) {
			filename = fileNameStrings[index];
			textView = new TextView(mActivity);
			textView.setText(filename);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setOnClickListener(this);
			textView.setBackgroundResource(R.drawable.bg_addressbar_right_0);

			mcurrentPathLinearLayout.addView(textView);

		} else {
			for (int i = index; i < fileNameStrings.length; i++) {
				filename = fileNameStrings[i];
				textView = new TextView(mActivity);
				textView.setText(filename);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				textView.setOnClickListener(this);

				if (i == index) {
					textView.setBackgroundResource(R.drawable.bg_addressbar_left);
				} else if (fileNameStrings.length - i == 1) {
					textView.setBackgroundResource(R.drawable.bg_addressbar_right);
				} else {
					textView.setBackgroundResource(R.drawable.bg_addressbar_middle);
				}

				mcurrentPathLinearLayout.addView(textView);
			}

		}

		mHorizontalScrollView.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHorizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_moving_confirm:
			mFileInteractionHub.onOperationButtonConfirm();
			break;

		case R.id.button_moving_cancel:
			mFileInteractionHub.onOperationButtonCancel();
			break;

		default:
			String fileIndex = (String) ((TextView) v).getText();
			int length = fileIndex.length();
			int index = mcurrentPath.indexOf(fileIndex);
			String path = mcurrentPath.substring(0, index + length);

			onRefreshFileList(path, mFileSortHelper);
			mFileInteractionHub.setCurrentPath(path);
			break;
		}

		setHasOptionsMenu(true);

	}

	public boolean setPath(String location) {
		if (!location.startsWith(mFileInteractionHub.getRootPath())) {
			return false;
		}
		mFileInteractionHub.setCurrentPath(location);
		mFileInteractionHub.refreshFileList();
		return true;
	}


	public void copyFile(ArrayList<FileInfo> files) {
		mFileInteractionHub.onOperationCopy(files);
	}


	public void moveToFile(ArrayList<FileInfo> files) {
		mFileInteractionHub.onOperationMove(files);
	}


	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return mActivity;
	}

	@Override
	public View getViewById(int id) {
		// TODO Auto-generated method stub
		return mRootView.findViewById(id);
	}

	@Override
	public FileInfo getItem(int pos) {
		// TODO Auto-generated method stub
		if (pos < 0 || pos > mFileNameList.size() - 1) {
			return null;
		}
		return mFileNameList.get(pos);
	}

	@Override
	public void onPick(FileInfo f) {
		try {
			Intent intent = Intent.parseUri(Uri.fromFile(new File(f.filePath))
					.toString(), 0);
			mActivity.setResult(Activity.RESULT_OK, intent);
			mActivity.finish();
			return;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDataChanged() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void runOnUiThread(Runnable r) {
		// TODO Auto-generated method stub
		mActivity.runOnUiThread(r);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sortCurrentList(FileSortHelper sort) {
		// TODO Auto-generated method stub
		Collections.sort(mFileNameList, sort.getComparator());
		onDataChanged();
		mActivity.setFileNum(mFileNameList.size(),MenuItemType.MENU_DEVICE);
	}

	@Override
	public ArrayList<FileInfo> getAllFiles() {
		// TODO Auto-generated method stub
		return mFileNameList;
	}

	@Override
	public void addSingleFile(FileInfo file) {
		mFileNameList.add(file);
		onDataChanged();
	}

	public class refreshFileAsyncTask extends AsyncTask<File, Void, Integer> {

		@Override
		protected Integer doInBackground(File... files) {
			// TODO Auto-generated method stub

			ArrayList<FileInfo> fileList = mFileNameList;
			fileList.clear();

			File[] listFiles = files[0].listFiles(mFileCategoryHelper
					.getFilter());
			if (listFiles == null)
				return Integer.valueOf(0);

			for (File child : listFiles) {
				String absolutePath = child.getAbsolutePath();
				if (FileUtil.isNormalFile(absolutePath)
						&& FileUtil.shouldShowFile(absolutePath)) {
					FileInfo lFileInfo = FileUtil.GetFileInfo(child,
							mFileCategoryHelper.getFilter(), mSettingHelper.getShowHideFile());
					if (lFileInfo != null) {
						fileList.add(lFileInfo);
					}
				}
			}
			return Integer.valueOf(fileList.size());
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			setFileNum(result.intValue());
			showProgressBar(false);
		}
	}


	@Override
	public void updateMediaData() {
		// TODO Auto-generated method stub
		ToastUtils.getInstance(mActivity).showMask("Fileview  updateMediaData",
				Toast.LENGTH_SHORT);
	}

}
