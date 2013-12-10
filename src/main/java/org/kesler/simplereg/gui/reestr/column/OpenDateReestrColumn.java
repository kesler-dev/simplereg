package org.kesler.simplereg.gui.reestr.column;

import java.text.SimpleDateFormat;

import org.kesler.simplereg.logic.Reception;

public class OpenDateReestrColumn extends ReestrColumn {

	public OpenDateReestrColumn() {
		name = "Создан";
		alias = "openDate";
		width = 17;
	}

	public String getValue(Reception reception) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

		return dateFormat.format(reception.getOpenDate());
	}
}