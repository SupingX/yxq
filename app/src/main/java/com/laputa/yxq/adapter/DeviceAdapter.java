package adapter;

import java.util.List;

import com.laputa.yxq.R;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {
	private List<BluetoothDevice> list;
	public DeviceAdapter(List<BluetoothDevice> list){
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
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent,false);
			vh.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			vh.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}
		
		BluetoothDevice device = list.get(position);
		String name = device.getName();
		String address = device.getAddress();
		vh.tvName.setText(name==null?"无名称":name);
		vh.tvAddress.setText(address==null?"无地址":address);
		return convertView;
	}
	
	
	class  ViewHolder{
		TextView tvName;
		TextView tvAddress;
		
		
	}

}
