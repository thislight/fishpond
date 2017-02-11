package fish.pondof.s.utils;

import panva.Matcher;
import android.view.MenuItem;
import android.util.Log;
import fish.pondof.s.StaticValue;

public class MenuItemMatcher extends Matcher<MenuItem>
{
	public MenuItemMatcher(MenuItem item){
		super(item);
	}
	
	/*
	@Override
	public boolean compare(MenuItem p1,MenuItem p2){
		boolean r = (p1.getGroupId() == p2.getGroupId()) && (p1.getItemId() == p2.getItemId());
		Log.d(StaticValue.LOG_TAG,"[MenuItemMatcher]: Return "+r);
		return r;
	}
	*/
}
