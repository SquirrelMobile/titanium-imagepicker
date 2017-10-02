/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package in.prashant.imagepicker;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.kroll.common.Log;

import android.os.Build;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.widget.Toast;



@Kroll.module(name="Imagepicker", id="in.prashant.imagepicker")
public class ImagepickerModule extends KrollModule
{	
	@Kroll.constant
	public static final int SHAPE_CIRCLE = Defaults.SHAPE_CIRCLE;
	
	@Kroll.constant
	public static final int SHAPE_SQUARE = Defaults.SHAPE_SQUARE;
	

	public ImagepickerModule() {
		super();
	}

	
	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		
	}
	
	private Intent prepareExtrasForIntent(Intent intent, KrollDict options) {
		Defaults.resetValues();
		
		checkAndSetParameters(1, Defaults.Params.STATUS_BAR_COLOR, Defaults.STATUS_BAR_COLOR, options, intent);
		checkAndSetParameters(1, Defaults.Params.BAR_COLOR, Defaults.BAR_COLOR, options, intent);
		checkAndSetParameters(1, Defaults.Params.BACKGROUND_COLOR, Defaults.BACKGROUND_COLOR, options, intent);
		checkAndSetParameters(1, Defaults.Params.COVER_VIEW_COLOR, Defaults.COVER_VIEW_COLOR, options, intent);
		checkAndSetParameters(1, Defaults.Params.CHECKMARK_COLOR, Defaults.CHECKMARK_COLOR, options, intent);
		checkAndSetParameters(1, Defaults.Params.TITLE, Defaults.TITLE, options, intent);
		checkAndSetParameters(1, Defaults.Params.DONE_BTN_TITLE, Defaults.DONE_BTN_TITLE, options, intent);
		checkAndSetParameters(1, Defaults.Params.MAX_IMAGE_MSG, Defaults.MAX_IMAGE_MSG, options, intent);
		checkAndSetParameters(2, Defaults.Params.GRID_SIZE, Defaults.GRID_SIZE, options, intent);
		checkAndSetParameters(2, Defaults.Params.IMAGE_HEIGHT, Defaults.IMAGE_HEIGHT, options, intent);
		checkAndSetParameters(2, Defaults.Params.SHOW_DIVIDER, Defaults.SHOW_DIVIDER, options, intent);
		checkAndSetParameters(2, Defaults.Params.DIVIDER_WIDTH, Defaults.DIVIDER_WIDTH, options, intent);
		checkAndSetParameters(2, Defaults.Params.MAX_IMAGE_SELECTION, Defaults.MAX_IMAGE_SELECTION, options, intent);
		checkAndSetParameters(2, Defaults.Params.SHAPE, Defaults.SHAPE, options, intent);
		checkAndSetParameters(2, Defaults.Params.CIRCLE_RADIUS, Defaults.CIRCLE_RADIUS, options, intent);
		checkAndSetParameters(2, Defaults.Params.CIRCLE_PADDING, Defaults.CIRCLE_PADDING, options, intent);
	    
		return intent;
	}
	
	// set extras received as dictionary from Titanium app, if not, then put their default value
	// (String = Type 1) & (Integer = Type 2)
	private void checkAndSetParameters(int type, String key, Object defaultValue, KrollDict options, Intent intent) {
		if (1 == type) {
			intent.putExtra(key, (String) (options.containsKeyAndNotNull(key) ? options.get(key) : defaultValue));
			
		} else if (2 == type) {
			intent.putExtra(key, (Integer) (options.containsKeyAndNotNull(key) ? options.get(key) : defaultValue));
		}
	}
	
	private boolean hasStoragePermissions() {
		if (Build.VERSION.SDK_INT < 23) {
			return true;
		}
		
		Context context = TiApplication.getInstance().getApplicationContext();
		
		if (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		
		return false;
    }
	
	
	@Kroll.method
	public void openGallery(@Kroll.argument(optional=true) KrollDict options) {
		if (!hasStoragePermissions()) {
			Log.e(Defaults.LCAT, "Storage permissions are denied.");
			Toast.makeText(TiApplication.getAppCurrentActivity().getApplicationContext(), "Storage permissions are denied.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		boolean isOption = null != options;
		
		KrollFunction callback = null;
		
		if (isOption) {
			if (options.containsKeyAndNotNull(Defaults.Params.CALLBACK)) {
				if (options.get(Defaults.Params.CALLBACK) instanceof KrollFunction) {
					callback = (KrollFunction) options.get(Defaults.Params.CALLBACK);
				} 
			}
		}
		
		GalleryResultHandler handler = new GalleryResultHandler(callback, getKrollObject());
		
		Activity activity = TiApplication.getAppCurrentActivity();
		Intent intent = new Intent(activity, ImagePickerActivity.class);
		
		if (isOption) {
			((TiActivitySupport) activity).launchActivityForResult(prepareExtrasForIntent(intent, options), Defaults.REQUEST_CODE, handler);
			
		} else {
			activity.startActivity(intent);
		}
	}
	
	
	@Kroll.method
	public TiBlob getImage(String filePath) {
        if (null != filePath) {
            return TiBlob.blobFromImage( BitmapFactory.decodeFile(filePath) );
        }
        Log.e(Defaults.LCAT, "File path missing");
        return null;
    }
	
	
	@Kroll.method
    public TiBlob resizeAsAspect(String filePath, int reqWidth, int reqHeight) {
        return Blobby.rescale(filePath, reqWidth, reqHeight, true);
    }

	
	@Kroll.method
    public TiBlob resizeAsSame(String filePath, int reqWidth, int reqHeight) {
        return Blobby.rescale(filePath, reqWidth, reqHeight, false);
    }
}





