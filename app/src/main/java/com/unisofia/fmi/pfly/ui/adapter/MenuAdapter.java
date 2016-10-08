package com.unisofia.fmi.pfly.ui.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unisofia.fmi.pfly.R;

public class MenuAdapter extends BaseAdapter {

	private List<MenuItem> mMenuItems;

	public MenuAdapter(List<MenuItem> menuItems) {
		mMenuItems = menuItems;
	}
	
	public int getItemIndex(MenuItem item) {
		return mMenuItems.indexOf(item);
	}

	@Override
	public int getCount() {
		return mMenuItems.size();
	}

	@Override
	public MenuItem getItem(int position) {
		return mMenuItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.list_item_menu, parent,
					false);

			holder = new ViewHolder();
			holder.mTitleTextView = (TextView) convertView
					.findViewById(R.id.textview_title);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MenuItem item = getItem(position);
		holder.mTitleTextView.setText(item.getTitle());

		return convertView;
	}

	static class ViewHolder {
		TextView mTitleTextView;
	}

	public enum MenuItem {

		PROJECTS(R.string.nav_projects),
		TASKS(R.string.nav_my_tasks),
		CALENDAR(R.string.nav_calendar),
		ADVANCED_SEARCH(R.string.nav_advanced_search),
		LOGOUT(R.string.nav_logout);

		private int mTitle;

		private MenuItem(int title) {
			mTitle = title;
		}

		public int getTitle() {
			return mTitle;
		}
	}

}