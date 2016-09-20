package adapter;

import java.util.List;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.laputa.yxq.R;

import bean.MenuItem;

public class MenuAdapter extends BaseAdapter {
	private List<MenuItem> list;
	public MenuAdapter(List<MenuItem> list){
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent,false);
			vh.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			vh.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
			vh.ivMenu = (ImageView) convertView.findViewById(R.id.iv_menu);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}

		MenuItem menuItem = list.get(position);
		String name = menuItem.getName();
		String desc = menuItem.getDesc();
		int img = menuItem.getImg();
		vh.tvTitle.setText(name==null?"标题":name);
		vh.tvDesc.setText(desc==null?"描述":desc);
		vh.ivMenu.setImageResource(img<=0?R.drawable.ic_yxq:img);


		return convertView;
	}


	class  ViewHolder{
		TextView tvTitle;
		TextView tvDesc;
		ImageView ivMenu;


	}

}
