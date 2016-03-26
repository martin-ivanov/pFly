package com.unisofia.fmi.pfly.ui.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.ui.fragment.MenuFragment.MenuItem;

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

}