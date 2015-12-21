package com.xyyy.shop.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.listener.HttpBackListListener;
import com.android.volley.util.VolleyUtil;
import com.baidu.mobstat.StatService;
import com.xyyy.shop.R;
import com.xyyy.shop.ShopApplication;
import com.xyyy.shop.activity.SearchActivity;
import com.xyyy.shop.adapter.ClassifyMenuContentItemAdapter;
import com.xyyy.shop.adapter.ClassifyMenuItemAdapter;
import com.xyyy.shop.model.EnnGoodsCat;
import com.xyyy.shop.toolUtil.CommonVariable;
import com.xyyy.shop.toolUtil.StringUtils;
import com.xyyy.shop.view.CustomProgressDialog;

/**
 * 商品分类的模块
 * 
 * @author lxn
 */
public class ClassifyFragment extends Fragment {

	private RelativeLayout search;// 搜索框的按钮
	private ListView menu_listview;// 左边的分类列表
	private GridView menu_gridview_content;// 右边的详细分类列表
	private ClassifyMenuItemAdapter menuadapter;
	private ClassifyMenuContentItemAdapter menucontentadapter;
	private List<EnnGoodsCat> menulist = new ArrayList<EnnGoodsCat>();// 左边一级分类的数据
	private List<EnnGoodsCat> menucontentlist = new ArrayList<EnnGoodsCat>();// 右边二级分类的数据
	private CustomProgressDialog customProgressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_classify, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		customProgressDialog=new CustomProgressDialog(getActivity(), "正在加载......");
		initdata();
	}
    /**
     * 初始化控件
     */
	private void initView() {
		search = (RelativeLayout) getView().findViewById(R.id.search);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), SearchActivity.class);
				startActivity(intent);
			}
		});
		menu_listview = (ListView) getView().findViewById(R.id.menu_list);
		menu_gridview_content = (GridView) getView().findViewById(
				R.id.menu_list_content);
		//左边 一级分类的
		menuadapter = new ClassifyMenuItemAdapter(getActivity(), menulist);
		menu_listview.setAdapter(menuadapter);
		menu_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String baiduevent=menulist.get(arg2).getCatName();
				if(StringUtils.isBlank(baiduevent)){
					baiduevent="分类条目点击";
				}
				StatService.onEvent(getActivity(),"classify_menu" ,baiduevent);
				smoothScroollListView(arg2);
				menuadapter.setViewBackGround(arg2);
				menuadapter.notifyDataSetChanged();
			}
		});
		// 右边九宫格布局的
		menucontentadapter = new ClassifyMenuContentItemAdapter(getActivity(),
				menucontentlist);
		menu_gridview_content.setAdapter(menucontentadapter);
		menu_gridview_content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Integer id = menucontentlist.get(arg2).getCatId();
				String catname=menucontentlist.get(arg2).getCatName();
				Intent intent = new Intent();
				intent.setClass(getActivity(), SearchActivity.class);
				intent.putExtra("id", id);
				intent.putExtra("catname", catname);
				startActivity(intent);
			}
		});
	}
    /**
     * 加载一级分类的数据
     */
	private void initdata() {
		customProgressDialog.show();
		VolleyUtil.sendStringRequestByGetToList(CommonVariable.GetCatURL
				+ "-100", null, null, EnnGoodsCat.class,
				new HttpBackListListener<EnnGoodsCat>() {

					@Override
					public void onSuccess(List<EnnGoodsCat> t) {
						// TODO 请求数据成功
						menulist = t;
						menuadapter.set_list(menulist);
						menuadapter.setViewBackGround(0);
						menuadapter.notifyDataSetChanged();
						smoothScroollListView(0);
					}

					@Override
					public void onFail(String failstring) {
						// TODO 业务访问失败
						customProgressDialog.dismiss();
						Toast.makeText(getActivity(), "加载数据失败！", 0).show();
					}

					@Override
					public void onError(VolleyError error) {
						// TODO 接口访问失败
						customProgressDialog.dismiss();
						Toast.makeText(getActivity(), "加载数据失败！", 0).show();
					}

				}, false, "");
	}

	/**
	 * 加载右边条目中分类加载的数据
	 * 
	 * @param string
	 */
	private void loadContentData(Integer id) {
		String url = CommonVariable.GetCatURL + id;
		VolleyUtil.sendStringRequestByGetToList(url, null, null,
				EnnGoodsCat.class, new HttpBackListListener<EnnGoodsCat>() {

					@Override
					public void onSuccess(List<EnnGoodsCat> t) {
						// TODO 请求数据成功
						customProgressDialog.dismiss();
						if (t != null) {
							menucontentlist = t;
						} else {
							menucontentlist = new ArrayList<EnnGoodsCat>();
						}
						menucontentadapter.set_list(menucontentlist);
						menucontentadapter.notifyDataSetChanged();
					}

					@Override
					public void onFail(String failstring) {
						// TODO 业务访问失败
						customProgressDialog.dismiss();
						Toast.makeText(getActivity(), "加载数据失败！", 0).show();
						menucontentlist = new ArrayList<EnnGoodsCat>();
				    	menucontentadapter.set_list(menucontentlist);
				    	menucontentadapter.notifyDataSetChanged();
					}

					@Override
					public void onError(VolleyError error) {
						// TODO 接口访问失败
						customProgressDialog.dismiss();
						Toast.makeText(getActivity(), "加载数据失败！", 0).show();
						menucontentlist = new ArrayList<EnnGoodsCat>();
				     	menucontentadapter.set_list(menucontentlist);
				    	menucontentadapter.notifyDataSetChanged();
					}

				}, false, "");
	}

	

	/**
	 * listView scroll
	 * 
	 * @param position
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void smoothScroollListView(int position) {
		if (Build.VERSION.SDK_INT >= 21) {
			menu_listview.setSelectionFromTop(position, 0);
		} else if (Build.VERSION.SDK_INT >= 11) {
			menu_listview.smoothScrollToPositionFromTop(position, 0, 500);
		} else if (Build.VERSION.SDK_INT >= 8) {
			int firstVisible = menu_listview.getFirstVisiblePosition();
			int lastVisible = menu_listview.getLastVisiblePosition();
			if (position < firstVisible) {
				menu_listview.smoothScrollToPosition(position);
			} else {
				if (firstVisible == 0) {
					menu_listview.smoothScrollToPosition(position + lastVisible
							- firstVisible);
				} else {
					menu_listview.smoothScrollToPosition(position + lastVisible
							- firstVisible - 1);
				}
			}
		} else {
			menu_listview.setSelection(position);
		}
		// TODO 根据左边点击条目的数据 获取右边表格布局中的数据
		if(!customProgressDialog.isShowing()){
			customProgressDialog.show();
		}
		if(menulist.size()>0){
			loadContentData(menulist.get(position).getCatId());
		}
	}
	
	
	/**
	 * 当前fragment显示状态发生改变时执行的方法 隐藏是 hidden值为true
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden) {
			if(null!=menulist&&menulist.size()>0){
				
			}else{
				initdata();
			}
			StatService.onPageStart(getActivity(),	"分类模块");
		}else{
			StatService.onPageEnd(getActivity(),"分类模块");
		}
		super.onHiddenChanged(hidden);
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(ShopApplication.mainflag==1)
		StatService.onPageStart(getActivity(),	"分类模块");
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(ShopApplication.mainflag==1)
		StatService.onPageEnd(getActivity(),"分类模块");
	}
}
