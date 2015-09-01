package com.ikabi.apps.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ikabi.apps.R;
import com.ikabi.apps.apis.FileCategoryHelper;
import com.ikabi.apps.apis.FileCategoryHelper.CategoryInfo;
import com.ikabi.apps.apis.FileCategoryHelper.FileCategoryType;
import com.ikabi.apps.ui.MainActivity;
import com.ikabi.apps.utils.MenuUtils.MenuItemType;

import java.util.HashMap;

public class SlidingMenuFragment extends Fragment implements OnClickListener{


	private RelativeLayout device;
	

	private RelativeLayout favorite;


	private RelativeLayout wifi;
	

	private RelativeLayout music;
	private TextView       musicnum;
		

	private RelativeLayout video;
	private TextView       videonum;
		

	private RelativeLayout image;
	private TextView       imagenum;
		

	private RelativeLayout document;
	private TextView       documentnum;
		

	private RelativeLayout zip;
	private TextView       zipnum;
		

	private RelativeLayout apk;
	private TextView apknum;

	private RelativeLayout favo;
	private TextView       favnum;
	

	private MenuItemType currentmenuItemType = MenuItemType.MENU_DEVICE;
	
	private FileCategoryHelper mFileCategoryHelper;
	
	private MainActivity mActivity;
	
	private HashMap<FileCategoryType,Long> fileNums = new HashMap<FileCategoryType, Long>();
	
	public void SlidingMenuFragment(){
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	
    	mActivity = (MainActivity)getActivity();
    	mActivity.setSlidingMenuFragment(this);
    	
    	View menu = inflater.inflate(R.layout.main_slidingmenu,container, false);
    	device   = (RelativeLayout)menu.findViewById(R.id.device);
    	favorite = (RelativeLayout)menu.findViewById(R.id.favorite);
    	wifi     = (RelativeLayout)menu.findViewById(R.id.wifi);
    	music    = (RelativeLayout)menu.findViewById(R.id.music);
    	musicnum = (TextView)menu.findViewById(R.id.music_num);
    	image    = (RelativeLayout)menu.findViewById(R.id.image);
    	imagenum = (TextView)menu.findViewById(R.id.image_num);
    	video    = (RelativeLayout)menu.findViewById(R.id.video);
    	videonum = (TextView)menu.findViewById(R.id.video_num);
    	document = (RelativeLayout)menu.findViewById(R.id.document);
    	documentnum = (TextView)menu.findViewById(R.id.document_num);
    	zip      = (RelativeLayout)menu.findViewById(R.id.zip);
    	zipnum = (TextView)menu.findViewById(R.id.zip_num);
    	apk      = (RelativeLayout)menu.findViewById(R.id.apk);
		apknum = (TextView)menu.findViewById(R.id.apk_num);
		favo      = (RelativeLayout)menu.findViewById(R.id.favo);
		favnum = (TextView)menu.findViewById(R.id.fav_num);
    	
    	mFileCategoryHelper = new FileCategoryHelper(mActivity);
    	mFileCategoryHelper.refreshCategoryInfo();
    	

    	device.setOnClickListener(this);
    	favorite.setOnClickListener(this);
    	wifi.setOnClickListener(this);
    	music.setOnClickListener(this);
    	image.setOnClickListener(this);
    	video.setOnClickListener(this);
    	document.setOnClickListener(this);
    	zip.setOnClickListener(this);
    	apk.setOnClickListener(this);
    
    	showFileNum();
    	return menu;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    }
    
    public boolean SelMenu(MenuItemType menuType){
    	
    	device.setBackgroundResource(R.drawable.menu_item_selecter);
		device.getChildAt(0).setVisibility(View.GONE);
		
		favorite.setBackgroundResource(R.drawable.menu_item_selecter);
		favorite.getChildAt(0).setVisibility(View.GONE);
		
		wifi.setBackgroundResource(R.drawable.menu_item_selecter);
		wifi.getChildAt(0).setVisibility(View.GONE);
		
		image.setBackgroundResource(R.drawable.menu_item_selecter);
		image.getChildAt(0).setVisibility(View.GONE);
		
		music.setBackgroundResource(R.drawable.menu_item_selecter);
		music.getChildAt(0).setVisibility(View.GONE);
		
		video.setBackgroundResource(R.drawable.menu_item_selecter);
		video.getChildAt(0).setVisibility(View.GONE);
		
		document.setBackgroundResource(R.drawable.menu_item_selecter);
		document.getChildAt(0).setVisibility(View.GONE);
		
		zip.setBackgroundResource(R.drawable.menu_item_selecter);
		zip.getChildAt(0).setVisibility(View.GONE);
		
		apk.setBackgroundResource(R.drawable.menu_item_selecter);
		apk.getChildAt(0).setVisibility(View.GONE);

		favo.setBackgroundResource(R.drawable.menu_item_selecter);
		favo.getChildAt(0).setVisibility(View.GONE);
		
		switch (menuType) {
		case MENU_DEVICE:
			device.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			device.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_FAVORITE:
			favorite.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			favorite.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_WIFI:
			wifi.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			wifi.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_VIDEO:
			video.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			video.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_MUSIC:
			music.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			music.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
			
		case MENU_DOCUMENT:
		    document.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			document.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_ZIP:
			zip.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			zip.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_APK:
			apk.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			apk.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_IMAGE:
			image.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			image.getChildAt(0).setVisibility(View.VISIBLE);
			break;

		case MENU_FAVORITE1:
			favo.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			favo.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		default:
			break;
		}
		
		currentmenuItemType = menuType;
		return true;
    }
    
    
    private void OpenFragment(MenuItemType menuType){
    	if(getActivity() != null && getActivity() instanceof MainActivity){
    		((MainActivity)getActivity()).setShowSelFragments(menuType);
    	}
    	
    	return;
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.device:
			OpenFragment(MenuItemType.MENU_DEVICE);
			break;
			
		case R.id.favorite:
			OpenFragment(MenuItemType.MENU_FAVORITE);
			break;
			
		case R.id.wifi:
			OpenFragment(MenuItemType.MENU_WIFI);
			break;
			
		case R.id.video:
			OpenFragment(MenuItemType.MENU_VIDEO);
			break;
			
		case R.id.music:
			OpenFragment(MenuItemType.MENU_MUSIC);
			break;
			
			
		case R.id.document:
			OpenFragment(MenuItemType.MENU_DOCUMENT);
			break;
			
		case R.id.zip:
			OpenFragment(MenuItemType.MENU_ZIP);
			break;
			
		case R.id.apk:
			OpenFragment(MenuItemType.MENU_APK);
			break;
			
		case R.id.image:
			OpenFragment(MenuItemType.MENU_IMAGE);
			break;

		case R.id.favo:
			OpenFragment(MenuItemType.MENU_FAVORITE1);
			break;
			
		default:
			break;
		}
	}
	
	public void updatefilenum(){
		mFileCategoryHelper.refreshCategoryInfo();
		showFileNum();
	}
	
	
	private void showFileNum(){
		
		String sNumFormat = getResources().getString(R.string.file_num);
		
		CategoryInfo fCategoryInfo = mFileCategoryHelper.getCategoryInfo(FileCategoryType.Music);
		fileNums.put(FileCategoryType.Music, Long.valueOf(fCategoryInfo.count));
		musicnum.setText(String.format(sNumFormat, Long.valueOf(fCategoryInfo.count).toString()));
		
		fCategoryInfo = mFileCategoryHelper.getCategoryInfo(FileCategoryType.Video);
		fileNums.put(FileCategoryType.Video, Long.valueOf(fCategoryInfo.count));
		videonum.setText(String.format(sNumFormat, Long.valueOf(fCategoryInfo.count).toString()));
		
		fCategoryInfo = mFileCategoryHelper.getCategoryInfo(FileCategoryType.Doc);
		fileNums.put(FileCategoryType.Doc, Long.valueOf(fCategoryInfo.count));
		documentnum.setText(String.format(sNumFormat, Long.valueOf(fCategoryInfo.count).toString()));
		
		fCategoryInfo = mFileCategoryHelper.getCategoryInfo(FileCategoryType.Zip);
		fileNums.put(FileCategoryType.Zip, Long.valueOf(fCategoryInfo.count));
		zipnum.setText(String.format(sNumFormat, Long.valueOf(fCategoryInfo.count).toString()));
		
		fCategoryInfo = mFileCategoryHelper.getCategoryInfo(FileCategoryType.Apk);
		fileNums.put(FileCategoryType.Apk, Long.valueOf(fCategoryInfo.count));
		apknum.setText(String.format(sNumFormat, Long.valueOf(fCategoryInfo.count).toString()));
		
		fCategoryInfo = mFileCategoryHelper.getCategoryInfo(FileCategoryType.Picture);
		fileNums.put(FileCategoryType.Picture, Long.valueOf(fCategoryInfo.count));
		imagenum.setText(String.format(sNumFormat, Long.valueOf(fCategoryInfo.count).toString()));

		fCategoryInfo = mFileCategoryHelper.getCategoryInfo(FileCategoryType.Favorite1);
		fileNums.put(FileCategoryType.Favorite1, Long.valueOf(fCategoryInfo.count));
		favnum.setText(String.format(sNumFormat, Long.valueOf(fCategoryInfo.count).toString()));
		
	}
	

	public long getFilenum(FileCategoryType fc) {
		if (fileNums.containsKey(fc)) {
			return fileNums.get(fc).longValue();
		} else {
			return 0;
		}
	}
	
}
