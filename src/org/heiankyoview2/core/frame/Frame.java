package org.heiankyoview2.core.frame;

public class Frame {
	TreeFrame tf = null;
	Object valuearray[] = null;
	double time = 0.0;
	int id;
	
	/**
	 * Constructor
	 */
	public Frame() {
	}
	
	
	/**
	 * Constructor
	 */
	public Frame(TreeFrame tf) {
		this.tf = tf;
		valuearray = new Object[tf.getNumValues()];
		for(int i = 0; i < valuearray.length; i++) {
			int type = tf.getValueType(i + 1);
			switch(type) {
			case TreeFrame.FRAME_STRING:
				FrameString fstring = new FrameString(tf.getNumNodes());
				valuearray[i] = (Object)fstring;
				break;
			case TreeFrame.FRAME_DOUBLE:
				FrameDouble fdouble = new FrameDouble(tf.getNumNodes());
				valuearray[i] = (Object)fdouble;
				break;
			case TreeFrame.FRAME_INT:
				FrameInt fint = new FrameInt(tf.getNumNodes());
				valuearray[i] = (Object)fint;
				break;
			}
		}
	}
	
	
	/**
	 * TreeFrameをセットする
	 * @param tf
	 */
	public void setTreeFrame(TreeFrame tf) {
		this.tf = tf;
	}
	
	
	/**
	 * TreeFrameをゲットする
	 * @return
	 */
	public TreeFrame getTreeFrame() {
		return tf;
	}
	
	
	/**
	 * IDをセットする
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
	/**
	 * IDをゲットする
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	
	/**
	 * 時刻をセットする
	 * @param time
	 */
	public void setTime(double time) {
		this.time = time;
	}
	
	
	/**
	 * 時刻をゲットする
	 * @return
	 */
	public double getTime() {
		return time;
	}
	
	
	/**
	 * String型のValueをセットする
	 * @param valueid
	 * @param nodeid
	 * @param value
	 */
	public void set(int valueid, int nodeid, String value) {
		if(valueid <= 0 || valueid > valuearray.length) {
			System.out.println("  Frame: valueid " + valueid + " is out of range.");
			return;
		}
		FrameString fstring = (FrameString)valuearray[valueid - 1];
		fstring.set(nodeid, value);
	}
	
	
	/**
	 * double型のValueをセットする
	 * @param valueid
	 * @param nodeid
	 * @param value
	 */
	public void set(int valueid, int nodeid, double value) {
		if(valueid <= 0 || valueid > valuearray.length) {
			System.out.println("  Frame: valueid " + valueid + " is out of range.");
			return;
		}
		FrameDouble fdouble = (FrameDouble)valuearray[valueid - 1];
		fdouble.set(nodeid, value);
	}
	
	
	/**
	 * int型のValueをセットする
	 * @param valueid
	 * @param nodeid
	 * @param value
	 */
	public void set(int valueid, int nodeid, int value) {
		if(valueid <= 0 || valueid > valuearray.length) {
			System.out.println("  Frame: valueid " + valueid + " is out of range.");
			return;
		}
		FrameInt fint = (FrameInt)valuearray[valueid - 1];
		fint.set(nodeid, value);
	}
	
	
	/**
	 * String型のValueをゲットする
	 * @param valueid
	 * @param nodeid
	 */
	public String getString(int valueid, int nodeid) {
		if(valueid <= 0 || valueid > valuearray.length) {
			System.out.println("  Frame: valueid " + valueid + " is out of range.");
			return null;
		}
		FrameString fstring = (FrameString)valuearray[valueid - 1];
		return fstring.get(nodeid);
	}
	
	
	/**
	 * double型のValueをゲットする
	 * @param valueid
	 * @param nodeid
	 */
	public double getDouble(int valueid, int nodeid) {
		if(valueid <= 0 || valueid > valuearray.length) {
			System.out.println("  Frame: valueid " + valueid + " is out of range.");
			return 0.0;
		}
		FrameDouble fdouble = (FrameDouble)valuearray[valueid - 1];
		return fdouble.get(nodeid);
	}
	
	
	/**
	 * int型のValueをゲットする
	 * @param valueid
	 * @param nodeid
	 */
	public int getInt(int valueid, int nodeid) {
		if(valueid <= 0 || valueid > valuearray.length) {
			System.out.println("  Frame: valueid " + valueid + " is out of range.");
			return 0;
		}
		FrameInt fint = (FrameInt)valuearray[valueid - 1];
		return fint.get(nodeid);
	}
	
	/**
	 * 格納する値の最大値を double 形式で指定する
	 */
	public void setMaxDValue(int valueid, double maxd) {
		if(valueid <= 0 || valueid > valuearray.length) {
			System.out.println("  Frame: valueid " + valueid + " is out of range.");
			return;
		}
		FrameDouble fdouble = (FrameDouble)valuearray[valueid - 1];
		fdouble.setMaxDValue(maxd);
	}

	/**
	 * 格納する値の最小値を double 形式で指定する
	 */
	public void setMinDValue(int valueid, double mind) {
		if(valueid <= 0 || valueid > valuearray.length) {
			System.out.println("  Frame: valueid " + valueid + " is out of range.");
			return;
		}
		FrameDouble fdouble = (FrameDouble)valuearray[valueid - 1];
		fdouble.setMinDValue(mind);
	}

	/**
	 * 格納する値の最大値を int 形式で指定する
	 */
	public void setMaxIValue(int valueid, int maxi) {
		if(valueid <= 0 || valueid > valuearray.length) {
			System.out.println("  Frame: valueid " + valueid + " is out of range.");
			return;
		}
		FrameInt fint = (FrameInt)valuearray[valueid - 1];
		fint.setMaxIValue(maxi);
	}

	/**
	 * 格納する値の最小値を int 形式で指定する
	 */
	public void setMinIValue(int valueid, int mini) {
		if(valueid <= 0 || valueid > valuearray.length) {
			System.out.println("  Frame: valueid " + valueid + " is out of range.");
			return;
		}
		FrameInt fint = (FrameInt)valuearray[valueid - 1];
		fint.setMinIValue(mini);
	}

	
	/**
	 * Nodeの色や高さを算出するための値を返す
	 * @param valueid
	 * @param nodeid
	 * @return Nodeの色や高さを算出するための値（原則として0.0～1.0、不正なIDの場合は負値）
	 */
	public double getAppearanceValue(int valueid, int nodeid) {
		double value = -1.0;
		
		switch(tf.getValueType(valueid)) {
		case TreeFrame.FRAME_STRING:
			FrameString fstring = (FrameString)valuearray[valueid - 1];
			value = fstring.getAppearanceValue(nodeid);
			break;
		case TreeFrame.FRAME_DOUBLE:
			FrameDouble fdouble = (FrameDouble)valuearray[valueid - 1];
			value = fdouble.getAppearanceValue(nodeid);
			break;
		case TreeFrame.FRAME_INT:
			FrameInt fint = (FrameInt)valuearray[valueid - 1];
			value = fint.getAppearanceValue(nodeid);
			break;
		}

		return value;
	}

	
}
