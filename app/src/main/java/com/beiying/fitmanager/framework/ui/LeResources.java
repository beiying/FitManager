package com.beiying.fitmanager.framework.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.core.ui.LeUI;

public class LeResources extends BYBasicContainer {
	
	private static final String TAG = "theme";

	static final String THEME_PACKAGE = "com.lenovo.browser.theme";

	private static LeResources sInstance;
	
	private Resources mInnerResources;
	public Resources mOutterResources;
	
	private LeResources() {
		
	}
	
	public static LeResources getInstance() {
		if (sInstance == null) {
			synchronized (LeResources.class) {
				if (sInstance == null) {
					sInstance = new LeResources();
				}
			}
		}
		return sInstance;
	}
	
	public void init(Context context, Resources superResources) {
		// LeBasicContainer, 不用init context!
//		sContext = context;
		mInnerResources = superResources;
	}
	
	public Context getContext() {
		return sContext;
	}
		
	public void setOutterResources(Resources res) {
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
	
	public Drawable getDrawable(Resources res, String bitmapResName, String tileResName, String drawableName) {
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
	
	public ColorStateList getColorStateList(String name) {
		int id = getColorResId(mOutterResources, name, THEME_PACKAGE);
		if (id != 0) {
			return mOutterResources.getColorStateList(id);
		} 
		if (sContext != null) {
			id = getColorResId(mInnerResources, name, sContext.getPackageName());
		}
		if (id != 0) {
			return mInnerResources.getColorStateList(id);
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
	
	public int getInnerColor(String name) {
		int id = 0;
		if (sContext != null) {
			id = getColorResId(mInnerResources, name, sContext.getPackageName());
		}
		if (id != 0) {
			return mInnerResources.getColor(id);
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
	
	public Drawable getInnerDrawable(String name) {
		int id = 0;
		if (sContext != null) {
			id = getDrawableId(mInnerResources, name, sContext.getPackageName());
		}
		if (id != 0) {
			return mInnerResources.getDrawable(id);
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
		int r = LeUI.getDensityDimen(getContext(), radius);
		float[] outerR = new float[] {r, r, r, r, r, r, r, r};
		RoundRectShape shape = new RoundRectShape(outerR, null, null);
		ShapeDrawable drawable = new ShapeDrawable(shape);
		drawable.getPaint().setColor(bgColor);
		shape.resize(width, height);
		return drawable;
	}
	
	public boolean hasColor(String name) {
		int outterId = getColorResId(mOutterResources, name, THEME_PACKAGE);
		int innerId = 0;
		if (sContext != null) {
			innerId = getColorResId(mInnerResources, name, sContext.getPackageName());			
		}
		return (outterId != 0 || innerId != 0);
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
	
	public Drawable getDrawable(Resources res, String name) {
		int id = 0;
		if (res != null) {
			if (res != mInnerResources) {
				id = getDrawableId(res, name, THEME_PACKAGE);
			} else if (res == mInnerResources && sContext != null) {
				id = getDrawableId(res, name, sContext.getPackageName());
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
	
	public int getColor(Resources res, String name) {
		int id = 0;
		if (res != null) {
			if (res != mInnerResources) {
				id = getColorResId(res, name, THEME_PACKAGE);
			} else if (res == mInnerResources && sContext != null) {
				id = getColorResId(res, name, sContext.getPackageName());
			}
			if (id != 0) {
				return res.getColor(id);
			}
		}
		return 0;
	}
	
	public int getDimension(Resources res, String name) {
		int id = 0;
		if (res != null) {
			if (res != mInnerResources) {
				id = getDimenResId(res, name, THEME_PACKAGE);
			} else if (res == mInnerResources && sContext != null) {
				id = getDimenResId(res, name, sContext.getPackageName());
			}
			if (id != 0) {
				return (int) res.getDimension(id);
			}
		}
		return -1;
	}
	
	public int getInteger(Resources res, String name) {
		int id = 0;
		if (res != null) {
			if (res != mInnerResources) {
				id = getIntegerId(res, name, THEME_PACKAGE);
			} else if (res == mInnerResources && sContext != null) {
				id = getIntegerId(res, name, sContext.getPackageName());
			}
			if (id != 0) {
				return (int) res.getInteger(id);
			}
		}
		return -1;
	}
	
	public String getString(Resources res, String name) {
		int id = 0;
		if (res != null) {
			if (res != mInnerResources) {
				id = getStringId(res, name, THEME_PACKAGE);
			} else if (res == mInnerResources && sContext != null) {
				id = getStringId(res, name, sContext.getPackageName());
			}
			if (id != 0) {
				return res.getString(id);
			}
		}
		return null;
	}
	
	private int getDrawableId(Resources resources, String name, String pkgName) {
		if (resources == null) {
			return 0;
		}
		return resources.getIdentifier(name, "drawable", pkgName);
	}
	
	private int getStringId(Resources resources, String name, String pkgName) {
		if (resources == null) {
			return 0;
		}
		return resources.getIdentifier(name, "string", pkgName);
	}
	
	private int getIntegerId(Resources resources, String name, String pkgName) {
		if (resources == null) {
			return 0;
		}
		return resources.getIdentifier(name, "integer", pkgName);
	}
	
	public static int getColorResId(Resources resources, String fieldName, String pkgName) {
		if (resources == null) {
			return 0;
		}
		return resources.getIdentifier(fieldName, "color", pkgName);
	}
	
	public static int getDimenResId(Resources resources, String fieldName, String pkgName) {
		if (resources == null) {
			return 0;
		}
		return resources.getIdentifier(fieldName, "dimen", pkgName);
	}
	
	public static void recyle() {
		sInstance = null;
	}
	
}
