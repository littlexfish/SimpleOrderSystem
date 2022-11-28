package edu.nptu.dllab.sos.util

enum class TimeFormat(val display: String, val format: String) {
	M2D2Y2("m2d2y2", "MM/dd/yy"),
	M2D2Y2_HM("m2d2y2_hm", "MM/dd/yy HH:mm"),
	Y2M2D2("y2m2d2", "yy/MM/dd"),
	Y2M2D2_HM("y2m2d2_hm", "yy/MM/dd HH:mm"),
	Y4M2D2("y4m2d2", "yyyy/MM/dd"),
	Y4M2D2_HM("y4m2d2_hm", "yyyy/MM/dd HH:mm"),
	M2D2Y4("m2d2y4", "MM/dd/yy"),
	M2D2Y4_HM("m2d2y4_hm", "MM/dd/yyyy HH:mm"),
	D2M2Y4("d2m2y4", "dd/MM/yyyy"),
	D2M2Y4_HM("d2m2y4_hm", "dd/MM/yyyy HH:mm"),
	
}