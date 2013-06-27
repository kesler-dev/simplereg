package org.kesler.simplereg;

import java.sql.SQLException;
import java.util.List;
import java.util.Date;

import org.kesler.simplereg.logic.Service;
import org.kesler.simplereg.logic.Applicator;
import org.kesler.simplereg.logic.Operator;
import org.kesler.simplereg.logic.Reception;

import org.kesler.simplereg.dao.DAOFactory;
import org.kesler.simplereg.dao.ReceptionDAO;

public class SimpleReg {

	public static void main(String[] args) {
		Service s1 = new Service();
		Service s2 = new Service();
		Service s21 = new Service();

		s1.setName("Service # 1");
		s2.setName("Service # 2");
		s21.setName("Service # 21");
		s21.setParentService(s2);

		System.out.println("Writing Services....");
		try {
			DAOFactory.getInstance().getServiceDAO().addService(s1);
			DAOFactory.getInstance().getServiceDAO().addService(s2);
			DAOFactory.getInstance().getServiceDAO().addService(s21);

		} catch (SQLException e) {
			System.out.println("DB Error: " + e.getMessage());
		}


		Applicator a1 = new Applicator("Вова Иванов");

		System.out.println("Writing Applicator....");
		try {
			DAOFactory.getInstance().getApplicatorDAO().addApplicator(a1);

		} catch (SQLException e) {
			System.out.println("DB Error: " + e.getMessage());
		}


		Operator o1 = new Operator("Операторов Оператор");

		System.out.println("Writing Operator....");
		try {
			DAOFactory.getInstance().getOperatorDAO().addOperator(o1);

		} catch (SQLException e) {
			System.out.println("DB Error: " + e.getMessage());
		}


		Reception r1 = new Reception(s21,a1,o1,new Date());

		Reception resultReception = null;

		System.out.println("Writing Reception....");
		try {
			DAOFactory.getInstance().getReceptionDAO().addReception(r1);

		} catch (SQLException e) {
			System.out.println("DB Error: " + e.getMessage());
		}

		Long id = r1.getId();

		System.out.println("Reading Reception # " + id);
		try {
			resultReception = DAOFactory.getInstance().getReceptionDAO().getReceptionById(id);
		} catch (SQLException e) {
			System.out.println("DB Error: " + e.getMessage());
		}


		System.out.println("================Результат================");

		if (resultReception != null) {
			System.out.println("Прием № "+ resultReception.getId()); 
			System.out.println("Услуга: " + resultReception.getService().getName()); 
			System.out.println("Заявитель: " + resultReception.getApplicator().getFIO() +
								"\nОператор: " + resultReception.getOperator().getFIO() +
								"\nДата: " + resultReception.getOpenDate());												
			} else {
			System.out.println("Error retriving data...");
		}

	}

}