package com.ikabi.apps.apis;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.ikabi.actionbarsherlock.view.ActionMode;
import com.ikabi.actionbarsherlock.view.Menu;
import com.ikabi.actionbarsherlock.view.MenuInflater;
import com.ikabi.actionbarsherlock.view.MenuItem;
import com.ikabi.apps.R;
import com.ikabi.apps.apis.FileInteractionHub.Mode;
import com.ikabi.apps.db.FavoriteDatabaseHelper;
import com.ikabi.apps.entity.FileInfo;
import com.ikabi.apps.ui.MainActivity;
import com.ikabi.apps.utils.FileUtil;
import com.ikabi.apps.utils.MenuUtils.MenuItemType;
import com.ikabi.apps.view.FileViewFragment;

public class FileListItem {

	private String LOG_TAG = "FileListItem";

	public static void setupFileListItemInfo(Context context, View view,
			FileInfo fileInfo, FileIconHelper fileIcon) {


		FileUtil.setText(view, R.id.file_name, fileInfo.fileName);
		FileUtil.setText(view, R.id.file_count, fileInfo.IsDir ? "("
				+ fileInfo.Count + ")" : "");
		FileUtil.setText(view, R.id.modified_time,
				FileUtil.formatDateString(context, fileInfo.ModifiedDate));
		FileUtil.setText(
				view,
				R.id.file_size,
				(fileInfo.IsDir ? "" : FileUtil
						.convertStorage(fileInfo.fileSize)));

		ImageView fileiconImageView;
		fileiconImageView = (ImageView) view
				.findViewById(R.id.file_image);
		ImageView fileiconframeImageView;
		fileiconframeImageView = (ImageView) view
				.findViewById(R.id.file_image_frame);
		if (fileInfo.IsDir) {
			fileiconframeImageView.setVisibility(View.GONE);
			fileiconImageView.setImageResource(R.drawable.ic_folder_filetype);
		} else {
			fileIcon.setIcon(fileInfo, fileiconImageView,
					fileiconframeImageView);
		}

	}

	public static void setupFileListItemInfo(Context context, View view,
			FileInfo fileInfo, FileIconHelper fileIcon,
			FileInteractionHub fileInteractionHub) {

		ImageView checkboxImageView = (ImageView) view
				.findViewById(R.id.file_checkbox);
		ImageView favoriteImageView = (ImageView) view
				.findViewById(R.id.favorite_img);
		if (fileInteractionHub == null
				|| fileInteractionHub.getMode() == Mode.Pick) {
			checkboxImageView.setVisibility(View.GONE);
		} else {
			checkboxImageView
					.setImageResource(fileInfo.Selected ? R.drawable.btn_check_on
							: R.drawable.btn_check_off);
			favoriteImageView
					.setImageResource(FavoriteDatabaseHelper.getInstance().isFavorite(fileInfo.filePath) ? R.drawable.ic_star_filetype
							: R.drawable.ic_graystar_filetype);

			checkboxImageView.setTag(fileInfo);
			favoriteImageView.setTag(fileInfo);
			view.setSelected(fileInfo.Selected);
		}

		FileUtil.setText(view, R.id.file_name, fileInfo.fileName);
		FileUtil.setText(view, R.id.file_count, fileInfo.IsDir ? "("
				+ fileInfo.Count + ")" : "");
		FileUtil.setText(view, R.id.modified_time,
				FileUtil.formatDateString(context, fileInfo.ModifiedDate));
		FileUtil.setText(
				view,
				R.id.file_size,
				(fileInfo.IsDir ? "" : FileUtil
						.convertStorage(fileInfo.fileSize)));

		ImageView fileiconImageView = (ImageView) view
				.findViewById(R.id.file_image);
		ImageView fileiconframeImageView = (ImageView) view
				.findViewById(R.id.file_image_frame);
		if (fileInfo.IsDir) {
			fileiconframeImageView.setVisibility(View.GONE);
			fileiconImageView.setImageResource(R.drawable.ic_folder_filetype);
		} else {
			fileIcon.setIcon(fileInfo, fileiconImageView,
					fileiconframeImageView);
		}

	}

	public static class FileItemOnClickListener implements OnClickListener {

		private Context mContext;
		private FileInteractionHub mfileInteractionHub;

		public FileItemOnClickListener(Context context, FileInteractionHub hub) {
			mContext = context;
			mfileInteractionHub = hub;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.category_file_checkbox_area:
			case R.id.file_checkbox_area:
			{
				ImageView img = (ImageView) v.findViewById(R.id.file_checkbox);
				assert (img != null && img.getTag() != null);

				FileInfo fileInfo = (FileInfo) img.getTag();
				fileInfo.Selected = !fileInfo.Selected;
				ActionMode actionMode = ((MainActivity) mContext)
						.getActionMode();
				if (actionMode == null) {
					actionMode = ((MainActivity) mContext)
							.startActionMode(new ModeCallback(mContext,
									mfileInteractionHub));
					((MainActivity) mContext).setActionMode(actionMode);
				} else {
					actionMode.invalidate();
				}

				if (mfileInteractionHub.onCheckItem(fileInfo, v)) {
					img.setImageResource(fileInfo.Selected ? R.drawable.btn_check_on
							: R.drawable.btn_check_off);
				} else {
					fileInfo.Selected = !fileInfo.Selected;
				}

				FileUtil.updateActionModeTitle(actionMode, mContext,
						mfileInteractionHub.getSelectedFileList().size());

			}
				break;
			case R.id.favorite_area:
			{
				ImageView img = (ImageView) v.findViewById(R.id.favorite_img);
				assert (img != null && img.getTag() != null);

				FileInfo fileInfo = (FileInfo) img.getTag();
				fileInfo.Started = !fileInfo.Started;
				img.setImageResource(fileInfo.Started ? R.drawable.ic_star_filetype
						: R.drawable.ic_graystar_filetype);

				mfileInteractionHub.onOperationFavorite(fileInfo.filePath);
			}
				break;

			default:
				break;
			}
		}
	}

	public static class ModeCallback implements ActionMode.Callback,
			OnClickListener {

		private Menu mMenu;
		private Button btnTitle;
		private Context mContext;
		private FileInteractionHub mfInteractionHub;
		private PopupWindow mpopupFilter = null;

		public ModeCallback(Context context,
				FileInteractionHub fileInteractionHub) {
			mContext = context;
			mfInteractionHub = fileInteractionHub;
		}

		private void scrollToMydevice() {
			MenuItemType menuItemType = ((MainActivity) mContext)
					.getCurrentMenuItemType();
			if (menuItemType != MenuItemType.MENU_DEVICE) {
				((MainActivity) mContext)
						.setShowSelFragments(MenuItemType.MENU_DEVICE);
			}
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			MenuInflater menuInflater = ((MainActivity) mContext)
					.getSupportMenuInflater();
			mMenu = menu;
			menuInflater.inflate(R.menu.action_mode_menu, mMenu);
			View titleView = View.inflate(mContext, R.layout.action_mode, null);
			btnTitle = (Button) titleView.findViewById(R.id.selection_menu);
			btnTitle.setOnClickListener(this);
			mode.setCustomView(titleView);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// TODO Auto-generated method stub
			switch (item.getItemId()) {
			case R.id.delete:
				mfInteractionHub.onOperationDelete();
				mode.finish();
				break;

			case R.id.copy:
				((FileViewFragment) ((MainActivity) mContext)
						.getFileViewFragment()).copyFile(mfInteractionHub
						.getSelectedFileList());
				mode.finish();
				scrollToMydevice();
				break;

			case R.id.cut:
				((FileViewFragment) ((MainActivity) mContext)
						.getFileViewFragment()).moveToFile(mfInteractionHub
						.getSelectedFileList());
				mode.finish();
				scrollToMydevice();
				break;

			case R.id.share:
				mfInteractionHub.onOperationShare();
				mode.finish();
				break;


			case R.id.rename:
				mfInteractionHub.onOperationRename();
				mode.finish();
				break;

			case R.id.detail:
				mfInteractionHub.onOperationdetail();
				mode.finish();
				break;

			default:
				break;
			}

			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// TODO Auto-generated method stub
			mfInteractionHub.clearSelection();
			((MainActivity) mContext).setActionMode(null);
		}


		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			if (mfInteractionHub.isAllSelection()) {
				LinearLayout layout = (LinearLayout) View.inflate(mContext,
						R.layout.select_all_dropdown, null);
				Button btnSelAll = (Button) layout
						.findViewById(R.id.select_all);
				btnSelAll.setVisibility(View.GONE);
				Button btnCancel = (Button) layout.findViewById(R.id.cancel);
				btnCancel.setVisibility(View.VISIBLE);
				btnCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mfInteractionHub.clearSelection();
						ActionMode mode = ((MainActivity) mContext)
								.getActionMode();
						if (mode != null) {
							mode.finish();
						}
						mpopupFilter.dismiss();
					}
				});

				mpopupFilter = new PopupWindow(layout,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				mpopupFilter.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.menu_item_selecter));
				mpopupFilter.setFocusable(true);
				mpopupFilter.setOutsideTouchable(true);
				mpopupFilter.setTouchable(true);

				mpopupFilter.showAsDropDown(v, 0, 0);
			} else {

				LinearLayout layout = (LinearLayout) View.inflate(mContext,
						R.layout.select_all_dropdown, null);
				Button btnSelAll = (Button) layout
						.findViewById(R.id.select_all);
				btnSelAll.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mfInteractionHub.onOperationSelectAll();
						FileUtil.updateActionModeTitle(
								((MainActivity) mContext).getActionMode(),
								mContext, mfInteractionHub
										.getSelectedFileList().size());
						mpopupFilter.dismiss();
					}
				});

				mpopupFilter = new PopupWindow(layout,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				mpopupFilter.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.menu_item_selecter));
				mpopupFilter.setFocusable(true);
				mpopupFilter.setOutsideTouchable(true);
				mpopupFilter.setTouchable(true);

				mpopupFilter.showAsDropDown(v, 0, 0);
			}
		}

	}

}
