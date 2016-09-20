package bean;

import android.app.Activity;

public class MenuItem {
	private int index;
	private String name;
	private String desc;
	private int img;
	private Class<? extends Activity> clz;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Class<? extends Activity> getClz() {
		return clz;
	}
	public void setClz(Class<? extends Activity> clz) {
		this.clz = clz;
	}
	public MenuItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MenuItem(int index, String name, String desc,
			Class<? extends Activity> clz) {
		super();
		this.index = index;
		this.name = name;
		this.desc = desc;
		this.clz = clz;
	}
	public int getImg() {
		return img;
	}
	public void setImg(int img) {
		this.img = img;
	}
	public MenuItem(int index, String name, String desc, int img,
			Class<? extends Activity> clz) {
		super();
		this.index = index;
		this.name = name;
		this.desc = desc;
		this.img = img;
		this.clz = clz;
	}
	
	
	
}
