package ca.paulshin.yunatube.youtube;

public class Clip {
	private String cid, ctitle, sid, stitle, yid, ytitle, ytid;

	public Clip(String cid, String ctitle, String sid, String stitle, String yid, String ytitle, String ytid) {
		this.cid = cid;
		this.ctitle = ctitle;
		this.sid = sid;
		this.stitle = stitle;
		this.yid = yid;
		this.ytitle = ytitle;
		this.ytid = ytid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCtitle() {
		return ctitle;
	}

	public void setCtitle(String ctitle) {
		this.ctitle = ctitle;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getStitle() {
		return stitle;
	}

	public void setStitle(String stitle) {
		this.stitle = stitle;
	}

	public String getYid() {
		return yid;
	}

	public void setYid(String yid) {
		this.yid = yid;
	}

	public String getYtitle() {
		return ytitle;
	}

	public void setYtitle(String ytitle) {
		this.ytitle = ytitle;
	}

	public String getYtid() {
		return ytid;
	}

	public void setYtid(String ytid) {
		this.ytid = ytid;
	}
}
