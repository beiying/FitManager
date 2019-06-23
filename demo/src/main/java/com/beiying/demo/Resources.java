package com.beiying.demo;

import android.content.Context;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

public class Resources {
	
	private static final String TAG = "theme";

	static final String THEME_PACKAGE = "com.lenovo.browser.theme";

	private static Resources sInstance;
	private Context mContext;
	
	private android.content.res.Resources mInnerResources;
	public android.content.res.Resources mOutterResources;
	
	private Resources(Context context) {
		mContext = context;
	}
	
	public static Resources getInstance(Context context) {
		if (sInstance == null) {
			synchronized (Resources.class) {
				if (sInstance == null) {
					sInstance = new Resources(context);
				}
			}
		}
		return sInstance;
	}
	
	public void init(Context context, android.content.res.Resources superResources) {
		// LeBasicContainer, 不用init context!
//		mContext = context;
		mInnerResources = superResources;
	}
	

	public void setOutterResources(android.content.res.Resources res) {
		mOutterResources = res;       
	}
	
	public String getString(String name) {
		String string = getString(mOutterResources, name);
		if (string == null) {
			string = getString(mInnerResources, name);
		}
		return string;
	}
	
	public int getInteger(String name) {
		int result = getInteger(mOutterResources, name);
		if (result == -1) {
			result = getInteger(mInnerResources, name);
		}
		return result;
	}
	
	public Drawable getOutterDrawable(String bitmapResName, String tileResName, String drawableName) {
		return getDrawable(mOutterResources, bitmapResName, tileResName, drawableName);
	}
	
	public Drawable getInnerDrawable(String bitmapResName, String tileResName, String drawableName) {
		return getDrawable(mInnerResources, bitmapResName, tileResName, drawableName);
	}
	
	public Drawable getDrawable(android.content.res.Resources res, String bitmapResName, String tileResName, String drawableName) {
		Drawable drawable;
		drawable = getDrawable(res, bitmapResName);
		if (drawable != null) {
			return drawable;
		}
		drawable = getDrawable(res, tileResName);
		if (drawable != null) {
			BitmapDrawable tileDrawable = (BitmapDrawable) drawable;
			tileDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			return tileDrawable;
		}
		drawable = getDrawable(res, drawableName);
		if (drawable != null) {
			return drawable;
		}
		return null;
	}
	

	public int getOutterColor(String name) {
		int id = getColorResId(mOutterResources, name, THEME_PACKAGE);
		if (id != 0) {
			return mOutterResources.getColor(id);
		}
		return 0;
	}
	

	public Drawable getOutterDrawable(String name) {
		int id = getDrawableId(mOutterResources, name, THEME_PACKAGE);
		if (id != 0) {
			return mOutterResources.getDrawable(id);
		}
		return null;
	}

	public Drawable getDrawable(String name) {
		Drawable drawable = getDrawable(mOutterResources, name);
		if (drawable != null) {
			return drawable;
		}
		return getDrawable(mInnerResources, name);
	}

	public Drawable getRoundRectDrawable(int width, int height, int radius, int bgColor) {
		int r = UI.getDensityDimen(mContext, radius);
		float[] outerR = new float[] {r, r, r, r, r, r, r, r};
		RoundRectShape shape = new RoundRectShape(outerR, null, null);
		ShapeDrawable drawable = new ShapeDrawable(shape);
		drawable.getPaint().setColor(bgColor);
		shape.resize(width, height);
		return drawable;
	}
	
	public int getColor(String name) {
		int color = getColor(mOutterResources, name);
		if (color != 0) {
			return color;
		}
		return getColor(mInnerResources, name);
	}
	
	public float getDimension(String name) {
		int dimension = getDimension(mOutterResources, name);
		if (dimension != -1) {
			return dimension;
		}
		return getDimension(mInnerResources, name);
	}
	
	public int getOutterColorResId(String name) {
		return getColorResId(mOutterResources, name, THEME_PACKAGE);
	}
	
	public Drawable getDrawable(android.content.res.Resources res, String name) {
		int id = 0;
		if (res != null) {
			if (res != mInnerResources) {
				id = getDrawableId(res, name, THEME_PACKAGE);
			} else if (res == mInnerResources && mContext != null) {
				id = getDrawableId(res, name, mContext.getPackageName());
			}
			if (id != 0) {
				if (res == null) {
					return null;
				}
				return res.getDrawable(id);
			}
		}
		return null;
	}
	
	public int getColor(android.content.res.Resources res, String name) {
		int id = 0;
		if (res != null) {
			if (res != mInnerResources) {
				id = getColorResId(res, name, THEME_PACKAGE);
			} else if (res == mInnerResources && mContext != null) {
				id = getColorResId(res, name, mContext.getPackageName());
			}
			if (id != 0) {
				return res.getColor(id);
			}
		}
		return 0;
	}
	
	public int getDimension(android.content.res.Resources res, String name) {
		int id = 0;
		if (res != null) {
			if (res != mInnerResources) {
				id = getDimenResId(res, name, THEME_PACKAGE);
			} else if (res == mInnerResources && mContext != null) {
				id = getDimenResId(res, name, mContext.getPackageName());
			}
			if (id != 0) {
				return (int) res.getDimension(id);
			}
		}
		return -1;
	}
	
	public int getInteger(android.content.res.Resources res, String name) {
		int id = 0;
		if (res != null) {
			if (res != mInnerResources) {
				id = getIntegerId(res, name, THEME_PACKAGE);
			} else if (res == mInnerResources && mContext != null) {
				id = getIntegerId(res, name, mContext.getPackageName());
			}
			if (id != 0) {
				return (int) res.getInteger(id);
			}
		}
		return -1;
	}
	
	public String getString(android.content.res.Resources res, String name) {
		int id = 0;
		if (res != null) {
			if (res != mInnerResources) {
				id = getStringId(res, name, THEME_PACKAGE);
			} else if (res == mInnerResources && mContext != null) {
				id = getStringId(res, name, mContext.getPackageName());
			}
			if (id != 0) {
				return res.getString(id);
			}
		}
		return null;
	}
	
	private int getDrawableId(android.content.res.Resources resources, String name, String pkgName) {
		if (resources == null) {
			return 0;
		}
		return resources.getIdentifier(name, "drawable", pkgName);
	}
	
	private int getStringId(android.content.res.Resources resources, String name, String pkgName) {
		if (resources == null) {
			return 0;
		}
		return resources.getIdentifier(name, "string", pkgName);
	}
	
	private int getIntegerId(android.content.res.Resources resources, String name, String pkgName) {
		if (resources == null) {
			return 0;
		}
		return resources.getIdentifier(name, "integer", pkgName);
	}
	
	public static int getColorResId(android.content.res.Resources resources, String fieldName, String pkgName) {
		if (resources == null) {
			return 0;
		}
		return resources.getIdentifier(fieldName, "color", pkgName);
	}
	
	public static int getDimenResId(android.content.res.Resources resources, String fieldName, String pkgName) {
		if (resources == null) {
			return 0;
		}
		return resources.getIdentifier(fieldName, "dimen", pkgName);
	}
	
	public static void recyle() {
		sInstance = null;
	}
	
}
