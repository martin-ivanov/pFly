package com.unisofia.fmi.pfly.ui.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.ui.adapter.MenuAdapter;

public class MenuFragment extends Fragment {

	public interface MenuListener {
		void onMenuItemSelected(MenuItem item);
	}

	public enum MenuItem {

		PROJECTS(R.string.menu_projects),
		TASKS(R.string.menu_tasks),
		SUBSCRIBE(R.string.menu_subscribe), 
		LOGOUT(R.string.menu_logout);

		private int mTitle;

		private MenuItem(int title) {
			mTitle = title;
		}

		public int getTitle() {
			return mTitle;
		}
	}

	private ListView mMenuListView;
	private MenuAdapter mMenuAdapter;

	private MenuListener mListener;

	public void setMenuListener(MenuListener listener) {
		this.mListener = listener;
	}
	
	public void setSelectedItem(MenuItem item) {
		int position = mMenuAdapter.getItemIndex(item);
		mMenuListView.setItemChecked(position, true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_menu, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		List<MenuItem> items = new ArrayList<MenuItem>();
		items.addAll(Arrays.asList(MenuItem.values()));

		mMenuAdapter = new MenuAdapter(items);
		mMenuListView = (ListView) view.findViewById(R.id.listview_menu);
		mMenuListView.setAdapter(mMenuAdapter);
		mMenuListView.setItemChecked(0, true);
		mMenuListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mListener != null) {
					mListener.onMenuItemSelected(mMenuAdapter.getItem(position));
				}
			}
		});
	}
}
