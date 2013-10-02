package org.kesler.simplereg.logic.reception;

import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import java.sql.SQLException;

import org.kesler.simplereg.dao.DAOFactory;
import org.kesler.simplereg.util.OptionsUtil;


public class ReceptionsModel {
	private static ReceptionsModel instance = null;
	private List<Reception> receptions;

	private ReceptionsModel() {
		receptions = new ArrayList<Reception>();
	}

	public static synchronized ReceptionsModel getInstance() {
		if (instance == null) {
			instance = new ReceptionsModel();
		}
		return instance;
	}


	public List<Reception> getReceptions() {
		return receptions;
	}

	// читаем данные из БД
	public void readReceptionsFromDB() {
		try {
			receptions = DAOFactory.getInstance().getReceptionDAO().getAllReceptions();
		} catch (SQLException sqle) {
			JOptionPane.showMessageDialog(null,sqle.getMessage(),"Ошибка чтения из базы данных", JOptionPane.OK_OPTION);
		}		
	}

	public void addReception(Reception reception) {
		reception.setStatus(ReceptionStatusesModel.getInstance().getInitReceptionStatus());

		try {
			//DAOFactory.getInstance().getApplicatorDAO().addApplicator(reception.getApplicator());
			DAOFactory.getInstance().getReceptionDAO().addReception(reception);
			receptions.add(reception);
		} catch (SQLException sqle) {
			JOptionPane.showMessageDialog(null,sqle.getMessage(),"Ошибка записи в базу данных", JOptionPane.OK_OPTION);
		}
	}

}
