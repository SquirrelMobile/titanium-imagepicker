/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2018 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.imagepicker;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.titanium.util.TiActivityResultHandler;
import org.appcelerator.titanium.TiBlob;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.media.ExifInterface;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.zhihu.matisse.Matisse;

@Kroll.module(name="TitaniumImagepicker", id="ti.imagepicker")
public class TitaniumImagepickerModule extends KrollModule implements TiActivityResultHandler
{
	// Standard Debugging variables
	private static final String LCAT = "TitaniumImagepickerModule";
	private static final boolean DBG = TiConfig.LOGD;

	private KrollFunction callback;
	protected int requestCode;

	@Kroll.method(runOnUiThread = true)
	public void openGallery(KrollDict args)
	{
		callback = (KrollFunction) args.get("callback");

		Activity activity = TiApplication.getInstance().getCurrentActivity();
		TiActivitySupport support = (TiActivitySupport) activity;
		requestCode = support.getUniqueResultCode();

		int maxImageSelection = args.optInt("maxImageSelection", 3);
		
		Intent matisseIntent = new Intent(activity, TiMatisseActivity.class);
		matisseIntent.putExtra(TiMatisseActivity.PROPERTY_MAX_IMAGE_SELECTION, maxImageSelection);
		
		support.launchActivityForResult(matisseIntent, requestCode, this);
	}

	@Override
	public void onResult(Activity activity, int thisRequestCode, int resultCode, Intent data)
	{
		if (callback == null) return;

		if (thisRequestCode == requestCode && data != null) {
			List<String> paths = Matisse.obtainPathResult(data);
			ArrayList<TiBlob> images = new ArrayList<>();

			for (String url : paths) {
				TiBlob image = computeBitmap(url);
				if (image == null) continue;
				images.add(image);
			}

			KrollDict event = new KrollDict();
			event.put("success", true);
			event.put("images", images.toArray());

			callback.callAsync(getKrollObject(), event);
		} else {
			KrollDict event = new KrollDict();
			event.put("success", false);
			event.put("cancel", true);
			callback.callAsync(getKrollObject(), event);	
		}
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private TiBlob computeBitmap(String url) {
		ExifInterface exif = null;

		try {
			exif = new ExifInterface(url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(TiApplication.getInstance().getContentResolver(), Uri.fromFile(new File(url)));

			bitmap = rotateBitmap(bitmap, orientation);
			TiBlob blob = TiBlob.blobFromImage(bitmap);
			bitmap = null;

			return blob;
		} catch (IOException ex) {
			Log.e(LCAT, "Cannot receive bitmap at path = " + url);
		} catch (OutOfMemoryError ex) {
			Log.e(LCAT, "Memory error while decoding image bitmap at path = " + url);
		}

		return null;
	}

	@Override
	public void onError(Activity activity, int requestCode, Exception e)
	{
		if (callback == null) return;

		KrollDict event = new KrollDict();
		event.put("success", false);
		callback.callAsync(getKrollObject(), event);
	}

	private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

		Matrix matrix = new Matrix();
		switch (orientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				return bitmap;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				matrix.setScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.setRotate(180);
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				matrix.setRotate(180);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				matrix.setRotate(90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.setRotate(90);
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				matrix.setRotate(-90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.setRotate(-90);
				break;
			default:
				return bitmap;
		}
		try {
			Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bitmap.recycle();
			return bmRotated;
		}
		catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

